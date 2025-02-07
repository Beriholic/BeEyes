"use client";
import MachineLayout from "@/layout/MachineLayout";
import { useEffect, useState } from "react";
import MachineDashboardPage from "./_module/dashbord";
import MachineTerminalPage from "./_module/terminal";

export default function MachinePage({
  params,
}: {
  params: Promise<{ id: number }>;
}) {
  const [id, setId] = useState<number | null>(null);

  const machineName = "测试机器";
  const [moduleIndex, setModuleIndex] = useState(0);

  useEffect(() => {
    const fetchId = async () => {
      const _id = (await params).id;
      setId(_id);
    };
    fetchId();
  }, [params]);

  return (
    <MachineLayout
      machineName={machineName}
      moduleIndex={moduleIndex}
      setModuleIndex={(index) => {
        setModuleIndex(index);
      }}
    >
      {moduleIndex === 0 ? (
        <MachineDashboardPage />
      ) : moduleIndex === 1 ? (
        <MachineTerminalPage />
      ) : (
        <></>
      )}
    </MachineLayout>
  );
}
