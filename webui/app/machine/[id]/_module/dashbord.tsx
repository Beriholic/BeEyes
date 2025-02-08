"use client";
import { Line } from "@ant-design/charts";
import { Card } from "@heroui/react";
import clsx from "clsx";
import { useTheme } from "next-themes";
import { useState } from "react";
import ReactCountryFlag from "react-country-flag";
import { motion } from "framer-motion";
import { GaugeComponent } from "react-gauge-component";
import {
  FaArrowRightArrowLeft,
  FaCodepen,
  FaEye,
  FaEyeSlash,
  FaMemory,
  FaMicrochip,
  FaServer,
} from "react-icons/fa6";

const data = {
  machineName: "测试机器",
};

export default function MachineDashboardPage() {
  const [showMachineInfo, setShowMachineInfo] = useState(true);
  const toggleShowMachineInfo = () => {
    setShowMachineInfo((value) => !value);
  };

  return (
    <motion.div
      initial={{ opacity: 0, x: -50 }}
      animate={{ opacity: 1, x: 0 }}
      transition={{
        duration: 0.6,
        ease: "easeOut",
        type: "spring",
        stiffness: 100,
        damping: 10,
      }}
      className="flex flex-col h-screen gap-4"
    >
      <div className="flex items-center gap-2 text-xl">
        <ReactCountryFlag countryCode="cn" />
        <span className="text-2xl">{data.machineName}</span>
      </div>
      <div className="flex flex-col gap-2">
        <div className="flex items-center gap-2">
          <div className="text-xl">机器信息</div>
          <button onClick={toggleShowMachineInfo}>
            {showMachineInfo ? <FaEye /> : <FaEyeSlash />}
          </button>
        </div>
        {showMachineInfo && (
          <Card className="flex flex-col gap-2 p-4 text-lg">
            <div className="flex gap-2 items-center">
              <FaServer />
              <div>System: </div>
              <div>Arch Linux rolling</div>
            </div>
            <div className="flex gap-2 items-center">
              <FaMicrochip />
              <div>CPU: </div>
              <div>AMD Ryzen 7 5800H with Radeon Graphics x86_64</div>
            </div>
            <div className="flex gap-2 items-center">
              <FaCodepen />
              <div>Kernel: </div>
              <div>6.11.5-zen1-1-zen</div>
            </div>
            <div className="flex gap-2 items-center">
              <FaMemory />
              <div>Memory: </div>
              <div>16G</div>
            </div>
            <div className="flex gap-2 items-center">
              <FaArrowRightArrowLeft />
              <div>Swap: </div>
              <div>16G</div>
            </div>
          </Card>
        )}
      </div>

      <div className="flex flex-col gap-2">
        <div className="text-xl">使用率</div>
        <div className="flex gap-4 items-center flex-wrap">
          <UsageGuage className="size-72" value={7} label="CPU 使用率" />
          <UsageGuage className="size-72" value={65} label="内存 使用率" />
          <UsageGuage className="size-72" value={32} label="Swap 使用率" />
          <UsageGuage className="size-72" value={53} label="磁盘 使用率" />
        </div>
      </div>

      <div className="flex flex-col gap-4">
        <CPUUsageHistory height={300} />
        <div className="flex items-center justify-between gap-4">
          <MemoryUsageHistory className="w-1/2" height={300} />
          <SwapUsageHistory className="w-1/2" height={300} />
        </div>
      </div>
      <div>
        <NetworkUsageHistory height={400} />
      </div>
    </motion.div>
  );
}

function UsageGuage({
  className,
  label,
  value,
}: {
  className?: string;
  label: string;
  value: number;
}) {
  return (
    <Card className={clsx(className, "text-on-background overflow-scroll")}>
      <div className="text-center pt-2">{label}</div>
      <GaugeComponent
        type="semicircle"
        arc={{
          colorArray: ["#00FF15", "#FF2121"],
          padding: 0.02,
          subArcs: [{}, {}, {}, {}],
        }}
        labels={{
          valueLabel: {
            style: {
              color: "#000000",
            },
          },
        }}
        pointer={{ type: "blob", animationDelay: 0 }}
        value={value}
      />
    </Card>
  );
}

function CPUUsageHistory({
  className,
  height,
}: {
  className?: string;
  height?: number;
}) {
  const theme = useTheme();
  const data = [
    { time: "08:00", usage: 45 },
    { time: "08:10", usage: 62 },
    { time: "08:20", usage: 58 },
    { time: "08:30", usage: 75 },
    { time: "08:40", usage: 82 },
    { time: "08:50", usage: 70 },
    { time: "09:00", usage: 65 },
    { time: "09:10", usage: 88 },
    { time: "09:20", usage: 95 },
  ];

  return (
    <Card className={className}>
      <Line
        theme={theme.theme}
        title="CPU 用量历史"
        height={height}
        data={data}
        encode={{
          x: "time",
          y: "usage",
        }}
        axis={{
          y: {
            title: "CPU 用量 (%)",
            min: 0,
            max: 100,
            grid: true,
            label: {
              formatter: (v: number) => `${v}%`,
            },
          },
        }}
        scale={{
          y: {
            nice: true,
          },
        }}
        style={{
          smooth: true,
        }}
        tooltip={{
          items: [
            {
              channel: "y",
              valueFormatter: (v) => `${v}%`,
            },
          ],
        }}
      />
    </Card>
  );
}

function MemoryUsageHistory({
  className,
  height,
}: {
  className?: string;
  height?: number;
}) {
  const theme = useTheme();
  const data = [
    { time: "08:00", used: 4.2 },
    { time: "08:10", used: 5.8 },
    { time: "08:20", used: 7.1 },
    { time: "08:30", used: 6.5 },
    { time: "08:40", used: 8.2 },
    { time: "08:50", used: 9.6 },
    { time: "09:00", used: 8.8 },
    { time: "09:10", used: 11.2 },
    { time: "09:20", used: 10.5 },
  ];

  return (
    <Card className={className}>
      <Line
        theme={theme.theme}
        title="内存用量历史"
        data={data}
        height={height}
        encode={{
          x: "time",
          y: "used",
        }}
        axis={{
          y: {
            title: "内存用量 (GB)",
            min: 0,
            max: 16,
            label: {
              formatter: (v: string) => `${v}GB`,
            },
          },
        }}
        smooth={true}
        tooltip={{
          items: [
            {
              channel: "y",
              valueFormatter: (v: number) => `${v}GB`,
            },
          ],
        }}
      />
    </Card>
  );
}

function SwapUsageHistory({
  className,
  height,
}: {
  className?: string;
  height?: number;
}) {
  const theme = useTheme();
  const data = [
    { time: "08:00", used: 0.5 },
    { time: "08:10", used: 0.8 },
    { time: "08:20", used: 1.2 },
    { time: "08:30", used: 0.9 },
    { time: "08:40", used: 1.5 },
    { time: "08:50", used: 1.8 },
    { time: "09:00", used: 1.4 },
    { time: "09:10", used: 2.1 },
    { time: "09:20", used: 1.9 },
  ];

  return (
    <Card className={className}>
      <Line
        theme={theme.theme}
        title="交换空间用量历史"
        data={data}
        height={height}
        encode={{
          x: "time",
          y: "used",
        }}
        axis={{
          y: {
            title: "交换空间用量 (GB)",
            min: 0,
            max: 4,
            label: {
              formatter: (v: string) => `${v}GB`,
            },
          },
        }}
        smooth={true}
        tooltip={{
          items: [
            {
              channel: "y",
              valueFormatter: (v: number) => `${v}GB`,
            },
          ],
        }}
      />
    </Card>
  );
}

function NetworkUsageHistory({
  className,
  height,
}: {
  className?: string;
  height?: number;
}) {
  const theme = useTheme();
  const data = [
    { time: "08:00", type: "下载", value: 2.5 },
    { time: "08:00", type: "上传", value: 0.8 },
    { time: "08:10", type: "下载", value: 3.2 },
    { time: "08:10", type: "上传", value: 1.1 },
    { time: "08:20", type: "下载", value: 4.5 },
    { time: "08:20", type: "上传", value: 1.5 },
    { time: "08:30", type: "下载", value: 3.8 },
    { time: "08:30", type: "上传", value: 1.2 },
    { time: "08:40", type: "下载", value: 5.1 },
    { time: "08:40", type: "上传", value: 1.8 },
    { time: "08:50", type: "下载", value: 4.2 },
    { time: "08:50", type: "上传", value: 1.4 },
    { time: "09:00", type: "下载", value: 3.9 },
    { time: "09:00", type: "上传", value: 1.3 },
    { time: "09:10", type: "下载", value: 5.5 },
    { time: "09:10", type: "上传", value: 2.0 },
    { time: "09:20", type: "下载", value: 4.8 },
    { time: "09:20", type: "上传", value: 1.7 },
  ];

  return (
    <Card className={className}>
      <Line
        theme={theme.theme}
        title="网络用量历史"
        data={data}
        height={height}
        encode={{
          x: "time",
          y: "value",
          color: "type",
        }}
        axis={{
          y: {
            title: "网络速度 (MB/s)",
            min: 0,
            max: 8,
            label: {
              formatter: (v: string) => `${v}MB/s`,
            },
          },
        }}
        color={(type: string) => {
          return type === "下载" ? "#2196F3" : "#4CAF50";
        }}
        smooth={true}
        tooltip={{
          items: [
            {
              channel: "y",
              valueFormatter: (v: number) => `${v}MB/s`,
            },
          ],
        }}
        legend={{
          position: "top",
        }}
      />
    </Card>
  );
}
