"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import { MachineControllerService } from "@/api/services/MachineControllerService";
import type { MachineManageView } from "@/api/models/MachineManageView";

const PAGE_SIZE = 20;

interface Props {
  value: string | null;
  onChange: (serverId: string | null) => void;
  placeholder?: string;
  className?: string;
}

export function SearchablePaginatedSelect({
  value,
  onChange,
  placeholder = "请选择服务器",
  className = "",
}: Props) {
  const [open, setOpen] = useState(false);
  const [keyword, setKeyword] = useState("");
  const [pageIndex, setPageIndex] = useState(0);
  const [machines, setMachines] = useState<MachineManageView[]>([]);
  const [totalCount, setTotalCount] = useState(0);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(false);
  const [selectedCache, setSelectedCache] = useState<MachineManageView | null>(null);
  const inputRef = useRef<HTMLInputElement>(null);
  const dropdownRef = useRef<HTMLDivElement>(null);

  const selectedMachine = machines.find(
    (m) => m.id?.toString() === value?.toString(),
  ) ?? (value ? selectedCache : null);

  const loadMachines = useCallback((searchKeyword: string, page: number) => {
    setLoading(true);
    MachineControllerService.getMachineManageList(
      page,
      PAGE_SIZE,
      searchKeyword || undefined,
    )
      .then((res) => {
        const list = res.data?.data ?? [];
        const total = res.data?.total ?? 0;
        setMachines(list);
        setTotalCount(total);
        setHasMore(list.length === PAGE_SIZE && (page + 1) * PAGE_SIZE < total);
      })
      .catch(() => setMachines([]))
      .finally(() => setLoading(false));
  }, []);

  // debounce search
  useEffect(() => {
    const timer = setTimeout(() => {
      setPageIndex(0);
      loadMachines(keyword, 0);
    }, 300);
    return () => clearTimeout(timer);
  }, [keyword, loadMachines]);

  // update cache when machines load
  useEffect(() => {
    if (value && machines.length > 0) {
      const found = machines.find((m) => m.id?.toString() === value?.toString());
      if (found) {
        setSelectedCache(found);
      }
    }
  }, [machines, value]);

  // load on open
  useEffect(() => {
    if (open) {
      loadMachines(keyword, pageIndex);
    }
  }, [open, pageIndex, loadMachines, keyword]);

  // close on outside click
  useEffect(() => {
    if (!open) return;
    const handler = (e: MouseEvent) => {
      if (
        dropdownRef.current &&
        !dropdownRef.current.contains(e.target as Node)
      ) {
        setOpen(false);
      }
    };
    document.addEventListener("mousedown", handler);
    return () => document.removeEventListener("mousedown", handler);
  }, [open]);

  const handleSelect = (machine: MachineManageView) => {
    onChange(machine.id?.toString() ?? null);
    setSelectedCache(machine);
    setOpen(false);
    setKeyword("");
  };

  const totalPages = Math.ceil(totalCount / PAGE_SIZE);

  return (
    <div ref={dropdownRef} className={`relative ${className}`}>
      {/* Trigger */}
      <button
        type="button"
        onClick={() => {
          setOpen((v) => !v);
          if (!open) {
            setKeyword("");
            setPageIndex(0);
          }
          setTimeout(() => inputRef.current?.focus(), 50);
        }}
        className="flex h-11 w-full items-center justify-between rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-left text-sm text-white focus:border-indigo-500 focus:outline-none"
      >
        <span className={selectedMachine ? "text-white" : "text-slate-400"}>
          {selectedMachine?.hostname ??
            selectedMachine?.id?.toString() ??
            placeholder}
        </span>
        <svg
          className="h-4 w-4 text-slate-400"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M19 9l-7 7-7-7"
          />
        </svg>
      </button>

      {/* Dropdown */}
      {open && (
        <div className="absolute z-50 mt-1 w-full rounded-xl border border-white/10 bg-slate-900 shadow-2xl">
          {/* Search input */}
          <div className="flex items-center border-b border-white/5 px-3">
            <svg
              className="h-4 w-4 text-slate-400"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M21 21l-4.35-4.35M17 11A6 6 0 115 11a6 6 0 0112 0z"
              />
            </svg>
            <input
              ref={inputRef}
              type="text"
              value={keyword}
              onChange={(e) => setKeyword(e.target.value)}
              placeholder="搜索服务器..."
              className="ml-2 h-10 flex-1 bg-transparent text-sm text-white placeholder-slate-500 focus:outline-none"
            />
            {loading && (
              <svg
                className="h-4 w-4 animate-spin text-slate-400"
                fill="none"
                viewBox="0 0 24 24"
              >
                <circle
                  className="opacity-25"
                  cx="12"
                  cy="12"
                  r="10"
                  stroke="currentColor"
                  strokeWidth="4"
                />
                <path
                  className="opacity-75"
                  fill="currentColor"
                  d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4z"
                />
              </svg>
            )}
          </div>

          {/* List */}
          <div className="max-h-60 overflow-y-auto">
            {/* All Servers Option */}
            <button
              type="button"
              onClick={() => {
                onChange(null);
                setOpen(false);
                setKeyword("");
              }}
              className={`flex w-full items-center justify-between px-4 py-2.5 text-left text-sm transition hover:bg-white/5 ${
                value === null
                  ? "bg-indigo-500/10 text-indigo-300"
                  : "text-slate-200"
              }`}
            >
              <div>
                <div className="font-medium">全部服务器</div>
                <div className="text-xs text-slate-500">监控所有服务器</div>
              </div>
              {value === null && (
                <svg
                  className="h-4 w-4 text-indigo-400"
                  fill="currentColor"
                  viewBox="0 0 20 20"
                >
                  <path
                    fillRule="evenodd"
                    d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                    clipRule="evenodd"
                  />
                </svg>
              )}
            </button>

            {/* Divider */}
            <div className="h-px bg-white/5" />

            {machines.length === 0 && !loading ? (
              <div className="py-6 text-center text-sm text-slate-400">
                无匹配结果
              </div>
            ) : (
              machines.map((machine) => (
                <button
                  key={machine.id}
                  type="button"
                  onClick={() => handleSelect(machine)}
                  className={`flex w-full items-center justify-between px-4 py-2.5 text-left text-sm transition hover:bg-white/5 ${
                    machine.id?.toString() === value?.toString()
                      ? "bg-indigo-500/10 text-indigo-300"
                      : "text-slate-200"
                  }`}
                >
                  <div>
                    <div className="font-medium">
                      {machine.hostname ?? `Server ${machine.id}`}
                    </div>
                    {machine.region && (
                      <div className="text-xs text-slate-500">
                        {machine.region}
                      </div>
                    )}
                  </div>
                  {machine.id?.toString() === value?.toString() && (
                    <svg
                      className="h-4 w-4 text-indigo-400"
                      fill="currentColor"
                      viewBox="0 0 20 20"
                    >
                      <path
                        fillRule="evenodd"
                        d="M16.707 5.293a1 1 0 010 1.414l-8 8a1 1 0 01-1.414 0l-4-4a1 1 0 011.414-1.414L8 12.586l7.293-7.293a1 1 0 011.414 0z"
                        clipRule="evenodd"
                      />
                    </svg>
                  )}
                </button>
              ))
            )}
          </div>

          {/* Pagination */}
          {totalPages > 1 && (
            <div className="flex items-center justify-between border-t border-white/5 px-3 py-2">
              <span className="text-xs text-slate-500">
                {pageIndex + 1} / {totalPages} · 共 {totalCount} 条
              </span>
              <div className="flex gap-1">
                <button
                  type="button"
                  disabled={pageIndex === 0}
                  onClick={() => setPageIndex((p) => Math.max(0, p - 1))}
                  className="rounded px-2 py-1 text-xs text-slate-400 hover:bg-white/5 disabled:cursor-not-allowed disabled:opacity-30"
                >
                  ‹
                </button>
                {Array.from({ length: Math.min(5, totalPages) }, (_, i) => {
                  let page = i;
                  if (totalPages > 5) {
                    if (pageIndex < 3) page = i;
                    else if (pageIndex > totalPages - 3)
                      page = totalPages - 5 + i;
                    else page = pageIndex - 2 + i;
                  }
                  return (
                    <button
                      key={page}
                      type="button"
                      onClick={() => setPageIndex(page)}
                      className={`h-6 w-6 rounded text-xs transition ${
                        page === pageIndex
                          ? "bg-indigo-500 text-white"
                          : "text-slate-400 hover:bg-white/5"
                      }`}
                    >
                      {page + 1}
                    </button>
                  );
                })}
                <button
                  type="button"
                  disabled={!hasMore && pageIndex === totalPages - 1}
                  onClick={() =>
                    setPageIndex((p) => Math.min(totalPages - 1, p + 1))
                  }
                  className="rounded px-2 py-1 text-xs text-slate-400 hover:bg-white/5 disabled:cursor-not-allowed disabled:opacity-30"
                >
                  ›
                </button>
              </div>
            </div>
          )}
        </div>
      )}
    </div>
  );
}
