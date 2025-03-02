export type AccountServiceResponse = {
  "ACCOUNT_SERVICE/ME": {
    readonly id: string;
    readonly username: string;
    readonly email: string;
    readonly avatar: string;
    readonly registerTime: string;
  };
};
