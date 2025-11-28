package cv.beriholic.beeyes.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cv.beriholic.beeyes.cache.CacheKey;
import cv.beriholic.beeyes.cache.RedisUtils;
import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.converter.ServerMachineConverter;
import cv.beriholic.beeyes.models.dto.ServerStatusDTO;
import cv.beriholic.beeyes.models.dto.system.MachineInfo;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import cv.beriholic.beeyes.models.entity.ServerDiskDO;
import cv.beriholic.beeyes.models.entity.ServerNetworkInterfacesDO;
import cv.beriholic.beeyes.models.entity.ServersDO;
import cv.beriholic.beeyes.models.entity.dto.SaveServerInput;
import cv.beriholic.beeyes.models.entity.dto.ServerAllScaleView;
import cv.beriholic.beeyes.repository.ServerDiskRepository;
import cv.beriholic.beeyes.repository.ServerNetworkInterfaceRepository;
import cv.beriholic.beeyes.repository.ServersRepository;
import cv.beriholic.beeyes.service.ClientService;
import cv.beriholic.beeyes.service.MachineService;
import cv.beriholic.beeyes.service.MachineStatusService;
import cv.beriholic.beeyes.service.MetricService;
import cv.beriholic.beeyes.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@Service
public class ClientServiceImpl implements ClientService {
    private final ServersRepository serversRepository;
    private final ServerDiskRepository serverDiskRepository;
    private final RedisUtils redisUtils;
    private final MetricService metricService;
    private final ServerNetworkInterfaceRepository serverNetworkInterfaceRepository;
    private final MachineStatusService machineStatusService;
    private final MachineService machineService;

    @Override
    public Long getIdByTokenWithCache(String token) {
        log.info("[getIdByTokenWithCache] biz start, token={}", token);
        if (StringUtils.isEmpty(token)) {
            throw new IllegalArgumentException("client token为空");
        }
        String cacheKey = CacheKey.machineIdToken(token);
        if (redisUtils.hasKey(cacheKey)) {
            return Long.valueOf(redisUtils.get(cacheKey));
        }
        Long id = serversRepository.getIdByApiKey(token);
        serversRepository.updateStatus(id, ServerStatus.REGISTER);
        if (Objects.nonNull(id)) {
            redisUtils.set(cacheKey, id.toString(), 1, TimeUnit.DAYS);
        }
        return id;
    }

    @Override
    public boolean verifyAndRegister(String token) {
        log.info("[verifyAndRegister] biz start, token={}", token);
        return Objects.nonNull(getIdByTokenWithCache(token));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportMachineInfo(Long machineId, MachineInfo machineInfo) {
        log.info("[reportMachineInfo] biz start, machineId={}, machineInfo={}", machineId, JsonUtil.toJSONString(machineInfo));
        ServersDO serversDO = serversRepository.findById(machineId, ServerAllScaleView.METADATA.getFetcher());
        SaveServerInput saveServerInput = new SaveServerInput();

        ServerStatusDTO serverStatus = machineStatusService.getServerStatus(machineId);
        if (ServerStatus.UNREGISTER.getKey().equals(serverStatus.getCurrentStatus())
                || ServerStatus.UNKNOW.getKey().equals(serverStatus.getCurrentStatus())
        ) {
            saveServerInput.setStatus(ServerStatus.REGISTER.getKey());
            machineStatusService.setServerStatus(machineId, ServerStatus.REGISTER);
        } else {
            saveServerInput.setStatus(serverStatus.getCurrentStatus());
        }

        saveServerInput.setId(machineId);
        ServerMachineConverter.buildSystemInfo(saveServerInput, machineInfo);
        saveServerInput.setHardware(ServerMachineConverter.buildHardware(machineInfo, serversDO));
        saveServerInput.setDisks(ServerMachineConverter.buildDisk(machineInfo, serversDO));
        saveServerInput.setNetworkInterfaces(ServerMachineConverter.buildNetworkInterface(machineInfo, serversDO));

        // 清理旧数据
        cleanOldMachineReportedData(machineId, serversDO, saveServerInput);
        // 更新
        serversRepository.save(saveServerInput, SaveMode.UPSERT);
    }

    private void cleanOldMachineReportedData(Long machineId, ServersDO serversDO, SaveServerInput saveServerInput) {
        // disk
        Set<Long> oldIds = Sets.newHashSet();
        if (Objects.nonNull(serversDO) && Objects.nonNull(serversDO.disks())) {
            oldIds = serversDO.disks().stream().map(ServerDiskDO::id).collect(Collectors.toSet());
        }
        Set<Long> newIds = saveServerInput.getDisks().stream().map(SaveServerInput.TargetOf_disks::getId).collect(Collectors.toSet());
        List<Long> diffIds = Lists.newArrayList(Sets.difference(oldIds, newIds));
        serverDiskRepository.deleteByIds(diffIds);

        // network
        oldIds.clear();
        if (Objects.nonNull(serversDO) && Objects.nonNull(serversDO.networkInterfaces())) {
            oldIds = serversDO.networkInterfaces().stream().map(ServerNetworkInterfacesDO::id).collect(Collectors.toSet());
        }
        newIds = saveServerInput.getNetworkInterfaces().stream().map(SaveServerInput.TargetOf_networkInterfaces::getId).collect(Collectors.toSet());
        diffIds = Lists.newArrayList(Sets.difference(oldIds, newIds));
        serverNetworkInterfaceRepository.deleteByIds(diffIds);

        // cache
        machineService.deleteUserServerCacheByServerId(machineId);
    }

    @Override
    public void reportRuntimeInfo(Long machineId, RuntimeInfo runtimeInfo) {
        metricService.saveRuntimeInfo(machineId, runtimeInfo);
    }
}
