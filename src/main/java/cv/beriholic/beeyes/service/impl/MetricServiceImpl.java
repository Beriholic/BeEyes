package cv.beriholic.beeyes.service.impl;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import cv.beriholic.beeyes.consts.HistoryTimeUnit;
import cv.beriholic.beeyes.consts.KafkaGroup;
import cv.beriholic.beeyes.consts.KafkaTopic;
import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.exception.BizRuntimeException;
import cv.beriholic.beeyes.exception.ErrorCode;
import cv.beriholic.beeyes.models.dto.MachineRuntimeInfoDTO;
import cv.beriholic.beeyes.models.dto.MessageEntity;
import cv.beriholic.beeyes.models.dto.RuntimeInfoDTO;
import cv.beriholic.beeyes.models.dto.ServerStatusDTO;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import cv.beriholic.beeyes.models.entity.dto.QueryMachineRuntimeHistoryRequest;
import cv.beriholic.beeyes.models.entity.dto.QueryMachineRuntimeInfoRequest;
import cv.beriholic.beeyes.mq.MetricRecordBaseProducerService;
import cv.beriholic.beeyes.repository.MetricDataRepository;
import cv.beriholic.beeyes.service.MachineService;
import cv.beriholic.beeyes.service.MachineStatusService;
import cv.beriholic.beeyes.service.MetricService;
import cv.beriholic.beeyes.utils.JsonUtil;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricServiceImpl implements MetricService {
    private final MetricDataRepository metricDataRepository;
    private final MetricRecordBaseProducerService metricRecordProducerService;
    private final MachineStatusService machineStatusService;
    private final MachineService machineService;

    private final Cache<@NonNull Long, RuntimeInfo> runtimeInfoCache = Caffeine.newBuilder()
            .maximumSize(1000)
            .expireAfterWrite(10, TimeUnit.SECONDS)
            .build();

    @KafkaListener(topics = KafkaTopic.MACHINE_RUNTIME_METRIC, groupId = KafkaGroup.MACHINE_RUNTIME_INFO_GROUP)
    public void recordMachineRuntimeInfo(MessageEntity message) {
        log.info("[recordMachineRuntimeInfo] message={}", JsonUtil.toJSONString(message));
        try {
            MachineRuntimeInfoDTO machineRuntimeInfoDTO = JsonUtil.parseObject(message.getContent(), MachineRuntimeInfoDTO.class);
            metricDataRepository.recordRuntimeInfo(machineRuntimeInfoDTO);
        } catch (Exception e) {
            log.error("[recordMachineRuntimeInfo] error", e);
        }
    }

    @Override
    public RuntimeInfo getMachineRuntimeInfoById(Long id) {
        log.info("[getMachineRuntimeInfoById] biz start id={}", id);
        if (Objects.isNull(id)) {
            return null;
        }
        return runtimeInfoCache.get(id, key -> null);
    }

    @Override
    @Transactional
    public void saveRuntimeInfo(Long machineId, RuntimeInfo runtimeInfo) {
        if (Objects.isNull(machineId)) {
            return;
        }
        log.info("[saveRuntimeInfo] biz start, machineId={}, runtimeInfo={}", machineId, JsonUtil.toJSONString(runtimeInfo));
        runtimeInfoCache.put(machineId, runtimeInfo);
        MachineRuntimeInfoDTO runtimeInfoDTO = MachineRuntimeInfoDTO.from(machineId, runtimeInfo);

        ServerStatusDTO oldStatus = machineStatusService.getServerStatus(machineId);
        machineStatusService.setServerStatus(machineId, oldStatus, ServerStatus.ONLINE);

        metricRecordProducerService.pushMachineMetricData(runtimeInfoDTO);
    }

    @Override
    public List<RuntimeInfoDTO> queryMachineRuntimeInfo(Long userId, QueryMachineRuntimeInfoRequest request) {
        List<Long> serverIds = machineService.getUserServerIdListByCache(
                request.getPageIndex(), request.getPageSize()
        );

        return serverIds.stream()
                .map(id -> {
                            RuntimeInfo runtimeInfo = runtimeInfoCache.get(id, key -> null);
                            if (Objects.isNull(runtimeInfo)) {
                                return null;
                            }
                            ServerStatusDTO serverStatus = machineStatusService.getServerStatus(id);
                            return new RuntimeInfoDTO(String.valueOf(id), serverStatus.getCurrentStatus(), runtimeInfo);
                        }
                )
                .filter(Objects::nonNull)
                .toList();
    }

    @Override
    public List<MachineRuntimeInfoDTO> queryMachineRuntimeHistory(Long userId, QueryMachineRuntimeHistoryRequest request) {
        Long serverId = Long.valueOf(request.getMachineId());
        boolean isValid = machineService.userHasServer(userId, serverId);
        if (!isValid) {
            throw new BizRuntimeException(ErrorCode.UNAUTHORIZED);
        }
        return metricDataRepository.queryHistoricalRuntimeInfo(
                serverId,
                request.getTime(),
                HistoryTimeUnit.of(request.getTimeUnit())
        );
    }
}

