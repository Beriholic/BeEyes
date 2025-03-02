export type AccountServiceRequest = {
  "ACCOUNT_SERVICE/CHANGE_PASSWORD": {
    readonly old: string;
    readonly password: string;
  };
};
