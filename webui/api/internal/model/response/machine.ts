export type MachineServiceResponse = {
  "MACHINE_SERVICE/LIST": Array<MachineType>;
  "MACHINE_SERVICE/INFO": MachineInfoType;
};

export interface MachineType {
  readonly id: string;
  readonly name: string;
  readonly location: string;
  readonly nodeName: string;
  readonly registerTime: string;
  readonly token: string;
  readonly active: string;
}

export interface MachineInfoType {
  readonly name: string;
  readonly location: string;
  readonly osName: string;
  readonly kernelVersion: string;
  readonly osVersion: string;
  readonly cpuArch: string;
  readonly cpuName: string;
  readonly cpuCoreCount: number;
  readonly totalMemory: number;
  readonly totalSwap: number;
  readonly totalDiskSize: number;
}
