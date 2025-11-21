/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { MachineView_TargetOf_disks } from './MachineView_TargetOf_disks';
import type { MachineView_TargetOf_hardware } from './MachineView_TargetOf_hardware';
import type { MachineView_TargetOf_networkInterfaces } from './MachineView_TargetOf_networkInterfaces';
/**
 * <p>
 * 服务器表：存储被监控服务器的基本信息
 * </p>
 *
 */
export type MachineView = {
    /**
     * 服务器唯一标识
     */
    id?: number;
    /**
     * 主机名
     */
    hostname?: string | null;
    /**
     * 服务器描述
     */
    description?: string | null;
    /**
     * 服务器状态（应用层维护枚举映射）
     */
    status?: number | null;
    /**
     * 最后在线时间
     */
    lastSeen?: string | null;
    hardware?: MachineView_TargetOf_hardware | null;
    disks?: Array<MachineView_TargetOf_disks>;
    networkInterfaces?: Array<MachineView_TargetOf_networkInterfaces>;
};

