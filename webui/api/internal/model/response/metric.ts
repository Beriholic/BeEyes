export type MetricServiceResponse = {
  "METRIC_SERVICE/GET_CLIENT_METRICS": Array<MetricData>;
};

export interface MetricData {
  readonly id: number;
  readonly online: boolean;
  readonly name: string;
  readonly location: string;
  readonly osName: string;
  readonly kernelVersion: string;
  readonly osVersion: string;
  readonly cpuName: string;
  readonly cpuArch: string;
  readonly cpuCoreCount: number;
  readonly totalMemory: number;
  readonly totalSwap: number;
  readonly totalDisk: number;

  readonly cpuUsage: number;
  readonly memoryUsage: number;
  readonly swapUsage: number;
  readonly networkUploadSpeed: number;
  readonly networkDownloadSpeed: number;
  readonly ipList: Array<string>;
}
