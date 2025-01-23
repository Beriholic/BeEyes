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

export interface ServerCardProps {
  name: string;
  osName: string;
  osArch: string;
  osVersion: string;
  status: "online" | "offline";
}

export default function ServerCard({
  name,
  status,
  osName,
  osArch,
  osVersion,
}: ServerCardProps) {
  return (
    <div className="border-2 border-foreground-500 rounded-2xl p-4 w-80 flex flex-col">
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-1">
          <ReactCountryFlag
            style={{
              fontSize: "1rem",
            }}
            countryCode="hk"
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
          {status == "online" ? (
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
        <div>{osArch}</div>
      </div>
      <Divider className="my-2 bg-divider" />
      <div className="flex text-sm items-center">
        <FaLocationDot />
        <div className="flex gap-1 items-center">
          <div>IP:</div>
          <ClipedLabel text="42.80.122.15" />
          <Popover placement="right">
            <PopoverTrigger>
              <b className="cursor-grab text-primary">...</b>
            </PopoverTrigger>
            <PopoverContent>
              <div className="px-1 py-2 ">
                <ClipedLabel text="42.80.122.15" />
                <ClipedLabel text="41.72.107.37" />
                <ClipedLabel text="4AA4:2733:412F:EC99:3EB0:98D1:D4DF:7F10" />
              </div>
            </PopoverContent>
          </Popover>
        </div>
      </div>
      <div className="flex gap-4 text-sm">
        <div className="flex items-center gap-1">
          <FaMicrochip />
          <div>2 CPU</div>
        </div>
        <div className="flex items-center gap-1">
          <FaMemory />
          <div>4 GB</div>
        </div>
      </div>
      <div className="flex flex-col gap-y-2 text-xs mt-2">
        <div>
          <div className="flex gap-1">
            <div>CPU: </div>
            <div>11%</div>
          </div>
          <Progress size="sm" value={11} />
        </div>
        <div>
          <div className="flex gap-1">
            <div>内存: </div>
            <div>33%</div>
          </div>
          <Progress size="sm" value={33} />
        </div>
        <div className="flex justify-between">
          <span>网络</span>
          <div className="flex items-center gap-2">
            <div className="flex items-center">
              <FaArrowUp />
              <span>12kb/s</span>
            </div>
            <div className="flex items-center">
              <FaArrowDown />
              <span>11kb/s </span>
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}
