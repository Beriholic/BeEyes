/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CPUInfo } from './CPUInfo';
import type { DiskInfo } from './DiskInfo';
import type { MemoryInfo } from './MemoryInfo';
import type { NetworkInfo } from './NetworkInfo';
/**
 * 运行时指标用于定期监控
 */
export type RuntimeInfo = {
    /**
     * 时间戳（毫秒）
     */
    timestamp?: number | null;
    /**
     * CPU信息
     */
    cpu_info?: CPUInfo;
    /**
     * 内存信息
     */
    memory_info?: MemoryInfo;
    /**
     * 磁盘信息列表
     */
    disk_info?: Array<DiskInfo>;
    /**
     * 网络信息
     */
    network_info?: NetworkInfo;
};

