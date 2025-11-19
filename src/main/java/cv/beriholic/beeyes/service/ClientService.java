package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.dto.system.MachineInfo;
import cv.beriholic.beeyes.models.dto.system.RuntimeInfo;

public interface ClientService {
    Long getIdByTokenWithCache(String token);

    boolean verifyAndRegister(String authorization);

    void reportMachineInfo(Long machineId, MachineInfo machineInfo);

    void reportRuntimeInfo(Long machineId, RuntimeInfo runtimeInfo);
}
