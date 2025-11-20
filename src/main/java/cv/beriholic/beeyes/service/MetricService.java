package cv.beriholic.beeyes.service;

import cv.beriholic.beeyes.models.dto.MessageEntity;

public interface MetricService {
    void recordMachineRuntimeInfo(MessageEntity message);
}
