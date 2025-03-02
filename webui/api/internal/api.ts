import type { Executor } from "./executor";
import { AccountService } from "./service/account";
import { AuthService } from "./service/auth";
import { MachineService as MachineService } from "./service/machine";

import { MetricService } from "./service/metric";

export class Api {
  readonly authService: AuthService;
  readonly metricService: MetricService;
  readonly machineService: MachineService;
  readonly accountService: AccountService;

  constructor(executor: Executor) {
    this.authService = new AuthService(executor);
    this.metricService = new MetricService(executor);
    this.machineService = new MachineService(executor);
    this.accountService = new AccountService(executor);
  }
}
