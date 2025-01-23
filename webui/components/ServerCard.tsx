"use client";

import {
  Divider,
  Popover,
  PopoverContent,
  PopoverTrigger,
  Progress,
} from "@heroui/react";
import ReactCountryFlag from "react-country-flag";
import {
  FaArrowDown,
  FaArrowUp,
  FaCirclePause,
  FaCirclePlay,
  FaLocationDot,
  FaMemory,
  FaMicrochip,
} from "react-icons/fa6";
import { ClipedLabel } from "./CilpedLabel";
import { MetricData } from "@/api/internal/model/response/metric";
import { useMemo } from "react";

export default function ServerCard({ data }: { data: MetricData }) {
  const {
    online,
    name,
    location,
    osName,
    osArch,
    osVersion,
    osBitSize,
    cpuName,
    ipList,
    cpuCoreCount,
    memorySize,
    cpuUsage,
    memoryUsage,
    networkUploadSpeed,
    networkDownloadSpeed,
  } = data;

  const cpuUsagePercent: number = useMemo(() => {
    return cpuUsage * 100;
  }, [cpuUsage]);
  const memoryUsagePercent = useMemo(() => {
    return (memoryUsage / memorySize) * 100;
  }, [memoryUsage]);

  return (
    <div className="border-2 border-foreground-500 rounded-2xl p-4 w-80 flex flex-col">
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-1">
          <ReactCountryFlag
            style={{
              fontSize: "1rem",
            }}
            countryCode={location}
          />
          <div
            className="text-xl cursor-grab hover:underline"
            onClick={() => {
              //TODO navigate to machine info page
            }}
          >
            {name}
          </div>
        </div>
        <div>
          {online ? (
            <div className="text-success-500 flex items-center gap-2">
              <FaCirclePlay />
              <div>运行中</div>
            </div>
          ) : (
            <div className="text-danger-500 flex items-center gap-1">
              <FaCirclePause />
              <div>关闭</div>
            </div>
          )}
        </div>
      </div>
      <div className="text-xs flex items-center justify-between">
        <div>
          {osName} {osVersion}
        </div>
        <div>{osBitSize} 位</div>
      </div>
      <Divider className="my-2 bg-divider" />
      <div className="flex text-sm items-center">
        <FaLocationDot />
        <div className="flex gap-1 items-center">
          <div>IP:</div>
          <ClipedLabel text={ipList[0]} />
          <Popover placement="right">
            <PopoverTrigger>
              <b className="cursor-grab text-primary">...</b>
            </PopoverTrigger>
            <PopoverContent>
              <div className="px-1 py-2 ">
                {ipList.map((ip, index) => (
                  <div key={index}>
                    <ClipedLabel text={ip} />
                  </div>
                ))}
              </div>
            </PopoverContent>
          </Popover>
        </div>
      </div>
      <div className="flex gap-4 text-sm">
        <div className="flex items-center gap-1">
          <FaMicrochip />
          <Popover placement="right">
            <PopoverTrigger>
              <div className="cursor-grab">{cpuCoreCount} CPU</div>
            </PopoverTrigger>
            <PopoverContent>
              <div className="px-1 py-2 ">
                {cpuName} {osArch}
              </div>
            </PopoverContent>
          </Popover>
        </div>
        <div className="flex items-center gap-1">
          <FaMemory />
          <div>{memorySize.toFixed(2)} GB</div>
        </div>
      </div>
      <div className="flex flex-col gap-y-2 text-xs mt-2">
        <div>
          <div className="flex gap-1">
            <div>CPU: </div>
            <div>{cpuUsagePercent.toFixed(2)}%</div>
          </div>
          <Progress size="sm" value={cpuUsagePercent} />
        </div>
        <div>
          <div className="flex gap-1">
            <div>内存: </div>
            <div>{memoryUsagePercent.toFixed(2)}%</div>
          </div>
          <Progress size="sm" value={memoryUsagePercent} />
        </div>
        <div className="flex justify-between">
          <span>网络</span>
          <div className="flex items-center gap-2">
            <div className="flex items-center">
              <FaArrowUp />
              <div>{networkUploadSpeed.toFixed(2)} kb/s</div>
            </div>
            <div className="flex items-center">
              <FaArrowDown />
              <div>{networkDownloadSpeed.toFixed(2)} kb/s</div>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
