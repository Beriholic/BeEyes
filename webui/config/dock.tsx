import { useRouter } from "next/navigation";
import { FaBox, FaGauge, FaTerminal } from "react-icons/fa6";

export default function dockConfig() {
  const router = useRouter();

  const items = [
    {
      icon: <FaGauge size={20} className="text-primary-foreground" />,
      label: "仪表盘",
      onClick: () => router.push("/"),
    },
    {
      icon: <FaBox size={20} className="text-primary-foreground" />,
      label: "API Keys",
      onClick: () => router.push("/keys"),
    },
    {
      icon: <FaTerminal size={20} className="text-primary-foreground" />,
      label: "终端",
      onClick: () => router.push("/term"),
    },
  ];
  return items;
}
