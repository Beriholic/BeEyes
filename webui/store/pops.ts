import { create } from "zustand";

export interface AlertContent {
  id?: string;
  type?: "success" | "primary" | "default" | "secondary" | "warning" | "danger";
  description?: string;
  title: string;
}

interface PopsProps {
  msgList: Array<AlertContent>;
  pushMsg: (msg: Omit<AlertContent, "id">) => void;
  popMsg: (id: string) => void;
}

export const usePopsStore = create<PopsProps>((set) => ({
  msgList: [],
  pushMsg: (msg) => {
    const id = Date.now().toString();
    set((state) => ({
      msgList: [{ ...msg, id }, ...state.msgList],
    }));
    setTimeout(() => {
      usePopsStore.getState().popMsg(id);
    }, 3000);
  },
  popMsg: (id) =>
    set((state) => ({
      msgList: state.msgList.filter((msg) => msg.id !== id),
    })),
}));

export const PopMsg = (msg: AlertContent) => {
  usePopsStore.getState().pushMsg(msg);
};

export const PopMsgOK = (msg: AlertContent) => {
  PopMsg({ ...msg, type: "success" });
};
export const PopMsgErr = (msg: AlertContent) => {
  PopMsg({ ...msg, type: "danger" });
};
export const PopMsgWarn = (msg: AlertContent) => {
  PopMsg({ ...msg, type: "warning" });
};
