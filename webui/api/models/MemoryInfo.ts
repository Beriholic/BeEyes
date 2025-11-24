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
    total_memory?: number | null;
    /**
     * 已使用内存（字节）
     */
    used_memory?: number | null;
    /**
     * 可用内存（字节）
     */
    free_memory?: number | null;
    /**
     * 总交换分区（字节）
     */
    total_swap?: number | null;
    /**
     * 已使用交换分区（字节）
     */
    used_swap?: number | null;
    /**
     * 可用交换分区（字节）
     */
    free_swap?: number | null;
    /**
     * 内存使用百分比
     */
    percent_memory?: number | null;
    /**
     * 交换分区使用百分比
     */
    percent_swap?: number | null;
};

