package xyz.beriholic.beeyes.service;

import xyz.beriholic.beeyes.entity.dto.WarnMessage;

public interface MessageService {
    void sendWarmMessage(WarnMessage message);
}
