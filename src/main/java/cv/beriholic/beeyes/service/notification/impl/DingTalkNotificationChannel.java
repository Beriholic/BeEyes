package cv.beriholic.beeyes.service.notification.impl;

import cv.beriholic.beeyes.consts.AlertCondition;
import cv.beriholic.beeyes.consts.AlertMetricType;
import cv.beriholic.beeyes.consts.AlertStatus;
import cv.beriholic.beeyes.models.entity.AlertLogDO;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;
import cv.beriholic.beeyes.service.dingtalk.DingTalkClient;
import cv.beriholic.beeyes.service.dingtalk.DingTalkMessage;
import cv.beriholic.beeyes.service.dingtalk.DingTalkSigner;
import cv.beriholic.beeyes.service.notification.NotificationChannel;
import cv.beriholic.beeyes.service.notification.NotificationResult;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * DingTalk notification channel for alert notifications.
 */
@Component
@Slf4j
public class DingTalkNotificationChannel implements NotificationChannel {

    private DingTalkClient client;
    private final boolean enabled;

    @Autowired
    public DingTalkNotificationChannel(
            @Value("${dingtalk.robot.webhook:}") String webhook,
            @Value("${dingtalk.robot.secret:}") String secret,
            @Value("${dingtalk.robot.enabled:true}") boolean enabled) {
        this.enabled = enabled && webhook != null && !webhook.isBlank();
        if (this.enabled) {
            this.client = createClient(webhook, secret);
            log.info("DingTalk notification channel enabled with webhook");
        } else {
            this.client = null;
            log.info("DingTalk notification channel disabled (webhook not configured)");
        }
    }

    /**
     * Constructor for testing with injectable client.
     */
    DingTalkNotificationChannel(DingTalkClient client, boolean enabled) {
        this.client = client;
        this.enabled = enabled;
    }

    /**
     * Creates DingTalkClient instance. Override in tests for mock injection.
     */
    protected DingTalkClient createClient(String webhook, String secret) {
        DingTalkSigner signer = new DingTalkSigner();
        return new DingTalkClient(signer, webhook, secret);
    }

    @Override
    public NotificationResult notify(AlertLogDO alertLog, AlertRuleDO rule) {
        if (!enabled || client == null) {
            log.debug("DingTalk channel skipped (not enabled)");
            return NotificationResult.success();
        }

        try {
            DingTalkMessage message = buildMarkdownMessage(alertLog, rule);
            client.send(message);
            log.info("DingTalk notification sent for rule: {}", rule.name());
            return NotificationResult.success();
        } catch (Exception e) {
            log.error("Failed to send DingTalk notification for rule: {}", rule.name(), e);
            return NotificationResult.failure(e.getMessage(), true);
        }
    }

    private DingTalkMessage buildMarkdownMessage(AlertLogDO alertLog, AlertRuleDO rule) {
        AlertMetricType metricType = AlertMetricType.of(rule.metricType());
        AlertCondition condition = AlertCondition.of(rule.condition());
        AlertStatus status = AlertStatus.of(alertLog.status());

        String metricName = metricType != null ? metricType.getDesc() : "Unknown";
        String conditionName = condition != null ? condition.getDesc() : "Unknown";
        String statusName = status != null ? status.getDesc() : "Unknown";

        String timeStr = alertLog.startedAt() != null
                ? Objects.requireNonNull(alertLog.startedAt()).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))
                : "N/A";

        // 根据状态选择不同的颜色标识
        String statusEmoji = "已恢复".equals(statusName) ? "✅" : "🚨";
        String statusColor = "已恢复".equals(statusName) ? "# 已恢复" : "**!! 告警 !!**";

        String text = String.format("""
                ### %s BeEyes 服务器监控告警

                %s

                > **告警规则**: %s
                >
                > **服务器 ID**: %d
                >
                > **监控指标**: %s
                >
                > **触发条件**: %s %.2f
                >
                > **当前值**: %.2f
                >
                > **状态**: %s %s
                >
                > **时间**: %s
                """,
                statusEmoji,
                statusColor,
                rule.name(),
                alertLog.serverId(),
                metricName,
                conditionName,
                rule.threshold(),
                alertLog.metricValue() != null ? alertLog.metricValue() : Double.valueOf(0.0),
                statusEmoji,
                statusName,
                timeStr
        );

        String title = statusName.contains("恢复") ? "✅ 服务器已恢复" : "🚨 服务器告警通知";
        return new DingTalkMessage.Markdown(title, text);
    }
}
