"use client";
import { api } from "@/api/instance";
import TerminalComponent from "@/components/Terminal";
import { PopMsgErr, PopMsgOK } from "@/store/pops";
import { Button, Card, Input } from "@heroui/react";
import clsx from "clsx";
import { setegid } from "process";
import { useState } from "react";
import { FaTerminal } from "react-icons/fa6";

interface SSHInfo {
  username: string;
  password: string;
  port: number;
}

export default function MachineTerminalPage({ id }: { id: string }) {
  const [enter, setEnter] = useState(false);
  const [edit, setEdit] = useState(false);
  const [sshInfo, setSSHInfo] = useState<SSHInfo>({
    username: "",
    password: "",
    port: 22,
  });

  const fetchSSHInfo = async () => {
    const resp = await api.machineService.sshInfo(id);
    if (resp.code !== 200) {
      PopMsgErr({
        title: "获取SSH信息失败",
        description: resp.message,
      });
    }
    setSSHInfo((prev) => ({
      ...prev,
      username: resp.data.username,
      port: resp.data.port,
    }));
  };

  const gotoEdit = async () => {
    await fetchSSHInfo();
    setEdit(true);
  };
  const gotoEnter = () => {
    setSSHInfo((prev) => ({
      ...prev,
      username: "",
      password: "",
      port: 22,
    }));
    setEdit(false);
  };
  const saveSSHInfo = async () => {
    if (sshInfo.username.length === 0) {
      PopMsgErr({
        title: "保存失败",
        description: "用户名不能为空",
      });
      return;
    }
    if (sshInfo.password.length === 0) {
      PopMsgErr({
        title: "保存失败",
        description: "密码不能为空",
      });
      return;
    }
    if (sshInfo.port === 0) {
      PopMsgErr({
        title: "保存失败",
        description: "端口不能为空",
      });
      return;
    }

    const resp = await api.machineService.saveSSHInfo({
      id: id,
      username: sshInfo.username,
      password: sshInfo.password,
      port: sshInfo.port,
    });
    if (resp.code !== 200) {
      PopMsgErr({
        title: "保存失败",
        description: resp.message,
      });
      return;
    }
    PopMsgOK({
      title: "保存成功",
    });
  };

  if (!enter && edit) {
    return (
      <div className=" flex flex-col items-center justify-center h-screen gap-4">
        <Card className="flex flex-col p-8 size-1/2 gap-4">
          <div className="text-2xl">编辑 SSH 连接信息</div>
          <Input
            label="用户名"
            value={sshInfo.username}
            onChange={(e) => {
              setSSHInfo({
                ...sshInfo,
                username: e.target.value,
              });
            }}
          />
          <Input
            label="密码"
            value={sshInfo.password}
            type="password"
            onChange={(e) => {
              setSSHInfo({
                ...sshInfo,
                password: e.target.value,
              });
            }}
          />
          <Input
            label="端口"
            value={`${sshInfo.port}`}
            type="number"
            onChange={(e) => {
              setSSHInfo({
                ...sshInfo,
                port: Number(e.target.value),
              });
            }}
          />
        </Card>
        <div className="flex items-center gap-4">
          <Button onPress={saveSSHInfo}>保存</Button>
          <Button onPress={gotoEnter}>返回</Button>
        </div>
      </div>
    );
  }

  if (!enter) {
    return (
      <div className="flex h-screen items-center justify-center">
        <Card className="size-1/4 p-4 flex flex-col gap-y-8 items-center">
          <div className="flex items-center gap-4 justify-center text-2xl">
            <FaTerminal className="text-3xl" />
            <div>SSH 连接</div>
          </div>
          <div className="flex items-center gap-4">
            <Button onPress={() => setEnter(true)}>连接</Button>
            <Button onPress={gotoEdit}>修改</Button>
          </div>
        </Card>
      </div>
    );
  }

  return (
    <div className={clsx("flex  flex-col h-screen")}>
      <div className="bg-black/80 rounded-lg shadow-2xl overflow-hidden border border-gray-200 dark:border-gray-800 transform transition-all duration-300 hover:border-gray-300 dark:hover:border-gray-600">
        <div className="flex items-center justify-between px-4 py-2 bg-gray-900 border-b border-gray-800">
          <div className="flex space-x-2">
            <button
              className="w-3 h-3 rounded-full bg-red-500 hover:bg-red-400"
              onClick={() => setEnter(false)}
            />
            <div className="w-3 h-3 rounded-full bg-yellow-500 hover:bg-yellow-400 cursor-pointer" />
            <div className="w-3 h-3 rounded-full bg-green-500 hover:bg-green-400 cursor-pointer" />
          </div>
          <span className="text-gray-400 text-sm font-mono"></span>
          <div className="w-12" />
        </div>

        <div className="w-full p-2 overflow-auto h-full flex-1">
          <TerminalComponent
            className="h-full w-full rounded-b-md overflow-hidden"
            id={id}
            rows={35}
          />
        </div>
      </div>
    </div>
  );
}
