/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { QueryMachineRuntimeInfoRequest } from '../models/QueryMachineRuntimeInfoRequest';
import type { RestBean_List_RuntimeInfoDTO } from '../models/RestBean_List_RuntimeInfoDTO';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class MetricControllerService {
    /**
     * @param requestBody
     * @returns RestBean_List_RuntimeInfoDTO OK
     * @throws ApiError
     */
    public static queryMachineRuntimeInfo(
        requestBody: QueryMachineRuntimeInfoRequest,
    ): CancelablePromise<RestBean_List_RuntimeInfoDTO> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/metric/runtime/current',
            body: requestBody,
            mediaType: 'application/json',
        });
    }
}
