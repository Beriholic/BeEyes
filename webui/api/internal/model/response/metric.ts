export type MetricServiceResponse = {
  "METRIC_SERVICE/GET_CLIENT_METRICS": Array<MetricData>;
  "METRIC_SERVICE/CURRENT_RUNTIME_INFO": CurrentRuntimeInfoType;
  "METRIC_SERVICE/HISTORY_INFO": { list: Array<HistoryRuntimeInfoType> };
};

export interface MetricData {
  readonly id: string;
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

export interface CurrentRuntimeInfoType {
  readonly online: boolean;
  readonly clientId: number;
  readonly timestamp: number;
  readonly cpuUsage: number;
  readonly memoryUsage: number;
  readonly swapUsage: number;
  readonly diskUsage: number;
  readonly networkUploadSpeed: number;
  readonly networkDownloadSpeed: number;
}

export interface HistoryRuntimeInfoType {
  online: boolean;
  timestamp: string;
  cpuUsage: number;
  diskUsage: number;
  memoryUsage: number;
  networkDownloadSpeed: number;
  networkUploadSpeed: number;
  swapUsage: number;
}
