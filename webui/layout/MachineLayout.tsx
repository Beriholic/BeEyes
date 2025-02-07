import MachineSidebar from "@/components/MachineSidebar";

export default function MachineLayout({
  children,
  machineName,
  moduleIndex,
  setModuleIndex,
}: {
  children: React.ReactNode;
  machineName: string;
  moduleIndex: number;
  setModuleIndex: (index: number) => void;
}) {
  return (
    <div className="flex flex-row">
      <MachineSidebar
        title={machineName}
        curIndex={moduleIndex}
        onIndexChange={setModuleIndex}
      />
      <div className="flex-1 max-h-screen overflow-scroll p-4">{children}</div>
    </div>
  );
}
