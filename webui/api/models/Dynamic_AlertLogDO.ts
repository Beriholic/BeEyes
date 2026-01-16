/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * <p>
 * 告警日志表：存储告警触发和恢复的历史记录
 * </p>
 *
 */
export type Dynamic_AlertLogDO = {
    createdAt?: string | null;
    updatedAt?: string | null;
    deleted?: boolean;
    createdBy?: number | null;
    /**
     * 日志ID
     */
    id?: number;
    /**
     * 关联规则ID
     */
    ruleId?: number;
    /**
     * 关联服务器ID
     */
    serverId?: number;
    /**
     * 触发时的指标值
     */
    metricValue?: number | null;
    /**
     * 告警内容
     */
    message?: string | null;
    /**
     * 告警状态（触发中/已恢复）
     */
    status?: number;
    /**
     * 开始时间
     */
    startedAt?: string | null;
    /**
     * 恢复时间
     */
    resolvedAt?: string | null;
};

