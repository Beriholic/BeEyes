export type MachineServiceRequest = {
  "MACHINE_SERVICE/NEW_MACHINE": {
    readonly name: string;
    readonly location: string;
    readonly nodeName: string;
  };
  "MACHINE_SERVICE/RENAME": {
    readonly id: number;
    readonly name: string;
  };
};
