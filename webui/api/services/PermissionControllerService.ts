/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { QueryPermissionUserListRequest } from '../models/QueryPermissionUserListRequest';
import type { RestBean_List_PermissionCode } from '../models/RestBean_List_PermissionCode';
import type { RestBean_List_short } from '../models/RestBean_List_short';
import type { RestBean_PageDTO_List_ManageUserView } from '../models/RestBean_PageDTO_List_ManageUserView';
import type { RestBean_UserRoleCode } from '../models/RestBean_UserRoleCode';
import type { RestBean_Void } from '../models/RestBean_Void';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class PermissionControllerService {
    /**
     * @returns RestBean_List_short OK
     * @throws ApiError
     */
    public static getCurrentUserPermissions(): CancelablePromise<RestBean_List_short> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/permission/current-permission',
        });
    }
    /**
     * @returns RestBean_UserRoleCode OK
     * @throws ApiError
     */
    public static getCurrentUserRole(): CancelablePromise<RestBean_UserRoleCode> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/permission/current-role',
        });
    }
    /**
     * @returns RestBean_List_PermissionCode OK
     * @throws ApiError
     */
    public static getPermissionEnum(): CancelablePromise<RestBean_List_PermissionCode> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/permission/enum',
        });
    }
    /**
     * @param userId
     * @param permissionCode
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static setPermission(
        userId: string,
        permissionCode: Array<number>,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/permission/set',
            query: {
                'userId': userId,
                'permissionCode': permissionCode,
            },
        });
    }
    /**
     * @param userId
     * @returns RestBean_List_PermissionCode OK
     * @throws ApiError
     */
    public static getUserPermissions(
        userId: string,
    ): CancelablePromise<RestBean_List_PermissionCode> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/permission/user',
            query: {
                'userId': userId,
            },
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_PageDTO_List_ManageUserView OK
     * @throws ApiError
     */
    public static getManageUserList(
        requestBody: QueryPermissionUserListRequest,
    ): CancelablePromise<RestBean_PageDTO_List_ManageUserView> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/permission/user-list',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
