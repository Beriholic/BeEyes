package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.dto.MessageEntity;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;

public interface MetricService {
    void recordMachineRuntimeInfo(MessageEntity message);

    RuntimeInfo getMachineRuntimeInfoById(Long id);

    void saveRuntimeInfo(Long machineId, RuntimeInfo runtimeInfo);
}
