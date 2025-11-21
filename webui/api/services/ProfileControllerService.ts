/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { RestBean_UserBaseView } from '../models/RestBean_UserBaseView';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class ProfileControllerService {
    /**
     * @returns RestBean_UserBaseView OK
     * @throws ApiError
     */
    public static getSelfInfo(): CancelablePromise<RestBean_UserBaseView> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/user/info',
        });
    }
}
