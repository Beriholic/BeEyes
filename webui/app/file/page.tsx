"use client";

import type { MachineTerminalListView } from "@/api/models/MachineTerminalListView";
import { ServerListSidebar } from "@/components/ServerListSidebar";
import { SSHConfigModal } from "@/components/SSHConfigModal";
import { FileManager } from "@/components/FileManager";
import { useState } from "react";

export default function FilePage() {
  const [selectedServer, setSelectedServer] =
    useState<MachineTerminalListView | null>(null);
  const [showConfigModal, setShowConfigModal] = useState(false);

  const handleSelectServer = (server: MachineTerminalListView) => {
    setSelectedServer(server);
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

      {/* Right Panel - File Manager */}
      <div className="flex flex-1 flex-col">
        {selectedServer ? (
          <FileManager
            serverId={selectedServer.id!}
            hostname={
              selectedServer.hostname ?? `Server ${selectedServer.id}`
            }
            onOpenSSHConfig={() => setShowConfigModal(true)}
          />
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
                    d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z"
                  />
                </svg>
              </div>
              <h2 className="text-xl font-semibold text-white">
                选择一台服务器
              </h2>
              <p className="text-sm text-slate-400">
                从左侧列表中选择一台服务器，即可浏览和管理远程文件
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
