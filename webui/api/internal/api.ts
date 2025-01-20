import type { Executor } from "./executor";

import { AuthService } from "./service";

export class Api {
  readonly authService: AuthService;

  constructor(executor: Executor) {
    this.authService = new AuthService(executor);
  }
}
