package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.dto.*;

import java.util.List;

public interface MachineService {
    PageDTO<List<MachineView>> getMachineListByUserId(PageDTO<Long> userIdPage);

    PageDTO<List<MachineManageView>> getMachineManageListByUserId(PageDTO<Long> userIdPage);

    void createMachine(Long userId, CreateMachineRequest request);

    void deleteMachine(Long userId, DeleteMachineRequest request);

    void updateMachine(Long userId, UpdateMachineRequest request);
}
