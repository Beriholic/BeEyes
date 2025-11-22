package cv.beriholic.beeyes.service.impl;

import cv.beriholic.beeyes.consts.CacheKey;
import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.models.dto.ServerStatusDTO;
import cv.beriholic.beeyes.repository.ServersRepository;
import cv.beriholic.beeyes.service.MachineStatusService;
import cv.beriholic.beeyes.utils.AsyncUtils;
import cv.beriholic.beeyes.utils.JsonUtil;
import cv.beriholic.beeyes.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class MachineStatusServiceImpl implements MachineStatusService {
    private final ServersRepository serversRepository;
    private final RedisUtils redisUtils;
    private final Executor virtualThreadExecutor = Executors.newVirtualThreadPerTaskExecutor();


    @Override
    public void setServerStatus(Long id, ServerStatus status) {
        ServerStatusDTO statusDTO = getServerStatus(id);
        if (statusDTO.isStatusUpdated(status)) {
            return;
        }
        statusDTO.updateStatus(status);
        redisUtils.set(CacheKey.machineStatus(id), JsonUtil.toJSONString(statusDTO));
        asyncStatusSyncToDB(id, status);
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

    private void asyncStatusSyncToDB(Long id, ServerStatus status) {
        CompletableFuture.runAsync(() -> {
                    AsyncUtils.withRetry(
                            () -> serversRepository.updateStatus(id, status),
                            3,
                            1000L
                    );
                },
                virtualThreadExecutor
        );
    }


    @Scheduled(fixedDelay = 30000)
    public void syncServerStatus() {
        List<Long> allIds = serversRepository.getAllIds();
        allIds.forEach(id -> {
            ServerStatusDTO serverStatus = getServerStatus(id);
            if (Objects.equals(serverStatus.getCurrentStatus(), ServerStatus.ONLINE.getKey())
                    && serverStatus.isNotUpdatedForHalfMinute()
            ) {
                setServerStatus(id, ServerStatus.OFFLINE);
            }
        });
    }
}

