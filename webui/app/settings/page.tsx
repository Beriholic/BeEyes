"use client";

export default function SettingsPage() {
  return (
    <div className="min-h-screen bg-slate-950">
      <div className="flex min-h-[calc(100vh-64px)] items-center justify-center bg-gradient-to-br from-slate-900 via-slate-950 to-black px-6 py-16 text-center text-white">
        <div className="max-w-lg space-y-6">
          <p className="text-sm uppercase tracking-[0.4em] text-slate-400">
            BeEyes Ops Center
          </p>
          <h1 className="text-4xl font-semibold">系统设置</h1>
          <p className="text-base text-slate-300">
            设置页面正在建设中，后续将提供个性化面板、偏好与安全选项。
          </p>
        </div>
      </div>
    </div>
  );
}

