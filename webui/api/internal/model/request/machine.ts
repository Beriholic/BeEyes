export type MachineServiceRequest = {
  "MACHINE_SERVICE/NEW_MACHINE": {
    readonly name: string;
    readonly location: string;
    readonly nodeName: string;
  };
  "MACHINE_SERVICE/RENAME": {
    readonly id: string;
    readonly name: string;
  };
  "MACHINE_SERVICE/DELETE": {
    readonly id: string;
  };
  "MACHINE_SERVICE/UPDATE": {
    readonly id: string;
    readonly name: string;
    readonly location: string;
    readonly nodeName: string;
  };
  "MACHINE_SERVICE/SSH_INFO_SAVE": {
    readonly id: string;
    readonly username: string;
    readonly password: string;
    readonly port: number;
  };
};
