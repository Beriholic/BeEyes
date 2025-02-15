import { BASE_URL } from "@/api/instance";
import { Executor } from "../executor";
import { MetricServiceResponse } from "../model/response/metric";
import { RestBean } from "../model/static/RestBean";
import {
  fetchEventSource,
  EventSourceMessage,
} from "@microsoft/fetch-event-source";

export class MetricService {
  constructor(private executor: Executor) {}

  getClientMetricList({
    onmessage,
    onerror,
    onopen,
    signal,
  }: {
    onmessage: (
      data: MetricServiceResponse["METRIC_SERVICE/GET_CLIENT_METRICS"]
    ) => void;
    onerror: (err: any) => void;
    onopen?: (resp: Response) => Promise<void>;
    signal: AbortSignal;
  }) {
    const _uri = `${BASE_URL}/api/metric/list`;

    fetchEventSource(_uri, {
      method: "GET",
      headers: {
        jinyum: localStorage.getItem("token") ?? "",
      },
      onopen: onopen,
      onmessage: (event) => {
        const data = JSON.parse(
          event.data
        ) as MetricServiceResponse["METRIC_SERVICE/GET_CLIENT_METRICS"];
        onmessage(data);
      },
      signal: signal,
      onerror: onerror,
    });
  }
}
