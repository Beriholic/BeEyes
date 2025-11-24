package cv.beriholic.beeyes.mq;

import cv.beriholic.beeyes.consts.BusinessId;
import cv.beriholic.beeyes.consts.KafkaTopic;
import cv.beriholic.beeyes.models.dto.MachineRuntimeInfoDTO;
import cv.beriholic.beeyes.models.dto.MessageEntity;
import cv.beriholic.beeyes.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
public class MetricRecordBaseProducerService extends BaseProducerService {
    public void pushMachineMetricData(MachineRuntimeInfoDTO dto) {
        log.info("[pushMachineMetricData] biz start, machineRuntimeInfoDTO={}", JsonUtil.toJSONString(dto));
        if (Objects.isNull(dto)) {
            return;
        }
        MessageEntity message = new MessageEntity(
                BusinessId.ReportMachineRuntimeInfo.buildKey(dto.getServerId()),
                JsonUtil.toJSONString(dto)
        );
        sendMessage(KafkaTopic.MACHINE_RUNTIME_METRIC, message);
    }
}
