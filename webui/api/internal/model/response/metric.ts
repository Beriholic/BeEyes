export type MetricServiceResponse = {
  "METRIC_SERVICE/GET_CLIENT_METRICS": Array<MetricData>;
};

export interface MetricData {
  readonly id: number;
  readonly online: boolean;
  readonly name: string;
  readonly location: string;
  readonly osName: string;
  readonly osArch: string;
  readonly osVersion: string;
  readonly osBitSize: number;
  readonly cpuName: string;
  readonly ipList: Array<string>;
  readonly cpuCoreCount: number;
  readonly memorySize: number;
  readonly cpuUsage: number;
  readonly memoryUsage: number;
  readonly networkUploadSpeed: number;
  readonly networkDownloadSpeed: number;
}
