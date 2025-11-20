package cv.beriholic.beeyes.service.impl;

import com.google.common.collect.Lists;
import com.google.common.collect.Sets;
import cv.beriholic.beeyes.consts.CacheKey;
import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.converter.ServerMachineConverter;
import cv.beriholic.beeyes.models.dto.MachineRuntimeInfoDTO;
import cv.beriholic.beeyes.models.dto.system.MachineInfo;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import cv.beriholic.beeyes.models.entity.ServerDiskDO;
import cv.beriholic.beeyes.models.entity.ServersDO;
import cv.beriholic.beeyes.models.entity.dto.SaveServerInput;
import cv.beriholic.beeyes.models.entity.dto.ServerAllSacleView;
import cv.beriholic.beeyes.mq.MetricRecordProducerService;
import cv.beriholic.beeyes.repository.ServerDiskRepository;
import cv.beriholic.beeyes.repository.ServersRepository;
import cv.beriholic.beeyes.service.ClientService;
import cv.beriholic.beeyes.utils.JsonUtil;
import cv.beriholic.beeyes.utils.RedisUtils;
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
    private final MetricRecordProducerService metricRecordProducerService;

    @Override
    public Long getIdByTokenWithCache(String token) {
        if (StringUtils.isEmpty(token)) {
            throw new IllegalArgumentException("client token为空");
        }
        String cacheKey = CacheKey.MACHINE_ID_TOKEN.getKey(token);
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
        return Objects.nonNull(getIdByTokenWithCache(token));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void reportMachineInfo(Long machineId, MachineInfo machineInfo) {
        ServersDO serversDO = serversRepository.findById(machineId, ServerAllSacleView.METADATA.getFetcher());
        SaveServerInput saveServerInput = new SaveServerInput();
        saveServerInput.setId(machineId);
        saveServerInput.setHostname(machineInfo.getSystemInfo().getHostName());
        saveServerInput.setHardware(ServerMachineConverter.buildHardware(machineInfo, serversDO));
        saveServerInput.setDisks(ServerMachineConverter.buildDisk(machineInfo, serversDO));

        // 清理旧数据
        Set<Long> oldDiskIds = Sets.newHashSet();
        if (Objects.nonNull(serversDO) && Objects.nonNull(serversDO.disks())) {
            oldDiskIds = serversDO.disks().stream().map(ServerDiskDO::id).collect(Collectors.toSet());
        }
        Set<Long> newDiskIds = saveServerInput.getDisks().stream().map(SaveServerInput.TargetOf_disks::getId).collect(Collectors.toSet());
        List<Long> diffDiskIds = Lists.newArrayList(Sets.difference(oldDiskIds, newDiskIds));
        serverDiskRepository.deleteByIds(diffDiskIds);

        // 更新
        serversRepository.save(saveServerInput, SaveMode.UPSERT);
    }

    @Override
    public void reportRuntimeInfo(Long machineId, RuntimeInfo runtimeInfo) {
        redisUtils.set(CacheKey.MACHINE_RUNTIME_INFO.getKey(machineId), JsonUtil.toJSONString(runtimeInfo), 10, TimeUnit.MINUTES);
        MachineRuntimeInfoDTO runtimeInfoDTO = MachineRuntimeInfoDTO.from(runtimeInfo);
        metricRecordProducerService.pushMachineMetricData(runtimeInfoDTO);
    }
}
