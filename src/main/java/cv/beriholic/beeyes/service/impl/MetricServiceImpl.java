package cv.beriholic.beeyes.service.impl;

import cv.beriholic.beeyes.consts.CacheKey;
import cv.beriholic.beeyes.consts.KafkaGroup;
import cv.beriholic.beeyes.consts.KafkaTopic;
import cv.beriholic.beeyes.models.dto.MachineRuntimeInfoDTO;
import cv.beriholic.beeyes.models.dto.MessageEntity;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import cv.beriholic.beeyes.mq.MetricRecordProducerService;
import cv.beriholic.beeyes.repository.MetricDataRepository;
import cv.beriholic.beeyes.service.MetricService;
import cv.beriholic.beeyes.utils.JsonUtil;
import cv.beriholic.beeyes.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricServiceImpl implements MetricService {
    private final MetricDataRepository metricDataRepository;
    private final MetricRecordProducerService metricRecordProducerService;
    private final RedisUtils redisUtils;

    @Override
    @KafkaListener(topics = KafkaTopic.MACHINE_RUNTIME_METRIC, groupId = KafkaGroup.MACHINE_RUNTIME_INFO_GROUP)
    public void recordMachineRuntimeInfo(MessageEntity message) {
        log.info("[recordMachineRuntimeInfo] message={}", JsonUtil.toJSONString(message));
        try {
            MachineRuntimeInfoDTO machineRuntimeInfoDTO = JsonUtil.parseObject(message.getContent(), MachineRuntimeInfoDTO.class);
            metricDataRepository.recordRuntimeInfo(machineRuntimeInfoDTO);
        } catch (Exception e) {
            log.error("[recordMachineRuntimeInfo] error}", e);
        }
    }


    @Override
    public RuntimeInfo getMachineRuntimeInfoById(Long id) {
        log.info("[getMachineRuntimeInfoById] biz start id={}", id);
        String machineRuntimeInfoJson = redisUtils.get(CacheKey.MACHINE_RUNTIME_INFO.getKey(id));
        if (StringUtils.isEmpty(machineRuntimeInfoJson)) {
            return null;
        }
        return JsonUtil.parseObject(machineRuntimeInfoJson, RuntimeInfo.class);
    }

    @Override
    public void saveRuntimeInfo(Long machineId, RuntimeInfo runtimeInfo) {
        log.info("[saveRuntimeInfo] biz start, machineId={}, runtimeInfo={}", machineId, JsonUtil.toJSONString(runtimeInfo));
        redisUtils.set(CacheKey.MACHINE_RUNTIME_INFO.getKey(machineId), JsonUtil.toJSONString(runtimeInfo), 10, TimeUnit.MINUTES);
        MachineRuntimeInfoDTO runtimeInfoDTO = MachineRuntimeInfoDTO.from(runtimeInfo);
        metricRecordProducerService.pushMachineMetricData(runtimeInfoDTO);
    }
}

