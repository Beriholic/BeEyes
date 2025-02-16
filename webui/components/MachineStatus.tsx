import { FaCirclePause, FaCirclePlay } from "react-icons/fa6";

export type MachineStatusType = "online" | "offline" | "loading";

interface MachineStatusProps {
  status: MachineStatusType;
}
export default function MachineStatus({ status }: MachineStatusProps) {
  switch (status) {
    case "online":
      return (
        <div className="text-success-500 flex items-center gap-2">
          <FaCirclePlay />
          <div>运行中</div>
        </div>
      );
    case "loading":
      return (
        <div className="text-warning-500 flex items-center gap-2">
          <FaCirclePlay />
          <div>加载中</div>
        </div>
      );
    default:
      return (
        <div className="text-danger-500 flex items-center gap-1">
          <FaCirclePause />
          <div>关闭</div>
        </div>
      );
  }
}
