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
export type QueryManageUserListRequest = {
    username?: string | null;
    fullName?: string | null;
    email?: string | null;
    phone?: string | null;
    ruleCode?: number | null;
    parentId?: string | null;
    pageIndex?: number;
    pageSize?: number;
};

