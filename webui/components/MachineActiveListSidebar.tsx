import { MachineActiveType } from "@/api/internal/model/response/machine";
import { Card } from "@heroui/react";
import clsx from "clsx";
import ReactCountryFlag from "react-country-flag";

interface MachineActiveListSidebarProps {
  className?: string;
  machines: Array<MachineActiveType>;
  onClick: (id: string) => void;
}
export default function MachineActiveListSidebar({
  className,
  machines,
  onClick,
}: MachineActiveListSidebarProps) {
  if (machines.length === 0) {
    return;
  }

  return (
    <div
      className={clsx(
        "flex flex-col max-h-screen overflow-scroll w-1/6 gap-4 p-2",
        {
          className,
        }
      )}
    >
      {machines.map((machine) => (
        <div
          className="flex flex-row items-center"
          key={machine.id}
          onClick={() => onClick(machine.id)}
        >
          <Card className="flex flex-row items-center justify-around w-full h-12 hover:scale-105 cursor-pointer">
            <ReactCountryFlag countryCode={machine.location} />
            <span>{machine.name}</span>
          </Card>
        </div>
      ))}
    </div>
  );
}
