/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * <p>
 * 服务器网络接口表：存储服务器网络接口信息
 * </p>
 *
 */
export type MachineView_TargetOf_networkInterfaces = {
    /**
     * 网络接口ID
     */
    id?: number;
    /**
     * 接口名称
     */
    interfaceName?: string;
    /**
     * IPv4地址
     */
    ipv4Address?: Array<string>;
    /**
     * IPv6地址（单个地址）
     */
    ipv6Address?: Array<string>;
};

