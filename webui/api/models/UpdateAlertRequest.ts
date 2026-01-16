/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * <p>
 * 告警规则表：存储用户自定义的告警规则
 * </p>
 *
 */
export type UpdateAlertRequest = {
    id?: string;
    name?: string;
    serverId?: string | null;
    metricType?: number;
    condition?: number;
    threshold?: number;
    durationSeconds?: number | null;
    silenceSeconds?: number | null;
    enabled?: boolean;
};

