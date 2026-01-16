package cv.beriholic.beeyes.service.notification.impl;

import cv.beriholic.beeyes.consts.AlertCondition;
import cv.beriholic.beeyes.consts.AlertMetricType;
import cv.beriholic.beeyes.consts.AlertStatus;
import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.service.dingtalk.DingTalkClient;
import cv.beriholic.beeyes.service.dingtalk.DingTalkMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Unit tests for DingTalkNotificationChannel.
 */
@ExtendWith(MockitoExtension.class)
class DingTalkNotificationChannelTest {

    @Mock
    private DingTalkClient mockClient;

    private DingTalkNotificationChannel channel;

    private AlertRuleDO testRule;
    private AlertLogDO testLog;

    @BeforeEach
    void setUp() {
        // Use package-private constructor for testing
        channel = new DingTalkNotificationChannel(mockClient, true);

        testRule = new AlertRuleDO() {
            @Override
            public long id() { return 1L; }
            @Override
            public String name() { return "High CPU Alert"; }
            @Override
            public Long serverId() { return 100L; }
            @Override
            public int metricType() { return AlertMetricType.CPU.getKey(); }
            @Override
            public int condition() { return AlertCondition.GT.getKey(); }
            @Override
            public double threshold() { return 80.0; }
            @Override
            public Integer durationSeconds() { return 60; }
            @Override
            public boolean enabled() { return true; }
            @Override
            public Integer silenceSeconds() { return 300; }
            @Override
            public LocalDateTime createdAt() { return null; }
            @Override
            public LocalDateTime updatedAt() { return null; }
            @Override
            public boolean isDeleted() { return false; }
            @Override
            public Long createdBy() { return null; }
        };

        testLog = new AlertLogDO() {
            @Override
            public long id() { return 1L; }
            @Override
            public long ruleId() { return 1L; }
            @Override
            public long serverId() { return 100L; }
            @Override
            public Double metricValue() { return 95.5; }
            @Override
            public String message() { return "CPU usage exceeded 80%"; }
            @Override
            public int status() { return AlertStatus.TRIGGERING.getKey(); }
            @Override
            public LocalDateTime startedAt() { return LocalDateTime.of(2025, 1, 1, 12, 0, 0); }
            @Override
            public LocalDateTime resolvedAt() { return null; }
            @Override
            public LocalDateTime createdAt() { return null; }
            @Override
            public LocalDateTime updatedAt() { return null; }
            @Override
            public boolean isDeleted() { return false; }
            @Override
            public Long createdBy() { return null; }
        };
    }

    @Test
    @DisplayName("notify sends message to DingTalk client")
    void notify_sendsMessage() {
        channel.notify(testLog, testRule);

        verify(mockClient).send(any(DingTalkMessage.class));
    }

    @Test
    @DisplayName("notify formats markdown message correctly")
    void notify_formatsMarkdownMessage() {
        ArgumentCaptor<DingTalkMessage> captor = ArgumentCaptor.forClass(DingTalkMessage.class);

        channel.notify(testLog, testRule);

        verify(mockClient).send(captor.capture());
        String json = captor.getValue().toJson();

        // Verify JSON contains expected fields
        assertTrue(json.contains("\"msgtype\":\"markdown\""));
        assertTrue(json.contains("\"title\":\"BeEyes Alert: High CPU Alert\""));
        assertTrue(json.contains("**Rule**: High CPU Alert"));
        assertTrue(json.contains("**Metric Type**: CPU使用率"));
        assertTrue(json.contains("**Condition**: 大于 80.00"));
        assertTrue(json.contains("**Current Value**: 95.50"));
        assertTrue(json.contains("**Status**: 触发中"));
    }

    @Test
    @DisplayName("notify handles client exception gracefully")
    void notify_handlesExceptionGracefully() {
        doThrow(new RuntimeException("Network error")).when(mockClient).send(any(DingTalkMessage.class));

        // Should not throw exception
        assertDoesNotThrow(() -> channel.notify(testLog, testRule));

        verify(mockClient).send(any(DingTalkMessage.class));
    }

    @Test
    @DisplayName("notify does nothing when channel is disabled")
    void notify_doesNothingWhenDisabled() {
        DingTalkNotificationChannel disabledChannel = new DingTalkNotificationChannel(null, false);

        // Should not throw exception
        assertDoesNotThrow(() -> disabledChannel.notify(testLog, testRule));
    }
}
