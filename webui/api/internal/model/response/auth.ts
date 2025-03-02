export type AuthServiceResponse = {
  "AUTH_SERVICE/LOGIN": {
    readonly username: string;
    readonly role: string;
    readonly token: string;
    readonly avatar: string;
  };
};
