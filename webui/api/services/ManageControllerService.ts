/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CreateUserRequest } from '../models/CreateUserRequest';
import type { DeleteUserRequest } from '../models/DeleteUserRequest';
import type { QueryManageUserListRequest } from '../models/QueryManageUserListRequest';
import type { ResetUserPasswordRequest } from '../models/ResetUserPasswordRequest';
import type { RestBean_CreateUserView } from '../models/RestBean_CreateUserView';
import type { RestBean_PageDTO_List_ManageUserView } from '../models/RestBean_PageDTO_List_ManageUserView';
import type { RestBean_ResetUserView } from '../models/RestBean_ResetUserView';
import type { RestBean_Void } from '../models/RestBean_Void';
import type { UpdateUserRequest } from '../models/UpdateUserRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ManageControllerService {
    /**
     * @param requestBody
     * @returns RestBean_PageDTO_List_ManageUserView OK
     * @throws ApiError
     */
    public static list(
        requestBody: QueryManageUserListRequest,
    ): CancelablePromise<RestBean_PageDTO_List_ManageUserView> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/manage/list',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_CreateUserView OK
     * @throws ApiError
     */
    public static createUser(
        requestBody: CreateUserRequest,
    ): CancelablePromise<RestBean_CreateUserView> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/manage/user-create',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static deleteUser(
        requestBody: DeleteUserRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/manage/user-delete',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_ResetUserView OK
     * @throws ApiError
     */
    public static resetUserPassword(
        requestBody: ResetUserPasswordRequest,
    ): CancelablePromise<RestBean_ResetUserView> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/v1/manage/user-reset-password',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static updateUser(
        requestBody: UpdateUserRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/v1/manage/user-update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
