package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.models.dto.ServerStatusDTO;

public interface MachineStatusService {
    void setServerStatus(Long id, ServerStatus status);

    ServerStatusDTO getServerStatus(Long id);
}
