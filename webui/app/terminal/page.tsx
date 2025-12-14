"use client";

import type { MachineTerminalListView } from "@/api/models/MachineTerminalListView";
import { ServerListSidebar } from "@/components/ServerListSidebar";
import { SSHConfigModal } from "@/components/SSHConfigModal";
import dynamic from "next/dynamic";
import { useState } from "react";
import ReactCountryFlag from "react-country-flag";

const TerminalWindow = dynamic(
  () => import("@/components/TerminalWindow").then((mod) => mod.TerminalWindow),
  { ssr: false }
);

export default function TerminalPage() {
  const [selectedServer, setSelectedServer] =
    useState<MachineTerminalListView | null>(null);
  const [showTerminal, setShowTerminal] = useState(false);
  const [showConfigModal, setShowConfigModal] = useState(false);

  const handleSelectServer = (server: MachineTerminalListView) => {
    setSelectedServer(server);
    setShowTerminal(false); // Reset terminal when switching servers
  };

  const handleConnect = () => {
    if (selectedServer?.id) {
      setShowTerminal(true);
    }
  };

  return (
    <div className="flex h-[calc(100vh-64px)] w-full overflow-hidden bg-slate-950">
      {/* Left Sidebar - Server List */}
      <div className="w-80 shrink-0">
        <ServerListSidebar
          selectedServerId={selectedServer?.id ?? null}
          onSelectServer={handleSelectServer}
        />
      </div>

      {/* Right Panel - Server Info & Terminal */}
      <div className="flex flex-1 flex-col">
        {selectedServer ? (
          <>
            {/* Server Info Panel */}
            {!showTerminal && (
              <div className="flex h-full items-center justify-center">
                <div className="w-full max-w-2xl space-y-6 p-8">
                  {/* Server Info Card */}
                  <div className="rounded-2xl border border-white/10 bg-slate-900/50 p-8">
                    <div className="mb-6 flex items-start justify-between">
                      <div>
                        <h2 className="text-2xl font-semibold text-white">
                          {selectedServer.hostname ??
                            `Server ${selectedServer.id}`}
                        </h2>
                        {selectedServer.region && (
                          <p className="mt-2 text-sm text-slate-400">
                            <span className="inline-flex items-center gap-1.5">
                              <span className="text-base">
                                <ReactCountryFlag
                                  countryCode={selectedServer.region}
                                  svg
                                />
                              </span>
                              {selectedServer.region}
                            </span>
                          </p>
                        )}
                        {selectedServer.description && (
                          <p className="mt-2 text-sm text-slate-500">
                            {selectedServer.description}
                          </p>
                        )}
                      </div>
                      <div className="rounded-xl bg-indigo-500/10 px-3 py-1.5">
                        <span className="text-xs font-medium text-indigo-300">
                          ID: {selectedServer.id}
                        </span>
                      </div>
                    </div>

                    {/* Action Buttons */}
                    <div className="flex gap-3">
                      <button
                        type="button"
                        onClick={() => setShowConfigModal(true)}
                        className="flex-1 rounded-xl border border-white/10 bg-white/5 px-6 py-4 text-sm font-medium text-slate-300 transition hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white"
                      >
                        <div className="flex items-center justify-center gap-2">
                          <svg
                            className="h-5 w-5"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M10.325 4.317c.426-1.756 2.924-1.756 3.35 0a1.724 1.724 0 002.573 1.066c1.543-.94 3.31.826 2.37 2.37a1.724 1.724 0 001.065 2.572c1.756.426 1.756 2.924 0 3.35a1.724 1.724 0 00-1.066 2.573c.94 1.543-.826 3.31-2.37 2.37a1.724 1.724 0 00-2.572 1.065c-.426 1.756-2.924 1.756-3.35 0a1.724 1.724 0 00-2.573-1.066c-1.543.94-3.31-.826-2.37-2.37a1.724 1.724 0 00-1.065-2.572c-1.756-.426-1.756-2.924 0-3.35a1.724 1.724 0 001.066-2.573c-.94-1.543.826-3.31 2.37-2.37.996.608 2.296.07 2.572-1.065z"
                            />
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M15 12a3 3 0 11-6 0 3 3 0 016 0z"
                            />
                          </svg>
                          SSH配置
                        </div>
                      </button>
                      <button
                        type="button"
                        onClick={handleConnect}
                        className="flex-1 rounded-xl bg-indigo-500 px-6 py-4 text-sm font-semibold text-white shadow-lg shadow-indigo-500/30 transition hover:bg-indigo-600 hover:shadow-indigo-500/50"
                      >
                        <div className="flex items-center justify-center gap-2">
                          <svg
                            className="h-5 w-5"
                            fill="none"
                            stroke="currentColor"
                            viewBox="0 0 24 24"
                          >
                            <path
                              strokeLinecap="round"
                              strokeLinejoin="round"
                              strokeWidth={2}
                              d="M8 9l3 3-3 3m5 0h3M5 20h14a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                            />
                          </svg>
                          连接
                        </div>
                      </button>
                    </div>
                  </div>

                  {/* Info Hint */}
                  <div className="rounded-xl border border-blue-500/20 bg-blue-500/5 p-4">
                    <div className="flex gap-3">
                      <svg
                        className="h-5 w-5 shrink-0 text-blue-400"
                        fill="none"
                        stroke="currentColor"
                        viewBox="0 0 24 24"
                      >
                        <path
                          strokeLinecap="round"
                          strokeLinejoin="round"
                          strokeWidth={2}
                          d="M13 16h-1v-4h-1m1-4h.01M21 12a9 9 0 11-18 0 9 9 0 0118 0z"
                        />
                      </svg>
                      <div className="text-sm text-blue-300">
                        <p className="font-medium">使用提示</p>
                        <p className="mt-1 text-blue-300/80">
                          点击“SSH配置”按钮配置服务器连接信息，配置完成后点击“连接”按钮即可开始远程操作
                        </p>
                      </div>
                    </div>
                  </div>
                </div>
              </div>
            )}

            {/* Terminal Window */}
            {showTerminal && (
              <div className="h-full">
                <TerminalWindow
                  serverId={selectedServer.id!}
                  hostname={
                    selectedServer.hostname ?? `Server ${selectedServer.id}`
                  }
                  onBack={() => setShowTerminal(false)}
                />
              </div>
            )}
          </>
        ) : (
          /* Empty State */
          <div className="flex h-full items-center justify-center">
            <div className="max-w-md space-y-4 text-center">
              <div className="mx-auto flex h-16 w-16 items-center justify-center rounded-2xl bg-indigo-500/10">
                <svg
                  className="h-8 w-8 text-indigo-400"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M8 9l3 3-3 3m5 0h3M5 20h14a2 2 0 002-2V6a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
                  />
                </svg>
              </div>
              <h2 className="text-xl font-semibold text-white">
                选择一台服务器
              </h2>
              <p className="text-sm text-slate-400">
                从左侧列表中选择一台服务器，配置 SSH 连接信息后即可开始远程操作
              </p>
            </div>
          </div>
        )}
      </div>

      {/* SSH Config Modal */}
      {selectedServer && (
        <SSHConfigModal
          isOpen={showConfigModal}
          onClose={() => setShowConfigModal(false)}
          serverId={selectedServer.id!}
          hostname={selectedServer.hostname ?? `Server ${selectedServer.id}`}
        />
      )}
    </div>
  );
}
