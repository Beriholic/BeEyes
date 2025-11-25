"use client";

import type { MachineTerminalListView } from "@/api/models/MachineTerminalListView";
import { TerminalControllerService } from "@/api/services/TerminalControllerService";
import { useCallback, useEffect, useState } from "react";
import ReactCountryFlag from "react-country-flag";

interface ServerListSidebarProps {
  selectedServerId: number | null;
  onSelectServer: (server: MachineTerminalListView) => void;
}

export function ServerListSidebar({
  selectedServerId,
  onSelectServer,
}: ServerListSidebarProps) {
  const [servers, setServers] = useState<MachineTerminalListView[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [pageIndex, setPageIndex] = useState(0);
  const [pageSize] = useState(10);
  const [total, setTotal] = useState(0);
  const [hostname, setHostname] = useState("");
  const [searchInput, setSearchInput] = useState("");

  const loadServers = useCallback(() => {
    let disposed = false;
    setLoading(true);
    setError(null);

    const request = TerminalControllerService.queryTerminalList(
      pageIndex,
      pageSize,
      hostname || undefined
    );

    request
      .then((response) => {
        if (disposed) return;
        const list = response?.data?.data ?? [];
        setServers(list);
        setTotal(response?.data?.total ?? 0);
      })
      .catch((err) => {
        if (disposed) return;
        const message =
          err instanceof Error ? err.message : "获取服务器列表失败";
        setError(message);
        setServers([]);
      })
      .finally(() => {
        if (disposed) return;
        setLoading(false);
      });

    return () => {
      disposed = true;
      request.cancel();
    };
  }, [pageIndex, pageSize, hostname]);

  useEffect(() => {
    loadServers();
  }, [loadServers]);

  const totalPages = Math.max(Math.ceil(total / pageSize), 1);
  const canPrev = pageIndex > 0;
  const canNext = pageIndex < totalPages - 1;

  const handleSearch = () => {
    setHostname(searchInput);
    setPageIndex(0); // Reset to first page when searching
  };

  const handleKeyPress = (e: React.KeyboardEvent) => {
    if (e.key === "Enter") {
      handleSearch();
    }
  };

  const handleClearSearch = () => {
    setSearchInput("");
    setHostname("");
    setPageIndex(0);
  };

  return (
    <div className="flex h-full flex-col border-r border-white/10 bg-slate-900/50">
      {/* Header */}
      <div className="border-b border-white/10 p-4">
        <h2 className="text-lg font-semibold text-white">服务器列表</h2>
        <p className="mt-1 text-xs text-slate-400">
          共 {total} 台 · 第 {pageIndex + 1}/{totalPages} 页
        </p>
      </div>

      {/* Search */}
      <div className="border-b border-white/10 p-4">
        <div className="flex gap-2">
          <div className="relative flex-1">
            <input
              type="text"
              value={searchInput}
              onChange={(e) => setSearchInput(e.target.value)}
              onKeyPress={handleKeyPress}
              placeholder="搜索主机名..."
              className="w-full rounded-lg border border-white/10 bg-slate-800 px-3 py-2 pl-9 text-sm text-white placeholder-slate-500 transition focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
            />
            <svg
              className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M21 21l-6-6m2-5a7 7 0 11-14 0 7 7 0 0114 0z"
              />
            </svg>
            {searchInput && (
              <button
                type="button"
                onClick={handleClearSearch}
                className="absolute right-2 top-1/2 -translate-y-1/2 rounded p-1 text-slate-400 transition hover:bg-white/10 hover:text-white"
              >
                <svg
                  className="h-4 w-4"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M6 18L18 6M6 6l12 12"
                  />
                </svg>
              </button>
            )}
          </div>
          <button
            type="button"
            onClick={handleSearch}
            className="rounded-lg border border-white/10 bg-indigo-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-600"
          >
            搜索
          </button>
        </div>
      </div>

      {/* Server List */}
      <div className="flex-1 overflow-y-auto">
        {loading ? (
          <div className="flex items-center justify-center p-8 text-sm text-slate-400">
            加载中...
          </div>
        ) : error ? (
          <div className="p-4 text-sm text-rose-400">{error}</div>
        ) : servers.length === 0 ? (
          <div className="flex items-center justify-center p-8 text-sm text-slate-400">
            暂无服务器
          </div>
        ) : (
          <div className="space-y-1 p-2">
            {servers.map((server) => {
              const isSelected = selectedServerId === server.id;
              return (
                <button
                  key={server.id}
                  type="button"
                  onClick={() => onSelectServer(server)}
                  className={`w-full rounded-xl border p-3 text-left transition ${
                    isSelected
                      ? "border-indigo-400 bg-indigo-500/20 shadow-[0_0_20px_rgba(99,102,241,0.15)]"
                      : "border-transparent bg-white/5 hover:border-white/20 hover:bg-white/10"
                  }`}
                >
                  <div className="flex items-start justify-between">
                    <div className="flex-1 min-w-0">
                      <p
                        className={`truncate text-sm font-semibold ${
                          isSelected ? "text-indigo-200" : "text-white"
                        }`}
                      >
                        {server.hostname ?? `ID-${server.id}`}
                      </p>
                      {server.region && (
                        <p className="mt-1 truncate text-xs text-slate-400">
                          <span className="inline-flex items-center gap-1.5">
                            <ReactCountryFlag
                              countryCode={server.region}
                              svg
                              style={{
                                width: "1.5em",
                                height: "1.5em",
                              }}
                            />
                            {server.region}
                          </span>
                        </p>
                      )}
                      {server.description && (
                        <p className="mt-1 truncate text-xs text-slate-500">
                          {server.description}
                        </p>
                      )}
                    </div>
                    {isSelected && (
                      <div className="ml-2 flex h-5 w-5 flex-shrink-0 items-center justify-center rounded-full bg-indigo-500">
                        <svg
                          className="h-3 w-3 text-white"
                          fill="currentColor"
                          viewBox="0 0 20 20"
                        >
                          <path
                            fillRule="evenodd"
                            d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                            clipRule="evenodd"
                          />
                        </svg>
                      </div>
                    )}
                  </div>
                </button>
              );
            })}
          </div>
        )}
      </div>

      {/* Pagination */}
      {!loading && servers.length > 0 && (
        <div className="border-t border-white/10 p-4">
          <div className="flex items-center justify-between gap-2">
            <button
              type="button"
              onClick={() => setPageIndex((prev) => Math.max(0, prev - 1))}
              disabled={!canPrev}
              className="rounded-xl border border-white/10 bg-white/5 px-3 py-2 text-xs font-medium text-slate-300 transition hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
            >
              上一页
            </button>
            <span className="text-xs text-slate-400">
              {pageIndex + 1} / {totalPages}
            </span>
            <button
              type="button"
              onClick={() => setPageIndex((prev) => prev + 1)}
              disabled={!canNext}
              className="rounded-xl border border-white/10 bg-white/5 px-3 py-2 text-xs font-medium text-slate-300 transition hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
            >
              下一页
            </button>
          </div>
        </div>
      )}
    </div>
  );
}
