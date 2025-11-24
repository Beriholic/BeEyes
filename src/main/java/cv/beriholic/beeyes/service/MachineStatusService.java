package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.consts.ServerStatus;
import cv.beriholic.beeyes.models.dto.ServerStatusDTO;

public interface MachineStatusService {
    void setServerStatus(Long id, ServerStatusDTO oldStatus, ServerStatus newStatus);

    void setServerStatus(Long id, ServerStatus newStatus);

    ServerStatusDTO getServerStatus(Long id);
}
