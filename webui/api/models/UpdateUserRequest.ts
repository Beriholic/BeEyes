/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * <p>
 * 用户表：存储系统用户基本信息和账户状态
 * </p>
 *
 */
export type UpdateUserRequest = {
    /**
     * 用户名（唯一）
     */
    username?: string;
    /**
     * 用户全名
     */
    fullName?: string | null;
    /**
     * 邮箱地址（唯一）
     */
    email?: string;
    /**
     * 手机号
     */
    phone?: string | null;
    userId?: string;
    roleCode?: number;
};

