package cv.beriholic.beeyes.service.impl;

import cv.beriholic.beeyes.consts.AlertCondition;
import cv.beriholic.beeyes.consts.AlertMetricType;
import cv.beriholic.beeyes.consts.AlertStatus;
import cv.beriholic.beeyes.models.dto.MachineRuntimeInfoDTO;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.dto.ServerStatusDTO;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertLogDODraft;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.models.entity.dto.*;
import cv.beriholic.beeyes.repository.AlertLogRepository;
import cv.beriholic.beeyes.repository.AlertRuleRepository;
import cv.beriholic.beeyes.repository.ServersRepository;
import cv.beriholic.beeyes.service.AlertExecutionLock;
import cv.beriholic.beeyes.service.AlertService;
import cv.beriholic.beeyes.service.MetricService;
import cv.beriholic.beeyes.service.alert.MetricSnapshotService;
import cv.beriholic.beeyes.service.notification.AsyncNotificationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRuleRepository alertRuleRepository;
    private final AlertLogRepository alertLogRepository;
    private final ServersRepository serversRepository;
    private final AsyncNotificationService asyncNotificationService;
    private final MetricService metricService;
    private final AlertExecutionLock alertExecutionLock;
    private final MetricSnapshotService metricSnapshotService;

    @Override
    public void createRule(CreateAlertRequest request) {
        SaveAlertInput input = new SaveAlertInput();
        input.setName(request.getName());
        input.setServerId(request.getServerId() != null ? Long.valueOf(request.getServerId()) : null);
        input.setMetricType(request.getMetricType());
        input.setCondition(request.getCondition());
        input.setThreshold(request.getThreshold());
        input.setDurationSeconds(request.getDurationSeconds());
        input.setSilenceSeconds(request.getSilenceSeconds());
        input.setEnabled(request.isEnabled());
        alertRuleRepository.save(input, SaveMode.INSERT_ONLY);
    }

    @Override
    public void updateRule(UpdateAlertRequest request) {
        UpdateAlertInput input = new UpdateAlertInput();
        input.setId(Long.valueOf(request.getId()));
        input.setName(request.getName());
        input.setServerId(
                StringUtils.isEmpty(request.getServerId()) ? null : Long.valueOf(request.getServerId())
        );
        input.setMetricType(request.getMetricType());
        input.setCondition(request.getCondition());
        input.setThreshold(request.getThreshold());
        input.setDurationSeconds(request.getDurationSeconds());
        input.setSilenceSeconds(request.getSilenceSeconds());
        input.setEnabled(request.isEnabled());
        alertRuleRepository.save(input, SaveMode.UPSERT);
    }

    @Override
    public void deleteRule(DeleteAlertRequest request) {
        alertRuleRepository.deleteById(Long.valueOf(request.getId()));
    }

    @Override
    public List<AlertRuleDO> getRules() {
        return alertRuleRepository.findAll();
    }

    @Override
    public PageDTO<List<AlertRuleDO>> getRulesPage(QueryAlertRuleRequest request) {
        var page = alertRuleRepository.findRulesPage(request);
        return PageDTO.of(page.getRows(), request.getPageIndex(), request.getPageSize(), page.getTotalRowCount(), page.getTotalPageCount());
    }

    @Override
    public PageDTO<List<AlertLogDO>> getAlertLogs(QueryAlertLogRequest request) {
        var page = alertLogRepository.findAlterLogByPage(request);
        return PageDTO.of(page.getRows(), request.getPageIndex(), request.getPageSize(), page.getTotalRowCount(), page.getTotalPageCount());
    }

    @Override
    public Map<String, Long> getAlertStats() {
        Map<String, Long> stats = new HashMap<>();
        long triggeringCount = alertLogRepository.countByStatus(AlertStatus.TRIGGERING.getKey());
        long resolvedCount = alertLogRepository.countByStatus(AlertStatus.RESOLVED.getKey());
        stats.put("triggering", triggeringCount);
        stats.put("resolved", resolvedCount);
        return stats;
    }

    @Override
    public void checkAllAlerts() {
        List<AlertRuleDO> rules = alertRuleRepository.findEnabledRules();
        List<Long> allServerIds = serversRepository.getAllIds();

        for (AlertRuleDO rule : rules) {
            if (rule.serverId() != null) {
                checkRuleForServer(rule, rule.serverId());
            } else {
                for (Long serverId : allServerIds) {
                    checkRuleForServer(rule, serverId);
                }
            }
        }
    }

    @Override
    public void triggerManualCheck() {
        String executionId = alertExecutionLock.tryAcquire();
        if (executionId == null) {
            log.warn("[AlertService] Skipping manual check - another execution is in progress");
            return;
        }
        try {
            log.info("[AlertService] Manual alert check triggered");
            checkAllAlerts();
        } finally {
            alertExecutionLock.release(executionId);
        }
    }

    private void checkRuleForServer(AlertRuleDO rule, Long serverId) {
        double metricValue;

        // For STATUS metric, get server status from database instead of runtime info
        if (rule.metricType() == AlertMetricType.STATUS.getKey()) {
            ServerStatusDTO serverStatus = serversRepository.getStatus(serverId);
            metricValue = serverStatus.getCurrentStatus();
        } else {
            RuntimeInfo runtimeInfo = metricService.getMachineRuntimeInfoById(serverId);
            if (runtimeInfo == null) {
                return;
            }
            MachineRuntimeInfoDTO dto = MachineRuntimeInfoDTO.from(serverId, runtimeInfo);
            metricValue = getMetricValue(rule.metricType(), dto);
        }

        // Record metric value for duration tracking
        LocalDateTime now = LocalDateTime.now();
        metricSnapshotService.recordMetric(rule.id(), serverId, metricValue, now);

        boolean conditionMet = checkCondition(metricValue, rule.condition(), rule.threshold());

        // Check if duration requirement is satisfied
        long durationSeconds = rule.durationSeconds() != null ? rule.durationSeconds() : 0;
        boolean durationSatisfied = metricSnapshotService.hasMetDuration(
                rule.id(), serverId, durationSeconds, rule.threshold(), conditionMet);

        if (conditionMet && durationSatisfied) {
            handleTriggeredAlert(rule, serverId, metricValue);
        } else if (!conditionMet) {
            // Condition no longer met, clear any duration tracking
            metricSnapshotService.clear(rule.id(), serverId);
            handleResolvedAlert(rule, serverId, metricValue);
        } else {
            // Condition met but duration not satisfied - log for debugging
            log.debug("Alert condition met but duration not satisfied: rule={}, server={}, " +
                            "duration={}s, metric={}, threshold={}",
                    rule.name(), serverId, durationSeconds, metricValue, rule.threshold());
        }
    }

    private double getMetricValue(int metricTypeInt, MachineRuntimeInfoDTO info) {
        AlertMetricType type = AlertMetricType.of(metricTypeInt);
        if (type == null)
            return -1;

        return switch (type) {
            case CPU -> info.getCpuUsage() != null ? info.getCpuUsage() : 0.0;
            case MEMORY -> info.getMemoryUsage() != null ? info.getMemoryUsage() : 0.0;
            case DISK -> info.getDiskUsage() != null ? info.getDiskUsage() : 0.0;
            default -> 0.0;
        };
    }

    private boolean checkCondition(double value, int conditionInt, double threshold) {
        AlertCondition cond = AlertCondition.of(conditionInt);
        if (cond == null)
            return false;

        return switch (cond) {
            case GT -> value > threshold;
            case LT -> value < threshold;
            case EQ -> Math.abs(value - threshold) < 0.0001;
        };
    }

    private void handleTriggeredAlert(AlertRuleDO rule, Long serverId, double currentValue) {
        List<AlertLogDO> existingLogs = alertLogRepository.findUnresolvedLogsNested(serverId, rule.id(),
                AlertStatus.TRIGGERING.getKey());

        if (!existingLogs.isEmpty()) {
            AlertLogDO existing = existingLogs.getFirst();

            long silenceSeconds = rule.silenceSeconds();
            if (silenceSeconds > 0) {
                LocalDateTime lastNotificationTime = existing.updatedAt() != null ? existing.updatedAt()
                        : existing.startedAt();
                long secondsSinceLast = 0;
                if (lastNotificationTime != null) {
                    secondsSinceLast = Duration.between(lastNotificationTime, LocalDateTime.now()).getSeconds();
                }

                if (secondsSinceLast < silenceSeconds) {
                    return;
                }
            }

            AlertLogDO updated = AlertLogDODraft.$.produce(existing, draft -> {
                draft.setMetricValue(currentValue);
                draft.setUpdatedAt(LocalDateTime.now());
            });
            alertLogRepository.save(updated, SaveMode.UPSERT);
            log.warn("Alert Re-Triggered (Silence Period Over): Rule={}, Server={}, Value={}", rule.name(), serverId,
                    currentValue);
            asyncNotificationService.notifyAsync(updated, rule);
            return;
        }

        AlertLogDO logEntry = AlertLogDODraft.$.produce(draft -> {
            draft.setRuleId(rule.id());
            draft.setServerId(serverId);
            draft.setMetricValue(currentValue);
            draft.setMessage("Alert triggered: " + rule.name() + " value: " + currentValue);
            draft.setStatus(AlertStatus.TRIGGERING.getKey());
            draft.setStartedAt(LocalDateTime.now());
        });
        var saveResult = alertLogRepository.save(logEntry, SaveMode.INSERT_ONLY);
        // Get the saved entity with all fields loaded including ID
        AlertLogDO savedLog = alertLogRepository.findById(saveResult.getModifiedEntity().id());
        log.warn("Alert Triggered: Rule={}, Server={}, Value={}", rule.name(), serverId, currentValue);

        asyncNotificationService.notifyAsync(savedLog, rule);
    }

    private void handleResolvedAlert(AlertRuleDO rule, Long serverId, double currentValue) {
        List<AlertLogDO> existingLogs = alertLogRepository.findUnresolvedLogsNested(serverId, rule.id(),
                AlertStatus.TRIGGERING.getKey());

        if (existingLogs.isEmpty()) {
            return;
        }

        for (AlertLogDO existing : existingLogs) {
            AlertLogDO updated = AlertLogDODraft.$.produce(existing, draft -> {
                draft.setStatus(AlertStatus.RESOLVED.getKey());
                draft.setResolvedAt(LocalDateTime.now());
            });
            alertLogRepository.save(updated, SaveMode.UPSERT);
            log.info("Alert Resolved: Rule={}, Server={}", rule.name(), serverId);
            asyncNotificationService.notifyAsync(updated, rule);
        }
    }

}
