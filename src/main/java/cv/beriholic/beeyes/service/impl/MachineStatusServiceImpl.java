package cv.beriholic.beeyes.service.impl;

import cv.beriholic.beeyes.cache.CacheKey;
import cv.beriholic.beeyes.cache.RedisUtils;
import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.models.dto.ServerStatusDTO;
import cv.beriholic.beeyes.models.dto.ServerStatusUpdatedDTO;
import cv.beriholic.beeyes.mq.ServerStatusProducerService;
import cv.beriholic.beeyes.repository.ServersRepository;
import cv.beriholic.beeyes.service.MachineStatusService;
import cv.beriholic.beeyes.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
@RequiredArgsConstructor
public class MachineStatusServiceImpl implements MachineStatusService {
    private final ServerStatusProducerService serverStatusProducerService;
    private final ServersRepository serversRepository;
    private final RedisUtils redisUtils;


    @Override
    public void setServerStatus(Long id, ServerStatusDTO oldStatus, ServerStatus newStatus) {
        if (!Objects.equals(oldStatus.getCurrentStatus(), newStatus.getKey())) {
            ServerStatusDTO statusDTO = ServerStatusDTO.of(newStatus);
            redisUtils.set(CacheKey.machineStatus(id), JsonUtil.toJSONString(statusDTO), 1, TimeUnit.HOURS);
            // 同步更新数据库，确保 AlertJob 能立即读到最新状态
            serversRepository.updateStatus(id, newStatus);
            ServerStatusUpdatedDTO updatedStatus = new ServerStatusUpdatedDTO(id, newStatus.getKey());
            serverStatusProducerService.pushUpdateServerStatus(updatedStatus);
        }
    }

    @Override
    public void setServerStatus(Long id, ServerStatus status) {
        ServerStatusDTO statusDTO = ServerStatusDTO.of(status);
        redisUtils.set(CacheKey.machineStatus(id), JsonUtil.toJSONString(statusDTO), 1, TimeUnit.HOURS);
        serversRepository.updateStatus(id, status);
    }

    @Override
    public ServerStatusDTO getServerStatus(Long id) {
        String statusJson = redisUtils.get(CacheKey.machineStatus(id));
        ServerStatusDTO serverStatusDTO;
        if (StringUtils.isEmpty(statusJson)) {
            serverStatusDTO = serversRepository.getStatus(id);
            redisUtils.set(CacheKey.machineStatus(id), JsonUtil.toJSONString(serverStatusDTO));
        } else {
            serverStatusDTO = JsonUtil.parseObject(statusJson, ServerStatusDTO.class);
        }
        return serverStatusDTO;
    }

    @Scheduled(fixedDelay = 30000)
    public void syncServerStatus() {
        List<Long> allIds = serversRepository.getAllIds();
        allIds.forEach(id -> {
            ServerStatusDTO oldStatus = getServerStatus(id);
            if (Objects.equals(oldStatus.getCurrentStatus(), ServerStatus.ONLINE.getKey())
                    && oldStatus.isNotUpdatedForHalfMinute()
            ) {
                setServerStatus(id, oldStatus, ServerStatus.OFFLINE);
            }
        });
    }
}

