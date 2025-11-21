/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 网络接口信息
 */
export type NetworkInterfaceInfo = {
    /**
     * 网络接口名称
     */
    name?: string;
    /**
     * IPv4地址列表
     */
    ipv4?: Array<string>;
    /**
     * IPv6地址列表
     */
    ipv6?: Array<string>;
    /**
     * 上传速度（字节）
     */
    uploadSpeed?: number | null;
    /**
     * 下载速度（字节）
     */
    downloadSpeed?: number | null;
};

