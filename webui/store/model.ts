export const UserInfoKey = "userInfo";
export interface UserInfo {
  username: string;
  role: string;
}

export const getLocalUserInfo = () => {
  const userInfo =
    localStorage.getItem(UserInfoKey) ?? '{username: "", role: ""}';
  return JSON.parse(userInfo) as UserInfo;
};
