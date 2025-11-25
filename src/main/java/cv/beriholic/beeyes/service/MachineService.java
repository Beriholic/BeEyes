package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.dto.*;

import java.util.List;

public interface MachineService {
    void createMachine(Long userId, CreateMachineRequest request);

    void deleteMachine(Long userId, DeleteMachineRequest request);

    void updateMachine(Long userId, UpdateMachineRequest request);

    List<Long> getUserServerIdListByCache(PageDTO<Long> userIdPage);

    void deleteUserServerCacheByServerId(Long serverId);

    PageDTO<List<MachineManageView>> queryMachineManageList(long userId, QueryMachineManageListRequest request);

    PageDTO<List<MachineView>> queryMachineListOrderByStatus(long userId, QueryMachineListRequest request);

    MachineSSHInfoView getMachineSSHInfoView(Long userId, Long serverId);

    void updateLastConnectTime(long clientId, Long userId);

    PageDTO<List<MachineTerminalListView>> queryMachineTerminalList(Long userId, QueryMachineTerminalListRequest request);

    void updateMachineSSHConfig(Long userId, UpdateSSHConfigRequest request);
}
