/* generated using openapi-typescript-codegen -- do not edit */
/* istanbul ignore file */
/* tslint:disable */
/* eslint-disable */
import type { Charset } from './Charset';
import type { ContentDisposition } from './ContentDisposition';
import type { HttpMethod } from './HttpMethod';
import type { HttpRange } from './HttpRange';
import type { InetSocketAddress } from './InetSocketAddress';
import type { Locale } from './Locale';
import type { Locale_LanguageRange } from './Locale_LanguageRange';
import type { MediaType } from './MediaType';
import type { URI } from './URI';
export type HttpHeaders = {
    accept?: Array<MediaType>;
    acceptLanguage?: Array<Locale_LanguageRange>;
    acceptLanguageAsLocales?: Array<Locale>;
    acceptPatch?: Array<MediaType>;
    accessControlAllowCredentials?: boolean;
    accessControlAllowHeaders?: Array<string>;
    accessControlAllowMethods?: Array<HttpMethod>;
    accessControlAllowOrigin?: string | null;
    accessControlExposeHeaders?: Array<string>;
    accessControlMaxAge?: number;
    accessControlRequestHeaders?: Array<string>;
    accessControlRequestMethod?: HttpMethod | null;
    acceptCharset?: Array<Charset>;
    allow?: Array<HttpMethod>;
    cacheControl?: string | null;
    connection?: Array<string>;
    contentDisposition?: ContentDisposition;
    contentLanguage?: Locale | null;
    contentLength?: number;
    contentType?: MediaType | null;
    date?: number;
    etag?: string | null;
    expires?: number;
    host?: InetSocketAddress | null;
    ifMatch?: Array<string>;
    ifModifiedSince?: number;
    ifNoneMatch?: Array<string>;
    ifUnmodifiedSince?: number;
    lastModified?: number;
    location?: URI | null;
    origin?: string | null;
    pragma?: string | null;
    range?: Array<HttpRange>;
    upgrade?: string | null;
    vary?: Array<string>;
    empty?: boolean;
};

