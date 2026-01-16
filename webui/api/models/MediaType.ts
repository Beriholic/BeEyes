/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Charset } from './Charset';
export type MediaType = {
    qualityValue?: number;
    wildcardType?: boolean;
    wildcardSubtype?: boolean;
    concrete?: boolean;
    type?: string;
    subtype?: string;
    subtypeSuffix?: string | null;
    charset?: Charset | null;
    parameters?: Record<string, string>;
};

