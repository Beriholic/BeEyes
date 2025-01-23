import type { Executor } from "./executor";

import { AuthService } from "./service";
import { MetricService } from "./service/metric";

export class Api {
  readonly authService: AuthService;
  readonly metricService: MetricService;

  constructor(executor: Executor) {
    this.authService = new AuthService(executor);
    this.metricService = new MetricService(executor);
  }
}
