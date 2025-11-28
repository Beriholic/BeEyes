/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { CreateMachineRequest } from '../models/CreateMachineRequest';
import type { DeleteMachineRequest } from '../models/DeleteMachineRequest';
import type { RestBean_MachineView } from '../models/RestBean_MachineView';
import type { RestBean_PageDTO_List_MachineManageView } from '../models/RestBean_PageDTO_List_MachineManageView';
import type { RestBean_PageDTO_List_MachineView } from '../models/RestBean_PageDTO_List_MachineView';
import type { RestBean_Void } from '../models/RestBean_Void';
import type { UpdateMachineRequest } from '../models/UpdateMachineRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class MachineControllerService {
    /**
     * @param serverId
     * @returns RestBean_MachineView OK
     * @throws ApiError
     */
    public static getMachineDetail(
        serverId: string,
    ): CancelablePromise<RestBean_MachineView> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/machine/detail',
            query: {
                'serverId': serverId,
            },
        });
    }
    /**
     * @param pageIndex
     * @param pageSize
     * @returns RestBean_PageDTO_List_MachineView OK
     * @throws ApiError
     */
    public static getMachineList(
        pageIndex: number,
        pageSize: number,
    ): CancelablePromise<RestBean_PageDTO_List_MachineView> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/machine/list',
            query: {
                'pageIndex': pageIndex,
                'pageSize': pageSize,
            },
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static deleteMachine(
        requestBody: DeleteMachineRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/machine/machine/delete',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static createMachine(
        requestBody: CreateMachineRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/machine/manage/create',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
    /**
     * @param pageIndex
     * @param pageSize
     * @param hostname
     * @returns RestBean_PageDTO_List_MachineManageView OK
     * @throws ApiError
     */
    public static getMachineManageList(
        pageIndex: number,
        pageSize: number,
        hostname?: string,
    ): CancelablePromise<RestBean_PageDTO_List_MachineManageView> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/machine/manage/list',
            query: {
                'hostname': hostname,
                'pageIndex': pageIndex,
                'pageSize': pageSize,
            },
        });
    }
    /**
     * @param requestBody
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static updateMachine(
        requestBody: UpdateMachineRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/machine/manage/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
