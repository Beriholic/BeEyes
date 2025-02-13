"use client";

import {
  Button,
  Card,
  Divider,
  Input,
  Modal,
  ModalBody,
  ModalContent,
  ModalFooter,
  ModalHeader,
  Popover,
  PopoverContent,
  PopoverTrigger,
  Progress,
  Tooltip,
  useDisclosure,
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
  FaPen,
} from "react-icons/fa6";
import { ClipedLabel } from "./CilpedLabel";
import { MetricData } from "@/api/internal/model/response/metric";
import { useMemo, useState } from "react";
import { totalmem } from "os";

export default function ServerCard({
  data,
  rename,
  onClick,
}: {
  data: MetricData;
  rename: (name: string) => void;
  onClick: () => void;
}) {
  const {
    online,
    name,
    location,
    osName,
    kernelVersion,
    osVersion,
    cpuName,
    cpuArch,
    cpuCoreCount,
    totalMemory,
    // totalSwap,
    // totalDisk,
    cpuUsage,
    memoryUsage,
    swapUsage,
    networkUploadSpeed,
    networkDownloadSpeed,
    ipList,
  } = data;

  const {
    isOpen: isOpenRename,
    onOpen: onOpenRename,
    onOpenChange: onOpenChangeRename,
  } = useDisclosure();
  const [newName, setNewName] = useState("");

  const renameServer = () => {
    rename(newName);
    setNewName("");
  };

  return (
    <Card className="rounded-2xl p-4 w-80 flex flex-col">
      <div className="flex justify-between items-center">
        <div className="flex items-center gap-1 flex-1 min-w-0">
          <ReactCountryFlag
            style={{
              fontSize: "1rem",
            }}
            countryCode={location}
          />
          <Tooltip content={name}>
            <div
              className="text-xl cursor-grab hover:underline truncate flex-1 min-w-0"
              onClick={onClick}
            >
              {name}
            </div>
          </Tooltip>
          <button onClick={onOpenRename}>
            <FaPen className="size-3 mx-1" />
          </button>
        </div>
        <div className="flex-shrink-0 pl-2">
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
        <Popover placement="right">
          <PopoverTrigger>
            <div className="cursor-grab">
              {osName} {osVersion}
            </div>
          </PopoverTrigger>
          <PopoverContent>
            <div className="px-1 py-2 ">{kernelVersion}</div>
          </PopoverContent>
        </Popover>

        <div>{cpuArch}</div>
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
              <div className="cursor-grab">{cpuCoreCount} Core</div>
            </PopoverTrigger>
            <PopoverContent>
              <div className="px-1 py-2 ">
                {cpuName} {cpuArch}
              </div>
            </PopoverContent>
          </Popover>
        </div>
        <div className="flex items-center gap-1">
          <FaMemory />
          <div>{totalMemory} GB</div>
        </div>
      </div>
      <div className="flex flex-col gap-y-2 text-xs mt-2">
        <div>
          <div className="flex gap-1">
            <div>CPU: </div>
            <div>{cpuUsage}%</div>
          </div>
          <Progress size="sm" value={cpuUsage} />
        </div>
        <div>
          <div className="flex gap-1">
            <div>内存: </div>
            <div>{(memoryUsage * 100).toFixed(0)}%</div>
          </div>
          <Progress size="sm" value={memoryUsage * 100} />
        </div>
        <div>
          <div className="flex gap-1">
            <div>Swap: </div>
            <div>{(swapUsage * 100).toFixed(0)}%</div>
          </div>
          <Progress size="sm" value={swapUsage * 100} />
        </div>
        <div className="flex justify-between">
          <span>网络</span>
          <div className="flex items-center gap-2">
            <div className="flex items-center">
              <FaArrowUp />
              <div>{networkUploadSpeed} kb/s</div>
            </div>
            <div className="flex items-center">
              <FaArrowDown />
              <div>{networkDownloadSpeed} kb/s</div>
            </div>
          </div>
        </div>
      </div>
      <Modal isOpen={isOpenRename} onOpenChange={onOpenChangeRename}>
        <ModalContent>
          {(onClose) => (
            <>
              <ModalHeader className="flex flex-col gap-1">
                重命名主机名称
              </ModalHeader>
              <ModalBody>
                <Input
                  placeholder="新名称"
                  value={newName}
                  onChange={(e) => setNewName(e.target.value)}
                />
              </ModalBody>
              <ModalFooter>
                <Button color="default" variant="light" onPress={onClose}>
                  取消
                </Button>
                <Button
                  color="primary"
                  onPress={() => {
                    renameServer();
                    onClose();
                  }}
                >
                  确认
                </Button>
              </ModalFooter>
            </>
          )}
        </ModalContent>
      </Modal>
    </Card>
  );
}
