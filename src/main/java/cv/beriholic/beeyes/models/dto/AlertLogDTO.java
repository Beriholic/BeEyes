package cv.beriholic.beeyes.models.dto;

import cv.beriholic.beeyes.consts.AlertMetricType;
import cv.beriholic.beeyes.consts.AlertStatus;
import cv.beriholic.beeyes.models.entity.AlertLogDO;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class AlertLogDTO {
    private String id;
    private String ruleId;
    private String serverId;
    private Double metricValue;
    private String message;
    private Integer status;
    private String statusDesc;
    private LocalDateTime startedAt;
    private LocalDateTime resolvedAt;

    public AlertLogDTO(AlertLogDO log, AlertRuleDTO rule) {
        this.id = String.valueOf(log.id());
        this.ruleId = String.valueOf(log.ruleId());
        this.serverId = String.valueOf(log.serverId());
        this.metricValue = log.metricValue();
        this.message = log.message();
        this.status = log.status();
        AlertStatus status = AlertStatus.of(log.status());
        this.statusDesc = status != null ? status.getDesc() : "未知";
        this.startedAt = log.startedAt();
        this.resolvedAt = log.resolvedAt();
    }
}
