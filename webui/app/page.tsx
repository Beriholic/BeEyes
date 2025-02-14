"use client";
import { api } from "@/api/instance";
import { MetricData } from "@/api/internal/model/response/metric";
import ServerCard from "@/components/ServerCard";
import { PopMsg } from "@/store/pops";
import {
  Button,
  Divider,
  Drawer,
  DrawerBody,
  DrawerContent,
  DrawerFooter,
  DrawerHeader,
  Input,
  Spinner,
} from "@heroui/react";
import { useActionState, useEffect, useState } from "react";
import { FaCircleNodes, FaEarthAsia, FaPlug, FaServer } from "react-icons/fa6";
import { AnimatePresence, motion } from "framer-motion";
import { useRouter } from "next/navigation";
import { HomeLayout } from "@/layout/HomeLayout";
import CopyedText from "@/components/CopyedText";

export default function Home() {
  const router = useRouter();
  const [serverList, setServerList] = useState<Array<MetricData>>([]);
  const [isOpenNewMachineDrawre, setIsOpenNewMachine] = useState(false);
  const [newMachineInfo, setNewMachineInfo] = useState<NewMachineInfoProps>({
    name: "",
    location: "",
    nodeName: "",
  });
  const [curApiKey, setCurApiKey] = useState<string | null>(null);

  useEffect(() => {
    const ctrl = new AbortController();
    api.metricService.getClientMetricList({
      onmessage: (data) => {
        setServerList(data);
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
  };

  const closeNewMachineDrawer = () => {
    setIsOpenNewMachine(false);
    setNewMachineInfo({
      name: "",
      location: "",
      nodeName: "",
    });
    setCurApiKey(null);
  };

  const renameServer = async (id: number, name: string) => {
    const res = await api.machineService.rename({
      id: id,
      name: name,
    });
    if (res.code !== 200) {
      PopMsg({
        type: "danger",
        title: "重命名失败",
        description: res.message,
      });
      return;
    }
    PopMsg({
      type: "success",
      title: "重命名成功",
      description: "等待数据刷新",
    });
  };

  return (
    <HomeLayout>
      <section className="flex flex-col">
        {serverList.length > 0 ? (
          <>
            <div className="flex gap-2 px-8 py-2 items-center justify-between">
              <div className="flex items-center gap-2">
                <FaServer size={24} />
                <b className="text-xl">主机列表</b>
              </div>
              <Button
                variant="bordered"
                color="primary"
                onPress={() => setIsOpenNewMachine(true)}
              >
                新建
              </Button>
            </div>
            <Divider className="bg-divider" />
            <motion.div
              className="p-4 grid grid-cols-4 gap-x-4 gap-y-8"
              initial={{ opacity: 0.4, y: -20 }}
              animate={{ opacity: 1, y: 0 }}
              exit={{ opacity: 0, y: 20, scale: 0.5 }}
            >
              {serverList.map((server) => {
                return (
                  <ServerCard
                    key={server.id}
                    data={server}
                    rename={(name) => {
                      renameServer(server.id, name);
                    }}
                    onClick={() => {
                      router.push(`/machine/${server.id}`);
                    }}
                  />
                );
              })}
            </motion.div>
          </>
        ) : (
          <AnimatePresence>
            <motion.div
              className="flex flex-col gap-2 justify-center fixed top-1/2 left-1/2"
              initial={{ opacity: 0, y: -50, scale: 0.3 }}
              animate={{ opacity: 1, y: 0, scale: 1 }}
              exit={{ opacity: 0, y: 20, scale: 0.5 }}
            >
              <Spinner size="lg" />
              <div className="text-xl">加载中</div>
            </motion.div>
          </AnimatePresence>
        )}

        <Drawer
          isOpen={isOpenNewMachineDrawre}
          onOpenChange={closeNewMachineDrawer}
        >
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
      </section>
    </HomeLayout>
  );
}

interface NewMachineInfoProps {
  name: string;
  location: string;
  nodeName: string;
}
