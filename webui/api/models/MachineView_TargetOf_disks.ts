/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * <p>
 * 服务器磁盘信息表：存储服务器磁盘分区信息
 * </p>
 *
 */
export type MachineView_TargetOf_disks = {
    /**
     * 磁盘名称
     */
    diskName?: string;
    /**
     * 磁盘类型（SSD/HDD等）
     */
    diskKind?: string | null;
    /**
     * 文件系统类型
     */
    fileSystem?: string | null;
    /**
     * 总容量(字节)
     */
    totalBytes?: number;
};

