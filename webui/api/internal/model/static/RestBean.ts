export interface RestBean<T> {
  readonly id?: string | undefined;
  readonly code?: number | undefined;
  readonly message: string;
  readonly data: T;
}
