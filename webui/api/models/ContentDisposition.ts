/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Charset } from './Charset';
export type ContentDisposition = {
    attachment?: boolean;
    formData?: boolean;
    inline?: boolean;
    type?: string | null;
    name?: string | null;
    filename?: string | null;
    charset?: Charset | null;
    size?: number | null;
    creationDate?: string | null;
    modificationDate?: string | null;
    readDate?: string | null;
};

