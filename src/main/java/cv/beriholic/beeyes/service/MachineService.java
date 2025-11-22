package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.dto.PageDTO;
import cv.beriholic.beeyes.models.entity.ServersDO;
import cv.beriholic.beeyes.models.entity.dto.CreateMachineRequest;
import cv.beriholic.beeyes.models.entity.dto.DeleteMachineRequest;
import cv.beriholic.beeyes.models.entity.dto.QueryMachineListRequest;
import cv.beriholic.beeyes.models.entity.dto.UpdateMachineRequest;
import org.babyfish.jimmer.View;

import java.util.List;

public interface MachineService {
    <V extends View<ServersDO>> PageDTO<List<V>> queryMachineList(Long userId, QueryMachineListRequest request, Class<V> viewType);

    void createMachine(Long userId, CreateMachineRequest request);

    void deleteMachine(Long userId, DeleteMachineRequest request);

    void updateMachine(Long userId, UpdateMachineRequest request);
}
