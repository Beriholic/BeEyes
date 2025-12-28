/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CreateAlertRequest } from '../models/CreateAlertRequest';
import type { DeleteAlertRequest } from '../models/DeleteAlertRequest';
import type { RestBean_List_AlertRuleDTO } from '../models/RestBean_List_AlertRuleDTO';
import type { RestBean_Void } from '../models/RestBean_Void';
import type { UpdateAlertRequest } from '../models/UpdateAlertRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class AlertControllerService {
    /**
     * @param requestBody
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static createRule(
        requestBody: CreateAlertRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/alert/rule/create',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static deleteRule(
        requestBody: DeleteAlertRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/alert/rule/delete',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @returns RestBean_List_AlertRuleDTO OK
     * @throws ApiError
     */
    public static listRules(): CancelablePromise<RestBean_List_AlertRuleDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/alert/rule/list',
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static updateRule(
        requestBody: UpdateAlertRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/v1/alert/rule/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
