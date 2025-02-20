import { exportPages } from "next/dist/export/worker";

export type MachineServiceResponse = {
  "MACHINE_SERVICE/LIST": Array<MachineType>;
  "MACHINE_SERVICE/INFO": MachineInfoType;
  "MACHINE_SERVICE/SSH_INFO": MachineSSHInfoType;
  "MACHINE_SERVICE/LIST_ACTIVE": Array<MachineActiveType>;
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
export interface MachineSSHInfoType {
  readonly username: string;
  readonly port: number;
}

export interface MachineActiveType {
  readonly id: string;
  readonly name: string;
  readonly location: string;
}
