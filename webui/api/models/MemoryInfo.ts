/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 内存信息包括RAM和交换分区
 */
export type MemoryInfo = {
    /**
     * 总内存（字节）
     */
    totalMemory?: number | null;
    /**
     * 已使用内存（字节）
     */
    usedMemory?: number | null;
    /**
     * 可用内存（字节）
     */
    freeMemory?: number | null;
    /**
     * 总交换分区（字节）
     */
    totalSwap?: number | null;
    /**
     * 已使用交换分区（字节）
     */
    usedSwap?: number | null;
    /**
     * 可用交换分区（字节）
     */
    freeSwap?: number | null;
    /**
     * 内存使用百分比
     */
    percentMemory?: number | null;
    /**
     * 交换分区使用百分比
     */
    percentSwap?: number | null;
};

