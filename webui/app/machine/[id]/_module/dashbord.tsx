"use client";
import { Line } from "@ant-design/charts";
import { Card } from "@heroui/react";
import clsx from "clsx";
import { useTheme } from "next-themes";
import { useCallback, useEffect, useState } from "react";
import ReactCountryFlag from "react-country-flag";
import { motion } from "framer-motion";
import { GaugeComponent } from "react-gauge-component";
import {
  FaArrowRightArrowLeft,
  FaCodepen,
  FaEye,
  FaEyeSlash,
  FaFloppyDisk,
  FaMemory,
  FaMicrochip,
  FaServer,
} from "react-icons/fa6";
import { MachineInfoType } from "@/api/internal/model/response/machine";
import { api } from "@/api/instance";
import { PopMsg, PopMsgErr } from "@/store/pops";
import { CurrentRuntimeInfoType } from "@/api/internal/model/response/metric";
import MachineStatus, { MachineStatusType } from "@/components/MachineStatus";
import { FadeContentDefault } from "@/components/FadeContent";

export default function MachineDashboardPage({ id }: { id: string }) {
  const theme = useTheme();
  const [showMachineInfo, setShowMachineInfo] = useState(true);
  const [machineStatus, setMachineStatus] =
    useState<MachineStatusType>("loading");
  const toggleShowMachineInfo = () => {
    setShowMachineInfo((value) => !value);
  };

  const [machineInfo, setMachineInfo] = useState<MachineInfoType | null>(null);
  const [curRuntimeInfo, setCurRuntimeInfo] =
    useState<CurrentRuntimeInfoType | null>(null);

  const [cpuHistory, setCPUHistory] = useState<Array<HistoryData> | null>(null);
  const [memoryHistory, setMemoryHistory] = useState<Array<HistoryData> | null>(
    null
  );
  const [swapHistory, setSwapHistory] = useState<Array<HistoryData> | null>(
    null
  );
  const [networkHistory, setNetworkHistory] =
    useState<Array<HistoryData> | null>(null);
  const [diskHistory, setDiskHistory] = useState<Array<HistoryData> | null>(
    null
  );

  useEffect(() => {
    const ctrl = new AbortController();
    api.authService.ping();

    api.metricService.getCurrentRuntimeInfo({
      id: id,
      onmessage: (data) => {
        data.online ? setMachineStatus("online") : setMachineStatus("offline");
        setCurRuntimeInfo(data);
      },
      onerror: (err) => {
        console.log(err);
      },
      signal: ctrl.signal,
    });

    return () => {
      ctrl.abort();
    };
  }, []);

  useEffect(() => {
    const fetchMachineInfo = async () => {
      const res = await api.machineService.info(id);
      if (res.code !== 200) {
        PopMsg({
          title: "获取机器信息失败",
          type: "danger",
          description: res.message,
        });
        return;
      }
      setMachineInfo(res.data);
    };
    fetchMachineInfo();
  }, []);

  useEffect(() => {
    const fetchHistoryInfo = async () => {
      const res = await api.metricService.getHistoryInfo({
        id: id,
      });
      if (res.code !== 200) {
        PopMsg({
          title: "获取历史信息失败",
          type: "danger",
          description: res.message,
        });
      }
      let cpu: Array<HistoryData> = [];
      let memory: Array<HistoryData> = [];
      let disk: Array<HistoryData> = [];
      let swap: Array<HistoryData> = [];
      let network: Array<HistoryData> = [];

      res.data.list.forEach((history) => {
        const time = extractTime(history.timestamp);

        cpu.push({ time: time, value: history.cpuUsage });
        memory.push({ time: time, value: history.memoryUsage * 100.0 });
        disk.push({ time: time, value: history.diskUsage });
        swap.push({ time: time, value: history.swapUsage * 100.0 });
        network.push({
          time: time,
          value: history.networkDownloadSpeed,
          type: "download",
        });
        network.push({
          time: time,
          value: history.networkUploadSpeed,
          type: "upload",
        });
      });

      setCPUHistory(cpu);
      setMemoryHistory(memory);
      setSwapHistory(swap);
      setNetworkHistory(network);
      setDiskHistory(disk);
    };
    fetchHistoryInfo();
  }, []);

  const renderUsageInfo = useCallback(() => {
    if (!curRuntimeInfo || !curRuntimeInfo?.online) return;
    return (
      <FadeContentDefault>
        <div className="flex flex-col gap-2">
          <div className="text-xl">使用率</div>
          <div className="flex gap-4 items-center flex-wrap">
            <UsageGuage
              className="size-72"
              value={curRuntimeInfo.cpuUsage}
              label="CPU 使用率"
            />
            <UsageGuage
              className="size-72"
              value={curRuntimeInfo.memoryUsage * 100.0}
              label="内存 使用率"
            />
            <UsageGuage
              className="size-72"
              value={curRuntimeInfo.swapUsage * 100.0}
              label="Swap 使用率"
            />
            <UsageGuage
              className="size-72"
              value={curRuntimeInfo.diskUsage}
              label="磁盘使用率"
            />
          </div>
        </div>
      </FadeContentDefault>
    );
  }, [curRuntimeInfo]);

  const renderCPUHistory = useCallback(
    ({ className, height }: GraphProps) => {
      if (!cpuHistory) return;

      return (
        <Card className={className}>
          <Line
            theme={theme.theme}
            title="CPU 用量历史"
            height={height}
            data={cpuHistory}
            encode={{
              x: "time",
              y: "value",
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
    },
    [cpuHistory, theme.theme]
  );

  const renderMemoryHistory = useCallback(
    ({ className, height }: GraphProps) => {
      if (!memoryHistory) return;
      return (
        <Card className={className}>
          <Line
            theme={theme.theme}
            title="内存用量历史"
            data={memoryHistory}
            height={height}
            encode={{
              x: "time",
              y: "value",
            }}
            axis={{
              y: {
                title: "内存用量 (%)",
                min: 0,
                max: 16,
                label: {
                  formatter: (v: string) => `${v}%`,
                },
              },
            }}
            smooth={true}
            tooltip={{
              items: [
                {
                  channel: "y",
                  valueFormatter: (v: number) => `${v}%`,
                },
              ],
            }}
          />
        </Card>
      );
    },
    [memoryHistory, theme.theme]
  );
  const renderSwapHistory = useCallback(
    ({ className, height }: GraphProps) => {
      if (!swapHistory) return;
      return (
        <Card className={className}>
          <Line
            theme={theme.theme}
            title="交换空间用量历史"
            data={swapHistory}
            height={height}
            encode={{
              x: "time",
              y: "value",
            }}
            axis={{
              y: {
                title: "交换空间用量 (%)",
                min: 0,
                max: 4,
                label: {
                  formatter: (v: string) => `${v}%`,
                },
              },
            }}
            smooth={true}
            tooltip={{
              items: [
                {
                  channel: "y",
                  valueFormatter: (v: number) => `${v}%`,
                },
              ],
            }}
          />
        </Card>
      );
    },
    [swapHistory, theme.theme]
  );

  const renderNetworkHistory = useCallback(
    ({ className, height }: GraphProps) => {
      if (!networkHistory) return;
      return (
        <Card className={className}>
          <Line
            theme={theme.theme}
            title="网络用量历史"
            data={networkHistory}
            height={height}
            encode={{
              x: "time",
              y: "value",
              color: "type",
            }}
            axis={{
              y: {
                title: "网络速度 (KB/s)",
                min: 0,
                max: 8,
                label: {
                  formatter: (v: string) => `${v}KB/s`,
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
                  valueFormatter: (v: number) => `${v}KB/s`,
                },
              ],
            }}
            legend={{
              position: "top",
            }}
          />
        </Card>
      );
    },
    [networkHistory, theme.theme]
  );

  const renderDiskHistory = useCallback(
    ({ className, height }: GraphProps) => {
      if (!swapHistory) return;
      return (
        <Card className={className}>
          <Line
            theme={theme.theme}
            title="硬盘用量历史"
            data={diskHistory}
            height={height}
            encode={{
              x: "time",
              y: "value",
            }}
            axis={{
              y: {
                title: "硬盘用量 (%)",
                min: 0,
                max: 4,
                label: {
                  formatter: (v: string) => `${v}%`,
                },
              },
            }}
            smooth={true}
            tooltip={{
              items: [
                {
                  channel: "y",
                  valueFormatter: (v: number) => `${v}%`,
                },
              ],
            }}
          />
        </Card>
      );
    },
    [diskHistory, theme.theme]
  );
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
        <ReactCountryFlag countryCode={machineInfo?.location ?? ""} />
        <span className="text-2xl">{machineInfo?.name}</span>
        <MachineStatus status={machineStatus} />
      </div>
      <div className="flex flex-col gap-2">
        <div className="flex items-center gap-2">
          <div className="text-xl">机器信息</div>
          <button onClick={toggleShowMachineInfo}>
            {showMachineInfo ? <FaEye /> : <FaEyeSlash />}
          </button>
        </div>
        {showMachineInfo && machineInfo && (
          <Card className="flex flex-col gap-2 p-4 text-lg">
            <div className="flex gap-2 items-center">
              <FaServer />
              <div>System: </div>
              <div>
                {machineInfo.osName} {machineInfo.osVersion}
              </div>
            </div>
            <div className="flex gap-2 items-center">
              <FaMicrochip />
              <div>CPU: </div>
              <div>{machineInfo.cpuName}</div>
            </div>
            <div className="flex gap-2 items-center">
              <FaCodepen />
              <div>Kernel: </div>
              <div>{machineInfo.kernelVersion}</div>
            </div>
            <div className="flex gap-2 items-center">
              <FaMemory />
              <div>Memory: </div>
              <div>{Math.ceil(machineInfo.totalMemory)}G</div>
            </div>
            <div className="flex gap-2 items-center">
              <FaArrowRightArrowLeft />
              <div>Swap: </div>
              <div>{machineInfo.totalSwap}G</div>
            </div>
            <div className="flex gap-2 items-center">
              <FaFloppyDisk />
              <div>硬盘: </div>
              <div>{machineInfo.totalDiskSize}G</div>
            </div>
          </Card>
        )}
      </div>

      {renderUsageInfo()}

      <div className="flex flex-col gap-4">
        {renderCPUHistory({ height: 300 })}
        <div className="flex items-center justify-between gap-4">
          {renderMemoryHistory({ className: "w-1/2", height: 300 })}
          {renderSwapHistory({ className: "w-1/2", height: 300 })}
        </div>
      </div>
      <div>{renderNetworkHistory({ height: 400 })}</div>
      <div>{renderDiskHistory({ height: 300 })}</div>
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

interface HistoryData {
  time: string;
  value: number;
  type?: string;
}

function extractTime(dateString: string) {
  const date = new Date(dateString);

  const hours = date.getUTCHours();
  const minutes = date.getUTCMinutes();
  const seconds = date.getUTCSeconds();

  return `${hours}:${minutes}:${seconds}`;
}
interface GraphProps {
  className?: string;
  height?: number;
}
