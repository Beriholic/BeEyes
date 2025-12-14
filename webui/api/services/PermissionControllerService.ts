/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { RestBean_List_PermissionCode } from '../models/RestBean_List_PermissionCode';
import type { RestBean_UserRoleCode } from '../models/RestBean_UserRoleCode';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class PermissionControllerService {
    /**
     * @returns RestBean_List_PermissionCode OK
     * @throws ApiError
     */
    public static getCurrentUserPermissions(): CancelablePromise<RestBean_List_PermissionCode> {
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
}
