export const TokenKey = "token";
export interface Token {
  value: string;
  expire: number;
}

export const UserInfoKey = "userInfo";
export interface UserInfo {
  username: string;
  role: string;
}
