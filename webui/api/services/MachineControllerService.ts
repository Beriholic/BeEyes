/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { RestBean_PageDTO_List_MachineView } from '../models/RestBean_PageDTO_List_MachineView';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class MachineControllerService {
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
}
