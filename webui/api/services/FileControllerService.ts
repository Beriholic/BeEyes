/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { ResponseEntity_InputStreamResource } from '../models/ResponseEntity_InputStreamResource';
import type { RestBean_List_SftpService_FileEntry } from '../models/RestBean_List_SftpService_FileEntry';
import type { RestBean_Void } from '../models/RestBean_Void';
import type { CancelablePromise } from '../core/CancelablePromise';
import { OpenAPI } from '../core/OpenAPI';
import { request as __request } from '../core/request';
export class FileControllerService {
    /**
     * Deletes a file on the remote server.
     * @param serverId
     * @param path
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static deleteFile(
        serverId: string,
        path: string,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'DELETE',
            url: '/api/v1/file/delete',
            query: {
                'serverId': serverId,
                'path': path,
            },
        });
    }
    /**
     * Downloads a file from the remote server.
     * @param serverId
     * @param path
     * @returns ResponseEntity_InputStreamResource OK
     * @throws ApiError
     */
    public static downloadFile(
        serverId: string,
        path: string,
    ): CancelablePromise<ResponseEntity_InputStreamResource> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/file/download',
            query: {
                'serverId': serverId,
                'path': path,
            },
        });
    }
    /**
     * Lists files in a remote directory.
     * @param serverId
     * @param path
     * @returns RestBean_List_SftpService_FileEntry OK
     * @throws ApiError
     */
    public static listFiles(
        serverId: string,
        path: string = '/',
    ): CancelablePromise<RestBean_List_SftpService_FileEntry> {
        return __request(OpenAPI, {
            method: 'GET',
            url: '/api/v1/file/list',
            query: {
                'serverId': serverId,
                'path': path,
            },
        });
    }
    /**
     * Creates a directory on the remote server.
     * @param serverId
     * @param path
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static createDirectory(
        serverId: string,
        path: string,
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/file/mkdir',
            query: {
                'serverId': serverId,
                'path': path,
            },
        });
    }
    /**
     * Uploads a file to the remote server.
     * @param serverId
     * @param path
     * @param remoteDirectory
     * @param formData
     * @returns RestBean_Void OK
     * @throws ApiError
     */
    public static uploadFile(
        serverId: string,
        path: string,
        remoteDirectory: string = '/',
        formData?: {
            file?: Blob;
        },
    ): CancelablePromise<RestBean_Void> {
        return __request(OpenAPI, {
            method: 'POST',
            url: '/api/v1/file/upload',
            query: {
                'serverId': serverId,
                'path': path,
                'remoteDirectory': remoteDirectory,
            },
            formData: formData,
            mediaType: 'multipart/form-data',
        });
    }
}
