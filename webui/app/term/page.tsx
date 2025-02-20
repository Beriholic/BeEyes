"use client";
import { api } from "@/api/instance";
import { MachineActiveType } from "@/api/internal/model/response/machine";
import { PopMsgErr } from "@/store/pops";
import { useCallback, useEffect, useState } from "react";
import { useRouter } from "next/navigation";
import { TerminalLayout } from "@/layout/TerminalLayout";
import MachineActiveListSidebar from "@/components/MachineActiveListSidebar";
import MachineTerminalPage from "../machine/[id]/_module/terminal";

export default function TerminalPage() {
  const router = useRouter();
  const [currentId, setCurrentId] = useState<string | null>(null);
  const [machines, setMachines] = useState<Array<MachineActiveType>>([]);

  useEffect(() => {
    const fetchMachineList = async () => {
      const res = await api.machineService.listActive();
      if (res.code !== 200) {
        PopMsgErr({
          title: "获取机器列表失败",
          description: res.message,
        });
        return;
      }

      setMachines(res.data);
    };

    fetchMachineList();
  }, []);

  const renderTerminal = useCallback(() => {
    if (!currentId) {
      return (
        <div className="flex items-center justify-center h-full">
          {machines.length === 0 ? (
            <button
              onClick={() => {
                router.push("/");
              }}
              className="text-2xl"
            >
              请创建一个机器
            </button>
          ) : (
            <div className="text-2xl">请选择一个机器</div>
          )}
        </div>
      );
    }
    return <MachineTerminalPage id={currentId} />;
  }, [currentId, machines]);

  const onClick = (id: string) => {
    setCurrentId(id);
  };

  return (
    <TerminalLayout>
      <div className="flex flex-row overflow-scroll h-full">
        <MachineActiveListSidebar machines={machines} onClick={onClick} />
        <div className="flex-1">{renderTerminal()}</div>
      </div>
    </TerminalLayout>
  );
}
