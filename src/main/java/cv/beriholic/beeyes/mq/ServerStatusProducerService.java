package cv.beriholic.beeyes.mq;

import cv.beriholic.beeyes.consts.BusinessId;
import cv.beriholic.beeyes.consts.KafkaTopic;
import cv.beriholic.beeyes.models.dto.MessageEntity;
import cv.beriholic.beeyes.models.dto.ServerStatusUpdatedDTO;
import cv.beriholic.beeyes.utils.JsonUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Slf4j
@Service
public class ServerStatusProducerService extends BaseProducerService {
    public void pushUpdateServerStatus(ServerStatusUpdatedDTO dto) {
        log.info("[pushUpdateServerStatus] biz start, serverStatus={}", JsonUtil.toJSONString(dto));
        if (Objects.isNull(dto)) {
            return;
        }
        MessageEntity message = new MessageEntity(
                BusinessId.ServerStatusUpdated.buildKey(dto.getId()),
                JsonUtil.toJSONString(dto)
        );
        sendMessage(KafkaTopic.SERVER_STATUS_UPDATED, message);
    }
}
