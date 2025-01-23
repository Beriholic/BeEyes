"use client";
import ServerCard from "@/components/ServerCard";
import { HomeLayout } from "@/layout/HomeLayout";
import { Divider } from "@heroui/react";
import { FaServer } from "react-icons/fa6";

export default function Home() {
  return (
    <HomeLayout>
      <section className="flex flex-col">
        <div className="flex gap-2 px-8 py-2 items-center">
          <FaServer size={24} />
          <b className="text-xl">主机列表</b>
        </div>
        <Divider className="bg-divider" />
        <div className="p-4 grid grid-cols-4 gap-x-4 gap-y-8">
          <ServerCard
            name="测试主机"
            status="online"
            osName="Ubuntu"
            osArch="x64"
            osVersion="22.04"
          />
        </div>
      </section>
    </HomeLayout>
  );
}
