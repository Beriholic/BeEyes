import { Executor } from "../executor";
import { AuthServiceRequest } from "../model/request/auth";
import { AuthServiceResponse } from "../model/response/auth";
import { RestBean } from "../model/static/RestBean";
import { Void } from "../model/static/void";

export class AuthService {
  constructor(private executor: Executor) {}

  async login(
    req: AuthServiceRequest["AUTH_SERVICE/LOGIN"]
  ): Promise<RestBean<AuthServiceResponse["AUTH_SERVICE/LOGIN"]>> {
    const _uri = `/api/auth/login?username=${req.username}&password=${req.password}`;

    return (await this.executor({
      uri: _uri,
      method: "POST",
    })) as Promise<RestBean<AuthServiceResponse["AUTH_SERVICE/LOGIN"]>>;
  }

  async logout(): Promise<RestBean<Void>> {
    const _uri = `/api/auth/logout`;

    return (await this.executor({
      uri: _uri,
      method: "POST",
    })) as Promise<RestBean<Void>>;
  }
}
