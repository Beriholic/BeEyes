package cv.beriholic.beeyes.service.impl;

import cn.hutool.core.util.IdUtil;
import com.google.common.collect.Lists;
import cv.beriholic.beeyes.consts.CacheKey;
import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.ServersDO;
import cv.beriholic.beeyes.models.entity.dto.*;
import cv.beriholic.beeyes.repository.ServersRepository;
import cv.beriholic.beeyes.service.MachineService;
import cv.beriholic.beeyes.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.View;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {
    private final ServersRepository serversRepository;
    private final RedisUtils redisUtils;

    @Override
    public <V extends View<ServersDO>> PageDTO<List<V>> queryMachineList(Long userId, QueryMachineListRequest request, Class<V> viewType) {
        QueryServerSpec queryServerSpec = new QueryServerSpec();
        queryServerSpec.setUserId(userId);
        queryServerSpec.setHostname(request.getHostname());

        Page<V> page = serversRepository.findBySpecFetchPage(
                queryServerSpec,
                request.getPageIndex(),
                request.getPageSize(),
                viewType
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
}
