import type { Executor } from "./executor";
import { AuthService } from "./service/auth";
import { ClientService } from "./service/machine";

import { MetricService } from "./service/metric";

export class Api {
  readonly authService: AuthService;
  readonly metricService: MetricService;
  readonly clientService: ClientService;

  constructor(executor: Executor) {
    this.authService = new AuthService(executor);
    this.metricService = new MetricService(executor);
    this.clientService = new ClientService(executor);
  }
}
