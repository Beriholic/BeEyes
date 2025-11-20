package cv.beriholic.beeyes.service.impl;

import cv.beriholic.beeyes.consts.KafkaGroup;
import cv.beriholic.beeyes.consts.KafkaTopic;
import cv.beriholic.beeyes.models.dto.MachineRuntimeInfoDTO;
import cv.beriholic.beeyes.models.dto.MessageEntity;
import cv.beriholic.beeyes.repository.MetricDataRepository;
import cv.beriholic.beeyes.service.MetricService;
import cv.beriholic.beeyes.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class MetricServiceImpl implements MetricService {
    private final MetricDataRepository metricDataRepository;

    @Override
    @KafkaListener(topics = KafkaTopic.MACHINE_RUNTIME_METRIC, groupId = KafkaGroup.MACHINE_RUNTIME_INFO_GROUP)
    public void recordMachineRuntimeInfo(MessageEntity message) {
        log.info("[MetricService.recordMachineRuntimeInfo] message={}", JsonUtil.toJSONString(message));
        try {
            MachineRuntimeInfoDTO machineRuntimeInfoDTO = JsonUtil.parseObject(message.getContent(), MachineRuntimeInfoDTO.class);
            metricDataRepository.recordRuntimeInfo(machineRuntimeInfoDTO);
        } catch (Exception e) {
            log.error("[MetricService.recordMachineRuntimeInfo] error}", e);
        }
    }
}
