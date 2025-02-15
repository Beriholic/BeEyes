export type MachineServiceResponse = {
  "MACHINE_SERVICE/LIST": Array<MachineType>;
};

export interface MachineType {
  id: string;
  name: string;
  location: string;
  nodeName: string;
  registerTime: string;
  token: string;
  active: string;
}
