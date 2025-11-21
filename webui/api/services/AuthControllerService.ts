/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { AuthChangePasswordRequest } from '../models/AuthChangePasswordRequest';
import type { AuthLoginRequest } from '../models/AuthLoginRequest';
import type { RestBean_Void } from '../models/RestBean_Void';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class AuthControllerService {
    /**
     * @param requestBody
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static changePassword(
        requestBody: AuthChangePasswordRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/v1/auth/change/password',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static login(
        requestBody: AuthLoginRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/auth/login',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static logout(): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/v1/auth/logout',
        });
    }
}
