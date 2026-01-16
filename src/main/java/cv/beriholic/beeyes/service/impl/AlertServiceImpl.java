package cv.beriholic.beeyes.service.impl;

import cv.beriholic.beeyes.consts.AlertCondition;
import cv.beriholic.beeyes.consts.AlertMetricType;
import cv.beriholic.beeyes.consts.AlertStatus;
import cv.beriholic.beeyes.models.dto.MachineRuntimeInfoDTO;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertLogDODraft;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.models.entity.dto.*;
import cv.beriholic.beeyes.repository.AlertLogRepository;
import cv.beriholic.beeyes.repository.AlertRuleRepository;
import cv.beriholic.beeyes.repository.ServersRepository;
import cv.beriholic.beeyes.service.AlertService;
import cv.beriholic.beeyes.service.MetricService;
import cv.beriholic.beeyes.service.notification.NotificationChannel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
@Slf4j
public class AlertServiceImpl implements AlertService {

    private final AlertRuleRepository alertRuleRepository;
    private final AlertLogRepository alertLogRepository;
    private final ServersRepository serversRepository;
    private final List<NotificationChannel> notificationChannels;
    private final MetricService metricService;

    @Override
    public void createRule(CreateAlertRequest request) {
        SaveAlertInput input = new SaveAlertInput();
        input.setName(request.getName());
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
        input.setServerId(Long.valueOf(request.getServerId()));
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
    public List<AlertLogDO> getAlertLogs(QueryAlertLogRequest request) {
        return alertLogRepository.findAlterLogByPage(request.getPageIndex(), request.getPageSize());
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
        log.info("[AlertService] Manual alert check triggered");
        checkAllAlerts();
    }

    private void checkRuleForServer(AlertRuleDO rule, Long serverId) {
        RuntimeInfo runtimeInfo = metricService.getMachineRuntimeInfoById(serverId);
        if (runtimeInfo == null) {
            return;
        }

        MachineRuntimeInfoDTO dto = MachineRuntimeInfoDTO.from(serverId, runtimeInfo);

        double metricValue = getMetricValue(rule.metricType(), dto);
        boolean isTriggered = checkCondition(metricValue, rule.condition(), rule.threshold());

        if (isTriggered) {
            handleTriggeredAlert(rule, serverId, metricValue);
        } else {
            handleResolvedAlert(rule, serverId, metricValue);
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
            notifyChannels(updated, rule);
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
        alertLogRepository.save(logEntry, SaveMode.UPSERT);
        log.warn("Alert Triggered: Rule={}, Server={}, Value={}", rule.name(), serverId, currentValue);

        notifyChannels(logEntry, rule);
    }

    private void notifyChannels(AlertLogDO logEntry, AlertRuleDO rule) {
        for (NotificationChannel channel : notificationChannels) {
            try {
                channel.notify(logEntry, rule);
            } catch (Exception e) {
                log.error("Failed to notify channel {}", channel.getClass().getSimpleName(), e);
            }
        }
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
        }
    }

}
