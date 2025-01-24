import { Executor } from "../executor";
import { MachineServiceRequest } from "../model/request/machine";
import { RestBean } from "../model/static/RestBean";
import { Void } from "../model/static/void";

export class ClientService {
  constructor(private executor: Executor) {}

  async rename(
    req: MachineServiceRequest["MACHINE_SERVICE/RENAME"]
  ): Promise<RestBean<Void>> {
    const _uri = "/api/machine/rename";
    return await this.executor({
      uri: _uri,
      method: "POST",
      body: req,
    });
  }
}
