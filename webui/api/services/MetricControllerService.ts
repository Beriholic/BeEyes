/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { RestBean_RuntimeInfo } from '../models/RestBean_RuntimeInfo';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class MetricControllerService {
    /**
     * @param id
     * @returns RestBean_RuntimeInfo OK
     * @throws ApiError
     */
    public static getMachineCurrentRuntimeInfo(
        id: string,
    ): CancelablePromise<RestBean_RuntimeInfo> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/metric/runtime/current/{id}',
            path: {
                'id': id,
            },
        });
    }
}
