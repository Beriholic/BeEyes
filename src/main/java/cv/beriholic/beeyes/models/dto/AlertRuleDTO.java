package cv.beriholic.beeyes.models.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import cv.beriholic.beeyes.models.entity.AlertRuleDO;

@Data
@NoArgsConstructor
public class AlertRuleDTO {
    private String id;
    private String name;
    private Long serverId;
    private Integer metricType;
    private Integer condition;
    private Double threshold;
    private Integer durationSeconds;
    private Integer silenceSeconds;
    private Boolean enabled;

    public AlertRuleDTO(AlertRuleDO rule) {
        this.id = String.valueOf(rule.id());
        this.name = rule.name();
        this.serverId = rule.serverId();
        this.metricType = rule.metricType();
        this.condition = rule.condition();
        this.threshold = rule.threshold();
        this.durationSeconds = rule.durationSeconds();
        this.silenceSeconds = rule.silenceSeconds();
        this.enabled = rule.enabled();
    }
}
