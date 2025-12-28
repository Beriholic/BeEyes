/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
/**
 * <p>
 * 用户权限表：存储用户具体权限分配信息
 * </p>
 *
 */
export type QueryPermissionUserListRequest = {
    username?: string | null;
    fullName?: string | null;
    email?: string | null;
    phone?: string | null;
    ruleCode?: number | null;
    pageIndex?: number;
    pageSize?: number;
};

