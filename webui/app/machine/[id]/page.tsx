"use client";
import MachineLayout from "@/layout/MachineLayout";
import { useCallback, useEffect, useState } from "react";
import MachineDashboardPage from "./_module/dashbord";
import MachineTerminalPage from "./_module/terminal";

export default function MachinePage({
  params,
}: {
  params: Promise<{ id: string }>;
}) {
  const [id, setId] = useState<string | null>(null);

  const [moduleIndex, setModuleIndex] = useState(0);

  useEffect(() => {
    const fetchId = async () => {
      const _id = (await params).id;
      setId(_id);
    };
    fetchId();
  }, [params]);

  const renderModule = useCallback(() => {
    if (!id) return <></>;

    switch (moduleIndex) {
      case 0:
        return <MachineDashboardPage id={id} />;
      case 1:
        return <MachineTerminalPage id={id} />;
      default:
        return <></>;
    }
  }, [id, moduleIndex]);

  return (
    <MachineLayout
      moduleIndex={moduleIndex}
      setModuleIndex={(index) => {
        setModuleIndex(index);
      }}
    >
      {renderModule()}
    </MachineLayout>
  );
}
