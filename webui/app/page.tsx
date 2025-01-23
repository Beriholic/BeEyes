"use client";
import { api } from "@/api/instance";
import {
  MetricData,
  MetricServiceResponse,
} from "@/api/internal/model/response/metric";
import ServerCard from "@/components/ServerCard";
import { HomeLayout } from "@/layout/HomeLayout";
import { PopMsg } from "@/store/pops";
import { divider, Divider } from "@heroui/react";
import { useEffect, useState } from "react";
import { FaServer } from "react-icons/fa6";

export default function Home() {
  const updateDelay = 2000;
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

  return (
    <HomeLayout>
      <section className="flex flex-col">
        <div className="flex gap-2 px-8 py-2 items-center">
          <FaServer size={24} />
          <b className="text-xl">主机列表</b>
        </div>
        <Divider className="bg-divider" />

        <div className="p-4 grid grid-cols-4 gap-x-4 gap-y-8">
          {serverList.map((server) => {
            return <ServerCard key={server.id} data={server} />;
          })}
        </div>
      </section>
    </HomeLayout>
  );
}
