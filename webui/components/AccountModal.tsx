"use client";
import { api } from "@/api/instance";
import { PopMsgErr, PopMsgOK, PopMsgWarn } from "@/store/pops";
import { useUserInfoStore } from "@/store/user";
import {
  Modal,
  ModalContent,
  ModalHeader,
  ModalBody,
  ModalFooter,
  Button,
  Input,
  RadioGroup,
  Radio,
  Avatar,
} from "@heroui/react";
import { useRouter } from "next/navigation";
import { useCallback, useMemo, useState } from "react";

export enum AccountModalType {
  USER_NAME,
  EMAIL,
  PASSWORD,
  AVATAR,
  NONE,
}

interface ModalProps {
  name: string;
  avatar: string;
  isOpen: boolean;
  onOpenChange: () => void;
  type: AccountModalType;
  onSuccess: () => void;
}
export default function AccountModal({
  name,
  avatar,
  isOpen,
  onOpenChange,
  type,
  onSuccess,
}: ModalProps) {
  const userInfo = useUserInfoStore((state) => state.userInfo);
  const saveUserInfo = useUserInfoStore((state) => state.save);
  const router = useRouter();
  const [username, setUsername] = useState("");
  const [email, setEmail] = useState("");
  const [oldPassword, setOldPassword] = useState("");
  const [password, setPassword] = useState("");
  const [passwordContrim, setPasswordContrim] = useState("");
  const [avatarSource, setAvatarSource] = useState("qq");
  const [avatarValue, setAvatarValue] = useState("");
  const [avatarPreview, setAvatarPreview] = useState(avatar);

  const modalTitle = useMemo(() => {
    switch (type) {
      case AccountModalType.USER_NAME:
        return "修改昵称";
      case AccountModalType.EMAIL:
        return "修改邮箱";
      case AccountModalType.PASSWORD:
        return "修改密码";
    }
  }, [type]);

  const inputModule = useCallback(() => {
    switch (type) {
      case AccountModalType.USER_NAME:
        return (
          <div>
            <Input
              label="昵称"
              value={username}
              onValueChange={(v) => setUsername(v)}
            />
          </div>
        );
      case AccountModalType.EMAIL:
        return (
          <div>
            <Input
              type="email"
              label="邮箱"
              value={email}
              onValueChange={(v) => setEmail(v)}
            />
          </div>
        );
      case AccountModalType.PASSWORD:
        return (
          <div className="flex flex-col gap-4">
            <Input
              label="旧密码"
              type="password"
              value={oldPassword}
              onValueChange={(v) => setOldPassword(v)}
            />
            <Input
              label="新密码"
              value={password}
              type="password"
              onValueChange={(v) => setPassword(v)}
              checked={password === passwordContrim}
            />
            <Input
              label="确认密码"
              type="password"
              value={passwordContrim}
              onValueChange={(v) => setPasswordContrim(v)}
            />
          </div>
        );
      case AccountModalType.AVATAR:
        return (
          <div className="flex flex-col gap-4">
            <Avatar
              className="transition-transform"
              name={name}
              size="lg"
              src={avatarPreview}
            />

            <div>头像来源</div>
            <RadioGroup
              value={avatarSource}
              onValueChange={(v) => setAvatarSource(v)}
            >
              <Radio value={"qq"}>QQ</Radio>
              <Radio value={"github"}>Github</Radio>
              <Radio value={"custom"}>自定义</Radio>
            </RadioGroup>
            <div>{avatarInput()}</div>
          </div>
        );
    }
  }, [
    type,
    username,
    email,
    oldPassword,
    password,
    passwordContrim,
    avatarSource,
    avatarValue,
    avatarPreview,
  ]);

  const avatarInput = useCallback(() => {
    switch (avatarSource) {
      case "qq":
        return (
          <Input
            label="QQ号"
            value={avatarValue}
            onValueChange={(v) => {
              setAvatarValue(v);
              setAvatarPreviewValue(v);
            }}
          />
        );
      case "github":
        return (
          <Input
            label="Github名称"
            value={avatarValue}
            onValueChange={(v) => {
              setAvatarValue(v);
              setAvatarPreviewValue(v);
            }}
          />
        );
      case "custom":
        return (
          <Input
            label="头像地址"
            value={avatarValue}
            onValueChange={(v) => {
              setAvatarValue(v);
              setAvatarPreviewValue(v);
            }}
          />
        );
    }
  }, [avatarSource, avatarValue]);

  const setAvatarPreviewValue = (value: string) => {
    switch (avatarSource) {
      case "qq":
        setAvatarPreview(`https://q.qlogo.cn/g?b=qq&nk=${value}&s=100`);
        break;
      case "github":
        setAvatarPreview(`https://avatars.githubusercontent.com/${value}`);
        break;
      default:
        setAvatarPreview(value);
    }
  };

  const rest = () => {
    setUsername("");
    setEmail("");
    setOldPassword("");
    setPassword("");
    setPasswordContrim("");
    setAvatarSource("qq");
    setAvatarValue("");
  };
  const closeModal = () => {
    rest();
    onOpenChange();
  };

  const changeUsername = async () => {
    const res = await api.accountService.changeUsername(username);
    if (res.code !== 200) {
      PopMsgErr({
        title: "修改昵称失败",
        description: res.message,
      });
      return;
    }

    PopMsgOK({
      title: "修改昵称成功",
      description: "昵称修改成功",
    });
    saveUserInfo({
      ...userInfo,
      username: username,
    });

    onSuccess();
    closeModal();
  };
  const changeEmail = async () => {
    const res = await api.accountService.changeEmail(email);
    if (res.code !== 200) {
      PopMsgErr({
        title: "修改邮箱失败",
        description: res.message,
      });
      return;
    }

    PopMsgOK({
      title: "修改邮箱成功",
      description: "邮箱修改成功",
    });
    onSuccess();
    closeModal();
  };
  const changePassword = async () => {
    if (password.length < 6) {
      PopMsgWarn({
        title: "修改密码失败",
        description: "密码长度不能小于6位",
      });
      return;
    }

    if (passwordContrim !== password) {
      PopMsgWarn({
        title: "修改密码失败",
        description: "两次密码不一致",
      });
      return;
    }

    const res = await api.accountService.changePassword({
      old: oldPassword,
      password: password,
    });
    if (res.code !== 200) {
      PopMsgErr({
        title: "修改密码失败",
        description: res.message,
      });
      return;
    }

    PopMsgOK({
      title: "修改密码成功",
      description: "密码修改成功",
    });

    router.push("/login");
  };

  const changeAvatar = async () => {
    const res = await api.accountService.changeAvatar(avatarPreview);
    if (res.code !== 200) {
      PopMsgErr({
        title: "修改头像失败",
        description: res.message,
      });
    }
    PopMsgOK({
      title: "修改头像成功",
      description: "头像修改成功",
    });
    saveUserInfo({
      ...userInfo,
      avatar: avatarPreview,
    });

    onSuccess();
    closeModal();
  };

  const change = () => {
    switch (type) {
      case AccountModalType.USER_NAME:
        changeUsername();
        break;
      case AccountModalType.EMAIL:
        changeEmail();
        break;
      case AccountModalType.PASSWORD:
        changePassword();
        break;
      case AccountModalType.AVATAR:
        changeAvatar();
        break;
    }
  };

  return (
    <Modal isOpen={isOpen} onOpenChange={closeModal}>
      <ModalContent>
        {(onClose) => (
          <>
            <ModalHeader className="flex flex-col gap-1">
              {modalTitle}
            </ModalHeader>
            <ModalBody>{inputModule()}</ModalBody>
            <ModalFooter>
              <Button color="danger" variant="light" onPress={onClose}>
                关闭
              </Button>
              <Button color="primary" onPress={change}>
                修改
              </Button>
            </ModalFooter>
          </>
        )}
      </ModalContent>
    </Modal>
  );
}
