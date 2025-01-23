import { Executor } from "../executor";
import { MetricServiceResponse } from "../model/response/metric";
import { RestBean } from "../model/static/RestBean";

export class MetricService {
  constructor(private executor: Executor) {}

  async getAll(): Promise<
    RestBean<MetricServiceResponse["METRIC_SERVICE/GET_CLIENT_METRICS"]>
  > {
    const _uri = "/api/metric/list";

    return (await this.executor({
      uri: _uri,
      method: "GET",
    })) as Promise<
      RestBean<MetricServiceResponse["METRIC_SERVICE/GET_CLIENT_METRICS"]>
    >;
  }
}
