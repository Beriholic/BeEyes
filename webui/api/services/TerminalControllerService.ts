/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { RestBean_PageDTO_List_MachineTerminalListView } from '../models/RestBean_PageDTO_List_MachineTerminalListView';
import type { RestBean_Void } from '../models/RestBean_Void';
import type { UpdateSSHConfigRequest } from '../models/UpdateSSHConfigRequest';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class TerminalControllerService {
    /**
     * @param pageIndex
     * @param pageSize
     * @param hostname
     * @returns RestBean_PageDTO_List_MachineTerminalListView OK
     * @throws ApiError
     */
    public static queryTerminalList(
        pageIndex: number,
        pageSize: number,
        hostname?: string,
    ): CancelablePromise<RestBean_PageDTO_List_MachineTerminalListView> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/terminal/list',
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
    public static updateSshConfig(
        requestBody: UpdateSSHConfigRequest,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'PUT',
            url: '/api/v1/terminal/ssh-config/update',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
