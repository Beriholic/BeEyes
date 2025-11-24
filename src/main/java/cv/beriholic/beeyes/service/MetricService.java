package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.dto.RuntimeInfoDTO;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;
import cv.beriholic.beeyes.models.entity.dto.QueryMachineRuntimeInfoRequest;

import java.util.List;

public interface MetricService {
    RuntimeInfo getMachineRuntimeInfoById(Long id);

    void saveRuntimeInfo(Long machineId, RuntimeInfo runtimeInfo);

    List<RuntimeInfoDTO> queryMachineRuntimeInfo(Long userId, QueryMachineRuntimeInfoRequest request);
}
