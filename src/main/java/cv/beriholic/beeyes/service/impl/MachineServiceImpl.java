package cv.beriholic.beeyes.service.impl;

import cn.hutool.core.util.IdUtil;
import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.dto.*;
import cv.beriholic.beeyes.repository.ServersRepository;
import cv.beriholic.beeyes.repository.UserServiceRepository;
import cv.beriholic.beeyes.service.MachineService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.babyfish.jimmer.Page;
import org.babyfish.jimmer.sql.ast.mutation.SaveMode;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class MachineServiceImpl implements MachineService {
    private final UserServiceRepository userServiceRepository;
    private final ServersRepository serversRepository;

    public PageDTO<List<MachineView>> getMachineListByUserId(PageDTO<Long> userIdPage) {
        List<MachineView> machineList;
        Page<Long> serverIds = userServiceRepository.getServerIdsByUserId(userIdPage);
        machineList = serversRepository.findByIds(serverIds.getRows(), MachineView.class);
        return PageDTO.of(machineList, userIdPage.getPageIndex(), userIdPage.getPageSize(), serverIds.getTotalRowCount(), serverIds.getTotalPageCount());
    }

    @Override
    public PageDTO<List<MachineManageView>> getMachineManageListByUserId(PageDTO<Long> userIdPage) {
        Page<Long> serverIds = userServiceRepository.getServerIdsByUserId(userIdPage);
        List<MachineManageView> machineList = serversRepository.findByIds(serverIds.getRows(), MachineManageView.class);
        return PageDTO.of(machineList, userIdPage.getPageIndex(), userIdPage.getPageSize(), serverIds.getTotalRowCount(), serverIds.getTotalPageCount());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void createMachine(Long userId, CreateMachineRequest request) {
        //TODO check 权限

        Long serverId = IdUtil.getSnowflakeNextId();

        SaveCreateMachineInput saveMachineInput = buildCreateMachineInput(serverId, request);
        SaveCreateUserServerInput saveUserServerInput = buildCreateUserServerInput(serverId, userId);

        serversRepository.save(saveMachineInput, SaveMode.INSERT_ONLY);
        userServiceRepository.save(saveUserServerInput, SaveMode.INSERT_ONLY);
    }


    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteMachine(Long userId, DeleteMachineRequest request) {
        //TODO check 权限
        serversRepository.deleteById(request.getServerId());
        userServiceRepository.deleteByServerId(request.getServerId());
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

    private SaveCreateMachineInput buildCreateMachineInput(Long id, CreateMachineRequest request) {
        SaveCreateMachineInput input = new SaveCreateMachineInput();
        input.setId(id);
        input.setDescription(request.getDescription());
        input.setRegion(request.getRegion());
        input.setApiKey(UUID.randomUUID().toString());
        input.setStatus(ServerStatus.UNREGISTER.getKey());
        return input;
    }

    private SaveCreateUserServerInput buildCreateUserServerInput(Long serverId, Long userId) {
        SaveCreateUserServerInput input = new SaveCreateUserServerInput();
        input.setId(IdUtil.getSnowflakeNextId());
        input.setServerId(serverId);
        input.setUserId(userId);
        return input;
    }
}
