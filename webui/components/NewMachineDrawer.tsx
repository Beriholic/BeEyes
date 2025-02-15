"use client";
import { Button } from "@heroui/button";
import { Input } from "@heroui/input";
import {
  Drawer,
  DrawerContent,
  DrawerHeader,
  DrawerBody,
  DrawerFooter,
} from "@heroui/react";
import { motion } from "framer-motion";
import { FaServer, FaEarthAsia, FaCircleNodes, FaPlug } from "react-icons/fa6";
import CopyedText from "./CopyedText";
import { api } from "@/api/instance";
import { PopMsg } from "@/store/pops";
import { useState } from "react";

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

export default function NewMachineDrawer({
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
    if (callBack) {
      callBack();
    }
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
                  onChange={(e) => {
                    setNewMachineInfo({
                      ...newMachineInfo,
                      name: e.target.value,
                    });
                  }}
                />
              </div>
              <div className="flex items-center gap-2">
                <FaEarthAsia size={24} />
                <Input
                  label="地区"
                  value={newMachineInfo.location}
                  onChange={(e) => {
                    setNewMachineInfo({
                      ...newMachineInfo,
                      location: e.target.value,
                    });
                  }}
                />
              </div>
              <div className="flex items-center gap-2">
                <FaCircleNodes size={24} />
                <Input
                  label="节点名称"
                  value={newMachineInfo.nodeName}
                  onChange={(e) => {
                    setNewMachineInfo({
                      ...newMachineInfo,
                      nodeName: e.target.value,
                    });
                  }}
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
