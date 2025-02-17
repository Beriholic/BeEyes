import { Executor } from "../executor";
import { MachineServiceRequest } from "../model/request/machine";
import { MachineServiceResponse } from "../model/response/machine";
import { RestBean } from "../model/static/RestBean";
import { Void } from "../model/static/void";

export class MachineService {
  constructor(private executor: Executor) {}

  async newMachine(
    req: MachineServiceRequest["MACHINE_SERVICE/NEW_MACHINE"]
  ): Promise<RestBean<string>> {
    const _uri = "/api/machine/new";
    return await this.executor({
      uri: _uri,
      method: "POST",
      body: req,
    });
  }
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
  async list(): Promise<
    RestBean<MachineServiceResponse["MACHINE_SERVICE/LIST"]>
  > {
    const _uri = "/api/machine/list";
    return await this.executor({
      uri: _uri,
      method: "GET",
    });
  }
  async delete(
    req: MachineServiceRequest["MACHINE_SERVICE/DELETE"]
  ): Promise<RestBean<Void>> {
    const _uri = "/api/machine/delete";
    return await this.executor({
      uri: _uri,
      method: "POST",
      body: req,
    });
  }
  async update(
    req: MachineServiceRequest["MACHINE_SERVICE/UPDATE"]
  ): Promise<RestBean<Void>> {
    const _uri = "/api/machine/update";
    return await this.executor({
      uri: _uri,
      method: "POST",
      body: req,
    });
  }
  async info(
    id: string
  ): Promise<RestBean<MachineServiceResponse["MACHINE_SERVICE/INFO"]>> {
    const _uri = `/api/machine/info?id=${id}`;
    return await this.executor({
      uri: _uri,
      method: "GET",
    });
  }
  async sshInfo(
    id: string
  ): Promise<RestBean<MachineServiceResponse["MACHINE_SERVICE/SSH_INFO"]>> {
    const _uri = `/api/machine/ssh/info/${id}`;
    return await this.executor({
      uri: _uri,
      method: "GET",
    });
  }
  async saveSSHInfo(
    req: MachineServiceRequest["MACHINE_SERVICE/SSH_INFO_SAVE"]
  ): Promise<RestBean<Void>> {
    const _uri = "/api/machine/ssh/save";
    return await this.executor({
      uri: _uri,
      method: "POST",
      body: req,
    });
  }
}
