/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * <p>
 * 服务器表：存储被监控服务器的基本信息
 * </p>
 *
 */
export type MachineManageView = {
    /**
     * 服务器唯一标识
     */
    id?: number;
    /**
     * 服务器描述
     */
    description?: string | null;
    /**
     * 主机名
     */
    hostname?: string | null;
    /**
     * 服务器地区
     */
    region?: string | null;
    /**
     * 客户端API密钥
     */
    apiKey?: string | null;
};

