"use client";
import { api } from "@/api/instance";
import { MetricData } from "@/api/internal/model/response/metric";
import ServerCard from "@/components/ServerCard";
import { PopMsg } from "@/store/pops";
import { Button, Divider } from "@heroui/react";
import { useEffect, useState } from "react";
import { FaServer } from "react-icons/fa6";
import { motion } from "framer-motion";
import { useRouter } from "next/navigation";
import { HomeLayout } from "@/layout/HomeLayout";
import NewMachineDrawer from "@/components/NewMachineDrawer";
import Loading from "@/components/Loading";

export default function Home() {
  const router = useRouter();
  const [serverList, setServerList] = useState<Array<MetricData> | null>(null);
  const [isOpenNewMachineDrawre, setIsOpenNewMachine] = useState(false);

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

  const renameServer = async (id: string, name: string) => {
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
      {serverList === null ? (
        <Loading />
      ) : (
        <section className="flex flex-col">
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
              新建主机
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
          <NewMachineDrawer
            isOpen={isOpenNewMachineDrawre}
            openChange={(value) => setIsOpenNewMachine(value)}
          />
        </section>
      )}
    </HomeLayout>
  );
}
