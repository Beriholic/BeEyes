/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CreateAlertRequest } from '../models/CreateAlertRequest';
import type { DeleteAlertRequest } from '../models/DeleteAlertRequest';
import type { QueryAlertLogRequest } from '../models/QueryAlertLogRequest';
import type { RestBean_Map_long } from '../models/RestBean_Map_long';
import type { RestBean_PageDTO_List_AlertRuleDTO } from '../models/RestBean_PageDTO_List_AlertRuleDTO';
import type { RestBean_PageDTO_List_Dynamic_AlertLogDO } from '../models/RestBean_PageDTO_List_Dynamic_AlertLogDO';
import type { RestBean_Void } from '../models/RestBean_Void';
import type { UpdateAlertRequest } from '../models/UpdateAlertRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class AlertControllerService {
    /**
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static manualCheck(): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/alert/check',
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_PageDTO_List_Dynamic_AlertLogDO OK
     * @throws ApiError
     */
    public static listAlertLogs(
        requestBody: QueryAlertLogRequest,
    ): CancelablePromise<RestBean_PageDTO_List_Dynamic_AlertLogDO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/alert/log/list',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
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
     * @param pageIndex
     * @param pageSize
     * @param name
     * @param serverId
     * @returns RestBean_PageDTO_List_AlertRuleDTO OK
     * @throws ApiError
     */
    public static listRules(
        pageIndex: number = 1,
        pageSize: number = 10,
        name?: string,
        serverId?: string,
    ): CancelablePromise<RestBean_PageDTO_List_AlertRuleDTO> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/alert/rule/list',
            query: {
                'pageIndex': pageIndex,
                'pageSize': pageSize,
                'name': name,
                'serverId': serverId,
            },
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
    /**
     * @returns RestBean_Map_long OK
     * @throws ApiError
     */
    public static getStats(): CancelablePromise<RestBean_Map_long> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/alert/stats',
        });
    }
}
