/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * 磁盘信息和使用统计
 */
export type DiskInfo = {
    /**
     * 磁盘名称
     */
    name?: string;
    /**
     * 文件系统类型
     */
    fileSystem?: string;
    /**
     * 总空间（字节）
     */
    total?: number | null;
    /**
     * 已使用空间（字节）
     */
    used?: number | null;
    /**
     * 可用空间（字节）
     */
    free?: number | null;
    /**
     * 使用比例
     */
    percent?: number | null;
    /**
     * 磁盘类型
     */
    kind?: string;
};

