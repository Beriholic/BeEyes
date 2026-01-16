/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { File } from './File';
import type { InputStream } from './InputStream';
import type { URI } from './URI';
import type { URL } from './URL';
export type InputStreamResource = {
    open?: boolean;
    inputStream?: InputStream;
    description?: string;
    readable?: boolean;
    file?: File;
    url?: URL;
    uri?: URI;
    filename?: string | null;
};

