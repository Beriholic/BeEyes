"use client";
import { api } from "@/api/instance";
import { MetricData } from "@/api/internal/model/response/metric";
import ServerCard from "@/components/ServerCard";
import { PopMsg } from "@/store/pops";
import { Divider, Navbar, Spinner } from "@heroui/react";
import { useEffect, useState } from "react";
import { FaServer } from "react-icons/fa6";
import { AnimatePresence, motion } from "framer-motion";
import { useRouter } from "next/navigation";
import { HomeLayout } from "@/layout/HomeLayout";

export default function Home() {
  const router = useRouter();
  const updateDelay = 3000;
  const [serverList, setServerList] = useState<Array<MetricData>>([]);

  useEffect(() => {
    const interval = setInterval(async () => {
      const res = await api.metricService.getAll();
      if (res.code !== 200) {
        PopMsg({
          type: "danger",
          title: "获取主机列表失败",
          description: res.message,
        });
        setServerList([]);
        return;
      }
      setServerList(res.data);
    }, updateDelay);
    return () => clearInterval(interval);
  }, []);

  const renameServer = async (id: number, name: string) => {
    const res = await api.clientService.rename({
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
            <div className="flex gap-2 px-8 py-2 items-center">
              <FaServer size={24} />
              <b className="text-xl">主机列表</b>
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
      </section>
    </HomeLayout>
  );
}
