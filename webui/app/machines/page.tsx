"use client";

export default function MachinesPage() {
  return (
    <div className="min-h-screen bg-slate-950 text-white">
      <div className="flex min-h-[calc(100vh-64px)] items-center justify-center bg-gradient-to-br from-slate-900 via-slate-950 to-black px-6 py-16 text-center">
        <div className="max-w-lg space-y-6">
          <p className="text-sm uppercase tracking-[0.4em] text-slate-400">
            BeEyes Ops Center
          </p>
          <h1 className="text-4xl font-semibold">机器管理</h1>
          <p className="text-base text-slate-300">
            该页面正在建设中，后续将提供主机资源管理、批量操作及节点标签等能力，敬请期待。
          </p>
        </div>
      </div>
    </div>
  );
}

