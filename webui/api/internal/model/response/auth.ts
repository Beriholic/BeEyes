export type AuthServiceResponse = {
  "AUTH_SERVICE/LOGIN": {
    readonly expire: Date;
    readonly role: string;
    readonly token: string;
    readonly username: string;
  };
};
