export type AuthServiceRequest = {
  "AUTH_SERVICE/LOGIN": {
    readonly username: string;
    readonly password: string;
  };
};
