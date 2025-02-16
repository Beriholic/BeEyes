"use client";
import { Button } from "@heroui/button";
import { Input } from "@heroui/input";
import {
  Drawer,
  DrawerContent,
  DrawerHeader,
  DrawerBody,
  DrawerFooter,
  Select,
  SelectItem,
} from "@heroui/react";
import { motion } from "framer-motion";
import { FaServer, FaEarthAsia, FaCircleNodes, FaPlug } from "react-icons/fa6";
import CopyedText from "./CopyedText";
import { api } from "@/api/instance";
import { PopMsg } from "@/store/pops";
import { useState } from "react";
import LocationSelect from "./LocationSelect";

interface NewMachineDrawerProps {
  isOpen: boolean;
  openChange: (isOpen: boolean) => void;
  callBack?: () => void;
}

interface NewMachineInfoProps {
  name: string;
  location: string;
  nodeName: string;
}

export default function NMachineNewDrawer({
  isOpen,
  openChange,
  callBack,
}: NewMachineDrawerProps) {
  const [newMachineInfo, setNewMachineInfo] = useState<NewMachineInfoProps>({
    name: "",
    location: "",
    nodeName: "",
  });
  const [curApiKey, setCurApiKey] = useState<string | null>(null);
  const newMachine = async () => {
    if (newMachineInfo.name.length === 0) {
      PopMsg({
        type: "danger",
        title: "新建失败",
        description: "主机名称不能为空",
      });
      return;
    }
    if (newMachineInfo.location.length === 0) {
      PopMsg({
        type: "danger",
        title: "新建失败",
        description: "主机位置不能为空",
      });
      return;
    }

    const res = await api.machineService.newMachine({
      name: newMachineInfo.name,
      location: newMachineInfo.location,
      nodeName: newMachineInfo.nodeName,
    });
    if (res.code !== 200) {
      PopMsg({
        type: "danger",
        title: "新建失败",
        description: res.message,
      });
      return;
    }
    setCurApiKey(res.data);
    PopMsg({
      type: "success",
      title: "新建成功",
      description: "请妥善保管API Key",
    });
    callBack?.();
  };

  const closeNewMachineDrawer = () => {
    openChange(false);
    setNewMachineInfo({
      name: "",
      location: "",
      nodeName: "",
    });
    setCurApiKey(null);
  };

  const setName = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewMachineInfo({
      ...newMachineInfo,
      name: e.target.value,
    });
  };
  const setNodeName = (e: React.ChangeEvent<HTMLInputElement>) => {
    setNewMachineInfo({
      ...newMachineInfo,
      nodeName: e.target.value,
    });
  };
  const setLocation = (e: React.ChangeEvent<HTMLSelectElement>) => {
    setNewMachineInfo({
      ...newMachineInfo,
      location: e.target.value,
    });
  };

  return (
    <Drawer isOpen={isOpen} onOpenChange={closeNewMachineDrawer}>
      <DrawerContent>
        {(onClose) => (
          <>
            <DrawerHeader className="flex flex-col gap-1">
              新建主机
            </DrawerHeader>
            <DrawerBody>
              <div className="flex items-center gap-2">
                <FaServer size={24} />
                <Input
                  label="主机名称"
                  value={newMachineInfo.name}
                  onChange={setName}
                />
              </div>
              <div className="flex items-center gap-2">
                <FaEarthAsia size={24} />
                <LocationSelect
                  onChange={setLocation}
                  name={newMachineInfo.location}
                />
              </div>
              <div className="flex items-center gap-2">
                <FaCircleNodes size={24} />
                <Input
                  label="节点名称"
                  value={newMachineInfo.nodeName}
                  onChange={setNodeName}
                />
              </div>
              {curApiKey && (
                <motion.div
                  className="flex flex-col gap-4"
                  initial={{ opacity: 0 }}
                  animate={{ opacity: 1 }}
                >
                  <div className="flex items-center gap-2">
                    <FaPlug size={24} />
                    <h1>API Key</h1>
                  </div>
                  <CopyedText value={curApiKey} />
                </motion.div>
              )}
            </DrawerBody>
            <DrawerFooter>
              <Button color="danger" variant="light" onPress={onClose}>
                关闭
              </Button>
              <Button color="primary" onPress={newMachine}>
                新建
              </Button>
            </DrawerFooter>
          </>
        )}
      </DrawerContent>
    </Drawer>
  );
}
