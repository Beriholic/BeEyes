"use client";
import { api } from "@/api/instance";
import { AccountServiceResponse } from "@/api/internal/model/response/account";
import AccountModal, { AccountModalType } from "@/components/AccountModal";
import { ThemeSwitch } from "@/components/theme-switch";
import SettingLayout from "@/layout/SettingLayout";
import { PopMsgErr } from "@/store/pops";
import { useUserInfoStore } from "@/store/user";
import { Avatar, Card, Divider, useDisclosure } from "@heroui/react";
import { useEffect, useState } from "react";
import {
  FaCalendar,
  FaEnvelope,
  FaIdCard,
  FaMoon,
  FaUser,
} from "react-icons/fa6";

export default function SettingPage() {
  const [account, setAccount] = useState<
    AccountServiceResponse["ACCOUNT_SERVICE/ME"] | null
  >(null);
  const { isOpen, onOpen, onOpenChange } = useDisclosure();

  const [modalType, setModalType] = useState<AccountModalType>(
    AccountModalType.NONE
  );

  const closeModal = () => {
    setModalType(AccountModalType.NONE);
    onOpenChange();
  };

  const openModal = (type: AccountModalType) => {
    setModalType(type);
    onOpen();
  };

  const fetchAccountInfo = async () => {
    const res = await api.accountService.me();
    if (res.code !== 200) {
      PopMsgErr({
        title: "获取用户信息失败",
        description: res.message,
      });
      return;
    }
    setAccount(res.data);
  };

  useEffect(() => {
    fetchAccountInfo();
  }, []);

  return (
    account && (
      <>
        <SettingLayout>
          <div className="flex flex-col max-w-4xl mx-auto gap-8">
            <div className="flex flex-col gap-4">
              <div className="text-xl">个人资料</div>
              <Card className="p-4 flex flex-col gap-2 gap-y-4">
                <button className="flex flex-row items-center justify-center gap-8">
                  <Avatar
                    className="transition-transform"
                    name={account.username}
                    size="lg"
                    src={account.avatar}
                    onClick={() => openModal(AccountModalType.AVATAR)}
                  />
                </button>
                <Divider />
                <div className="flex flex-row items-center justify-between gap-8">
                  <div className="flex flex-row px-2 gap-8">
                    <FaIdCard size={24} />
                    <div>ID</div>
                  </div>
                  <div>{account.id}</div>
                </div>
                <Divider />
                <div className="flex flex-row items-center justify-between gap-8">
                  <div className="flex flex-row px-2 gap-8">
                    <FaUser size={24} />
                    <div>昵称</div>
                  </div>
                  <button
                    className="hover:underline"
                    onClick={() => openModal(AccountModalType.USER_NAME)}
                  >
                    {account.username}
                  </button>
                </div>
                <Divider />
                <div className="flex flex-row items-center justify-between gap-8">
                  <div className="flex flex-row px-2 gap-8">
                    <FaEnvelope size={24} />
                    <div>邮箱</div>
                  </div>
                  <button
                    className="hover:underline"
                    onClick={() => openModal(AccountModalType.EMAIL)}
                  >
                    {account.email}
                  </button>
                </div>
                <Divider />
                <div className="flex flex-row items-center justify-between gap-8">
                  <div className="flex flex-row px-2 gap-8">
                    <FaCalendar size={24} />
                    <div>注册时间</div>
                  </div>
                  <div>{account.registerTime.split("T")[0]}</div>
                </div>
                <Divider />
                <div className="flex flex-row items-center justify-between gap-8">
                  <div className="flex flex-row px-2 gap-8">
                    <FaMoon size={24} />
                    <div>黑暗模式</div>
                  </div>
                  <ThemeSwitch />
                </div>
              </Card>
            </div>
            <div className="text-xl">安全设置</div>
            <Card className="p-4 flex flex-col gap-2 gap-y-4">
              <div className="flex flex-row items-center justify-between gap-8">
                <div className="flex flex-row px-2 gap-8">
                  <FaCalendar size={24} />
                  <button
                    className="hover:underline"
                    onClick={() => openModal(AccountModalType.PASSWORD)}
                  >
                    登陆密码
                  </button>
                </div>
              </div>
            </Card>
            <div className="h-12"></div>
          </div>
        </SettingLayout>
        <AccountModal
          name={account.username}
          avatar={account.avatar}
          isOpen={isOpen}
          onOpenChange={closeModal}
          type={modalType}
          onSuccess={() => fetchAccountInfo()}
        />
      </>
    )
  );
}
