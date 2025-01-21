import { create } from "zustand";
import { persist, createJSONStorage } from "zustand/middleware";

export interface UserInfo {
  username: string;
  role: string;
}

interface UserInfoStoreProps {
  userInfo: UserInfo;
  save: (userInfo: UserInfo) => void;
}

export const useUserInfoStore = create(
  persist<UserInfoStoreProps>(
    (set) => ({
      userInfo: {
        username: "",
        role: "",
      },
      save: (userInfo: UserInfo) => {
        set({ userInfo });
      },
    }),
    {
      name: "user-info",
      storage: createJSONStorage(() => localStorage),
    }
  )
);
