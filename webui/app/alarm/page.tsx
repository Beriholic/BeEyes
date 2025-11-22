"use client";

import { PageBackground } from "@/components/PageBackground";

export default function AlarmPage() {
  return (
    <PageBackground>
      <div className="flex min-h-[calc(100vh-64px)] items-center justify-center px-6 py-16 text-center">
        <div className="max-w-lg space-y-6">
          <p className="text-sm uppercase tracking-[0.4em] text-slate-400">
            BeEyes Ops Center
          </p>
          <h1 className="text-4xl font-semibold">告警中心</h1>
          <p className="text-base text-slate-300">
            告警策略、降噪与多渠道通知功能即将推出，帮助你更早感知风险。
          </p>
        </div>
      </div>
    </PageBackground>
  );
}

