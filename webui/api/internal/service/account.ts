import { Executor } from "../executor";
import { AccountServiceRequest } from "../model/request/account";
import { AccountServiceResponse } from "../model/response/account";
import { RestBean } from "../model/static/RestBean";
import { Void } from "../model/static/void";

export class AccountService {
  constructor(private executor: Executor) {}

  async me(): Promise<RestBean<AccountServiceResponse["ACCOUNT_SERVICE/ME"]>> {
    const _uri = "/api/account/me";

    return await this.executor({
      uri: _uri,
      method: "GET",
    });
  }

  async changeUsername(username: string): Promise<RestBean<Void>> {
    const _uri = `/api/account/change/username?username=${username}`;
    return await this.executor({
      uri: _uri,
      method: "POST",
    });
  }
  async changeEmail(email: string): Promise<RestBean<Void>> {
    const _uri = `/api/account/change/email?email=${email}`;
    return await this.executor({
      uri: _uri,
      method: "POST",
    });
  }
  async changePassword(
    req: AccountServiceRequest["ACCOUNT_SERVICE/CHANGE_PASSWORD"]
  ): Promise<RestBean<Void>> {
    const _uri = "/api/account/change/password";
    return await this.executor({
      uri: _uri,
      method: "POST",
      body: req,
    });
  }
  async changeAvatar(avatar: string): Promise<RestBean<Void>> {
    const _uri = "/api/account/change/avatar";
    return await this.executor({
      uri: _uri,
      method: "POST",
      body: {
        avatar: avatar,
      },
    });
  }
}
