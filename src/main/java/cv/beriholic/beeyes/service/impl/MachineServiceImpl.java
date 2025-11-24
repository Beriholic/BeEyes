package cv.beriholic.beeyes.service.impl;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import cv.beriholic.beeyes.cache.CacheKey;
import cv.beriholic.beeyes.cache.RedisUtils;
import cv.beriholic.beeyes.consts.KafkaGroup;
import cv.beriholic.beeyes.consts.KafkaTopic;
import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.models.dto.MessageEntity;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.dto.ServerStatusUpdatedDTO;
import cv.beriholic.beeyes.models.entity.dto.*;
import cv.beriholic.beeyes.repository.ServersRepository;
import cv.beriholic.beeyes.repository.UserRepository;
import cv.beriholic.beeyes.service.MachineService;
import cv.beriholic.beeyes.utils.JsonUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {
    private final ServersRepository serversRepository;
    private final RedisUtils redisUtils;
    private final UserRepository userRepository;

    @Override
    public void createMachine(Long userId, CreateMachineRequest request) {
        //TODO check 权限
        Long serverId = IdUtil.getSnowflakeNextId();
        SaveCreateMachineInput saveMachineInput = buildCreateMachineInput(serverId, userId, request);
        serversRepository.save(saveMachineInput, SaveMode.INSERT_ONLY);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMachine(Long userId, DeleteMachineRequest request) {
        //TODO check 权限
        serversRepository.deleteById(request.getServerId());
        redisUtils.delete(CacheKey.machineStatus(request.getServerId()));
    }

    @Override
    public void updateMachine(Long userId, UpdateMachineRequest request) {
        // TODO check权限
        UpdateServerInfoInput input = buildUpdateServerInfoInput(request);
        serversRepository.save(input, SaveMode.UPDATE_ONLY);
    }

    @Override
    public List<Long> getUserServerIdListByCache(PageDTO<Long> userIdPage) {
        String cacheKey = CacheKey.userServerList(userIdPage.getData());
        String pageFiled = CacheKey.pageKey(userIdPage.getPageIndex(), userIdPage.getPageSize());

        List<Long> serverList = redisUtils.hGetList(
                cacheKey,
                pageFiled,
                Long.class
        );
        if (CollectionUtils.isEmpty(serverList)) {
            serverList = serversRepository.getServerIdListOrderByStatus(userIdPage);
            redisUtils.hSetList(cacheKey, pageFiled, serverList);
        }

        return serverList;
    }

    @Override
    public void deleteUserServerCacheByServerId(Long serverId) {
        userRepository.getUserIdList(serverId)
                .forEach(id -> redisUtils.delete(CacheKey.machineStatus(id)));
    }

    @Override
    public PageDTO<List<MachineManageView>> queryMachineManageList(long userId, QueryMachineManageListRequest request) {
        QueryServerSpec queryServerSpec = new QueryServerSpec();
        queryServerSpec.setUserId(userId);
        queryServerSpec.setHostname(request.getHostname());

        Page<MachineManageView> page = serversRepository.findBySpecFetchPage(
                queryServerSpec,
                request.getPageIndex(),
                request.getPageSize(),
                MachineManageView.class
        );

        return PageDTO.of(
                page.getRows(),
                request.getPageIndex(),
                request.getPageSize(),
                page.getTotalRowCount(),
                page.getTotalPageCount()
        );
    }

    @Override
    public PageDTO<List<MachineView>> queryMachineListOrderByStatus(long userId, QueryMachineListRequest request) {
        QueryServerSpec queryServerSpec = new QueryServerSpec();
        queryServerSpec.setUserId(userId);

        Page<MachineView> page = serversRepository.queryMachineListOrderByStatus(
                queryServerSpec,
                request.getPageIndex(),
                request.getPageSize()
        );

        return PageDTO.of(
                page.getRows(),
                request.getPageIndex(),
                request.getPageSize(),
                page.getTotalRowCount(),
                page.getTotalPageCount()
        );
    }

    private UpdateServerInfoInput buildUpdateServerInfoInput(UpdateMachineRequest request) {
        UpdateServerInfoInput updateServerInfoInput = new UpdateServerInfoInput();
        updateServerInfoInput.setId(request.getId());
        updateServerInfoInput.setDescription(request.getDescription());
        updateServerInfoInput.setRegion(request.getRegion());
        return updateServerInfoInput;
    }

    private SaveCreateMachineInput buildCreateMachineInput(Long id, Long userId, CreateMachineRequest request) {
        SaveCreateMachineInput input = new SaveCreateMachineInput();
        input.setId(id);
        input.setDescription(request.getDescription());
        input.setRegion(request.getRegion());
        input.setApiKey(UUID.randomUUID().toString());
        input.setStatus(ServerStatus.UNREGISTER.getKey());

        SaveCreateMachineInput.TargetOf_users user = new SaveCreateMachineInput.TargetOf_users();
        user.setId(userId);
        input.setUsers(Lists.newArrayList(user));
        return input;
    }

    @KafkaListener(topics = KafkaTopic.SERVER_STATUS_UPDATED, groupId = KafkaGroup.SERVER_STATUS_GROUP)
    public void updateServerStatus(MessageEntity message) {
        String json = message.getContent();
        ServerStatusUpdatedDTO serverStatusUpdatedDTO = JsonUtil.parseObject(json, ServerStatusUpdatedDTO.class);
        if (Objects.isNull(serverStatusUpdatedDTO)) {
            return;
        }

        ServerStatus serverStatus = ServerStatus.of(serverStatusUpdatedDTO.getStatus());
        serversRepository.updateStatus(serverStatusUpdatedDTO.getId(), serverStatus);

    }
}
