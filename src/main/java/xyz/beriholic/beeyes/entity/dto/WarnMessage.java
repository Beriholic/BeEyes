package xyz.beriholic.beeyes.entity.dto;

import java.util.Date;

public class WarnMessage extends MessageTemplate {
    public WarnMessage(String title, String content, Date date) {
        super(title, content, date);
    }

    public static WarnMessage newCPUWarnMessage(Long id, String name) {
        return new WarnMessage(
                "BeEyes 系统警告",
                String.format("设备{id: %d, name: %s} CPU 占用率长时间达到或超过 80%%，请注意检查", id, name),
                new Date()
        );
    }
}
