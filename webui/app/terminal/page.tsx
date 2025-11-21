"use client";

export default function TerminalPage() {
  return (
    <div className="min-h-screen bg-slate-950 text-white">
      <div className="flex min-h-[calc(100vh-64px)] items-center justify-center bg-gradient-to-br from-slate-900 via-slate-950 to-black px-6 py-16 text-center">
        <div className="max-w-lg space-y-6">
          <p className="text-sm uppercase tracking-[0.4em] text-slate-400">
            BeEyes Ops Center
          </p>
          <h1 className="text-4xl font-semibold">远程终端</h1>
          <p className="text-base text-slate-300">
            即将上线安全审计、会话录制与指令白名单等能力，帮助你更高效地远程运维。
          </p>
        </div>
      </div>
    </div>
  );
}

