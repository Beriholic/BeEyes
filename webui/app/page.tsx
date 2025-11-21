"use client";

import type { MachineView } from "@/api/models/MachineView";
import { MachineControllerService } from "@/api/services/MachineControllerService";
import { useCallback, useEffect, useMemo, useState } from "react";

const PAGE_SIZE_OPTIONS = [5, 10, 20];

const STATUS_MAP: Record<
  number,
  { label: string; color: string; dot: string }
> = {
  1: {
    label: "在线",
    color: "bg-emerald-500/10 text-emerald-500 border-emerald-500/30",
    dot: "bg-emerald-400",
  },
  2: {
    label: "告警",
    color: "bg-amber-500/10 text-amber-600 border-amber-500/40",
    dot: "bg-amber-400",
  },
  0: {
    label: "离线",
    color: "bg-rose-500/10 text-rose-500 border-rose-500/30",
    dot: "bg-rose-400",
  },
};

const formatStatus = (status?: number | null) => {
  if (status === null || status === undefined) {
    return {
      label: "未知",
      color: "bg-slate-500/10 text-slate-400 border-slate-500/30",
      dot: "bg-slate-400",
    };
  }
  return STATUS_MAP[status] ?? {
    label: `状态${status}`,
    color: "bg-slate-500/10 text-slate-400 border-slate-500/30",
    dot: "bg-slate-400",
  };
};

const formatDate = (value?: string | null) => {
  if (!value) return "-";
  try {
    return new Intl.DateTimeFormat("zh-CN", {
      dateStyle: "medium",
      timeStyle: "short",
    }).format(new Date(value));
  } catch {
    return value;
  }
};

const formatBytes = (bytes?: number) => {
  if (!bytes || bytes <= 0) return "-";
  const units = ["B", "KB", "MB", "GB", "TB", "PB"];
  let value = bytes;
  let index = 0;
  while (value >= 1024 && index < units.length - 1) {
    value /= 1024;
    index += 1;
  }
  return `${value.toFixed(value >= 10 ? 0 : 1)} ${units[index]}`;
};

const summarizeDisks = (disks: MachineView["disks"]) => {
  if (!disks || disks.length === 0) {
    return {
      title: "暂无磁盘信息",
      detail: "",
      hint: "",
      hasMore: false,
    };
  }

  const totalBytes = disks.reduce((sum, disk) => sum + (disk.totalBytes ?? 0), 0);
  const mainDisk = disks[0];
  const hasMore = disks.length > 1;

  return {
    title: mainDisk.diskName ?? "磁盘",
    detail: `${formatBytes(mainDisk.totalBytes)} · ${mainDisk.diskKind ?? "类型未知"}`,
    hint: hasMore
      ? `共 ${disks.length} 块，总计 ${formatBytes(totalBytes)}，点击查看全部`
      : "",
    hasMore,
  };
};

const summarizeNetwork = (interfaces: MachineView["networkInterfaces"]) => {
  if (!interfaces || interfaces.length === 0) {
    return {
      title: "暂无网络信息",
      detail: "",
      hint: "",
      hasMore: false,
    };
  }

  const ipv4List = interfaces.flatMap((item) => item.ipv4Address ?? []);
  const ipv6List = interfaces.flatMap((item) => item.ipv6Address ?? []);
  const primaryIpv4 = ipv4List[0] ?? "-";
  const hasMore =
    ipv4List.length > 1 || ipv6List.length > 0 || interfaces.length > 1;

  return {
    title: primaryIpv4,
    detail: ipv6List[0] ? `IPv6: ${ipv6List[0]}` : "",
    hint: hasMore ? `共有 ${interfaces.length} 个接口，点击查看全部` : "",
    hasMore,
  };
};

export default function HomePage() {
  const [machines, setMachines] = useState<MachineView[]>([]);
  const [pageIndex, setPageIndex] = useState(0);
  const [pageSize, setPageSize] = useState(PAGE_SIZE_OPTIONS[0]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [networkModal, setNetworkModal] = useState<{
    hostname: string;
    interfaces: NonNullable<MachineView["networkInterfaces"]>;
  } | null>(null);
  const [diskModal, setDiskModal] = useState<{
    hostname: string;
    disks: NonNullable<MachineView["disks"]>;
  } | null>(null);

  const loadMachines = useCallback(() => {
    let disposed = false;
    const frame = requestAnimationFrame(() => {
      if (disposed) return;
      setLoading(true);
      setError(null);
    });

    const request = MachineControllerService.getMachineList(
      pageIndex,
      pageSize
    );

    request
      .then((response) => {
        if (disposed) return;
        const list = response?.data?.data ?? [];
        setMachines(list);
        setTotal(response?.data?.total ?? 0);
      })
      .catch((err) => {
        if (disposed) return;
        const message =
          err instanceof Error ? err.message : "获取机器列表失败";
        setError(message);
        setMachines([]);
      })
      .finally(() => {
        if (disposed) return;
        setLoading(false);
      });

    return () => {
      disposed = true;
      cancelAnimationFrame(frame);
      request.cancel();
    };
  }, [pageIndex, pageSize]);

  useEffect(() => loadMachines(), [loadMachines]);

  const totalPages = useMemo(() => {
    if (total <= 0) return 1;
    return Math.max(Math.ceil(total / pageSize), 1);
  }, [total, pageSize]);

  const canPrev = pageIndex > 0;
  const canNext = pageIndex + 1 < totalPages;

  const handlePageChange = (nextIndex: number) => {
    if (nextIndex < 0 || nextIndex >= totalPages) return;
    setPageIndex(nextIndex);
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white">
      <div className="relative overflow-hidden bg-gradient-to-br from-slate-900 via-slate-950 to-black">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_top,_rgba(59,130,246,0.18),_transparent_55%)]" />
        <header className="relative mx-auto flex w-full max-w-6xl flex-col gap-6 px-6 py-16 sm:px-8 lg:px-12">
          <div className="text-sm font-semibold uppercase tracking-[0.6em] text-slate-400">
            BeEyes Ops Center
          </div>
          <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
            <div>
              <h1 className="text-4xl font-semibold leading-tight text-white lg:text-5xl">
                服务器概览
              </h1>
              <p className="mt-4 text-base text-slate-300">
                实时掌握各节点运行态势、健康分布与最后在线时间。
              </p>
            </div>
            <div className="flex items-center gap-4 rounded-2xl border border-white/10 bg-white/5 px-6 py-4 text-sm text-slate-200">
              <div>
                <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
                  总台数
                </p>
                <p className="text-2xl font-semibold">{total}</p>
              </div>
              <div className="h-10 w-px bg-white/10" />
              <div>
                <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
                  当前页
                </p>
                <p className="text-2xl font-semibold">
                  {pageIndex + 1}/{totalPages}
                </p>
              </div>
            </div>
          </div>
        </header>
      </div>

      <main className="mx-auto w-full max-w-6xl px-6 pb-16 sm:px-8 lg:px-12">
        <section className="-mt-10 rounded-3xl border border-white/5 bg-slate-950/60 p-6 shadow-2xl backdrop-blur">
          <div className="flex flex-col gap-4 pb-6 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-lg font-semibold text-white">机器列表</p>
              <p className="text-sm text-slate-400">
                数据分页从 0 开始，当前为第 {pageIndex} 页（显示 {pageSize} 条）
              </p>
            </div>
            <div className="flex items-center gap-3 text-sm text-slate-300">
              <label htmlFor="page-size" className="text-slate-400">
                每页数量
              </label>
              <select
                id="page-size"
                value={pageSize}
                onChange={(event) => {
                  setPageSize(Number(event.target.value));
                  setPageIndex(0);
                }}
                className="rounded-2xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
              >
                {PAGE_SIZE_OPTIONS.map((size) => (
                  <option key={size} value={size} className="text-black">
                    {size}
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="overflow-hidden rounded-2xl border border-white/5 bg-slate-950">
            <table className="min-w-full divide-y divide-white/5 text-left text-sm">
              <thead className="bg-slate-900/80 text-xs uppercase tracking-wide text-slate-400">
                <tr>
                  <th className="px-6 py-4 font-medium">主机名</th>
                  <th className="px-6 py-4 font-medium">描述</th>
                  <th className="px-6 py-4 font-medium">硬件信息</th>
                  <th className="px-6 py-4 font-medium">磁盘</th>
                  <th className="px-6 py-4 font-medium">网络</th>
                  <th className="px-6 py-4 font-medium">最后在线</th>
                  <th className="px-6 py-4 font-medium">状态</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5 text-slate-200">
                {loading ? (
                  <tr>
                    <td colSpan={5} className="px-6 py-16 text-center text-slate-400">
                      正在加载机器列表...
                    </td>
                  </tr>
                ) : error ? (
                  <tr>
                    <td colSpan={5} className="px-6 py-16 text-center text-rose-400">
                      {error}
                    </td>
                  </tr>
                ) : machines.length === 0 ? (
                  <tr>
                    <td colSpan={5} className="px-6 py-16 text-center text-slate-400">
                      暂无数据
                    </td>
                  </tr>
                ) : (
                  machines.map((machine) => {
                    const status = formatStatus(machine.status);
                    const diskInfo = summarizeDisks(machine.disks);
                    const networkInfo = summarizeNetwork(machine.networkInterfaces);
                    const hostnameLabel =
                      machine.hostname ?? `ID-${machine.id ?? "未知"}`;
                    return (
                      <tr key={machine.id ?? machine.hostname} className="hover:bg-white/5">
                        <td className="px-6 py-4">
                          <div className="font-semibold text-white">
                            {machine.hostname ?? "-"}
                          </div>
                          <div className="text-xs text-slate-400">
                            ID: {machine.id ?? "未知"}
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <p className="max-w-xs text-sm text-slate-300">
                            {machine.description ?? "-"}
                          </p>
                        </td>
                        <td className="px-6 py-4">
                          <div className="text-sm text-slate-200">
                            {machine.hardware?.cpuName ?? "-"}
                          </div>
                          <div className="text-xs text-slate-400">
                            {machine.hardware?.cpuArch ?? "未知架构"} ·{" "}
                            {machine.hardware?.cpuCores ?? "-"} 核
                          </div>
                        </td>
                        <td className="px-6 py-4 text-sm">
                          {machine.disks && machine.disks.length > 0 ? (
                            <button
                              type="button"
                              onClick={() =>
                                setDiskModal({
                                  hostname: hostnameLabel,
                                  disks: machine.disks ?? [],
                                })
                              }
                              className="group flex w-full flex-col items-start rounded-2xl border border-transparent px-3 py-2 text-left transition hover:border-indigo-400/60 hover:bg-indigo-500/5"
                            >
                              <span className="font-semibold text-white group-hover:text-indigo-200">
                                {diskInfo.title}
                              </span>
                              {diskInfo.detail && (
                                <span className="mt-1 text-xs text-slate-300 group-hover:text-slate-100">
                                  {diskInfo.detail}
                                </span>
                              )}
                              {diskInfo.hint && (
                                <span className="mt-1 text-[11px] text-slate-500 group-hover:text-slate-200">
                                  {diskInfo.hint}
                                </span>
                              )}
                            </button>
                          ) : (
                            <span className="text-slate-500">暂无磁盘信息</span>
                          )}
                        </td>
                        <td className="px-6 py-4 text-sm">
                          {machine.networkInterfaces &&
                          machine.networkInterfaces.length > 0 ? (
                            <button
                              type="button"
                              onClick={() =>
                                setNetworkModal({
                                  hostname: hostnameLabel,
                                  interfaces: machine.networkInterfaces ?? [],
                                })
                              }
                              className="group flex w-full flex-col items-start rounded-2xl border border-transparent px-3 py-2 text-left transition hover:border-indigo-400/60 hover:bg-indigo-500/5"
                            >
                              <span className="font-semibold text-white group-hover:text-indigo-200">
                                {networkInfo.title}
                              </span>
                              {networkInfo.detail && (
                                <span className="mt-1 text-xs text-slate-300 group-hover:text-slate-100">
                                  {networkInfo.detail}
                                </span>
                              )}
                              {networkInfo.hint && (
                                <span className="mt-1 text-[11px] text-slate-500 group-hover:text-slate-200">
                                  {networkInfo.hint}
                                </span>
                              )}
                            </button>
                          ) : (
                            <span className="text-slate-500">暂无网络信息</span>
                          )}
                        </td>
                        <td className="px-6 py-4 text-sm text-slate-300">
                          {formatDate(machine.lastSeen)}
                        </td>
                        <td className="px-6 py-4">
                          <span
                            className={`inline-flex items-center gap-2 rounded-2xl border px-3 py-1 text-xs font-medium ${status.color}`}
                          >
                            <span className={`h-2 w-2 rounded-full ${status.dot}`} />
                            {status.label}
                          </span>
                        </td>
                      </tr>
                    );
                  })
                )}
              </tbody>
            </table>
          </div>

          <div className="mt-6 flex flex-col gap-4 text-sm text-slate-300 sm:flex-row sm:items-center sm:justify-between">
            <p>
              正在查看第 {pageIndex + 1} / {totalPages} 页，共 {total} 台机器
            </p>
            <div className="flex items-center gap-3">
              <button
                type="button"
                disabled={!canPrev}
                onClick={() => handlePageChange(pageIndex - 1)}
                className="rounded-2xl border border-white/10 px-4 py-2 transition hover:border-indigo-400 hover:text-white disabled:cursor-not-allowed disabled:border-white/5 disabled:text-slate-500"
              >
                上一页
              </button>
              <button
                type="button"
                disabled={!canNext}
                onClick={() => handlePageChange(pageIndex + 1)}
                className="rounded-2xl border border-white/10 px-4 py-2 transition hover:border-indigo-400 hover:text-white disabled:cursor-not-allowed disabled:border-white/5 disabled:text-slate-500"
              >
                下一页
              </button>
            </div>
          </div>
        </section>
      </main>

      {networkModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center px-4 py-10">
          <div
            className="absolute inset-0 bg-black/70 backdrop-blur-sm"
            onClick={() => setNetworkModal(null)}
          />
          <div className="relative z-10 w-full max-w-3xl rounded-3xl border border-white/10 bg-slate-950 p-8 text-white shadow-2xl">
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className="text-xs uppercase tracking-[0.4em] text-slate-400">
                  网络详情
                </p>
                <h3 className="mt-2 text-2xl font-semibold">
                  {networkModal.hostname}
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setNetworkModal(null)}
                className="rounded-full border border-white/10 px-4 py-2 text-sm text-slate-300 transition hover:border-indigo-400 hover:text-white"
              >
                关闭
              </button>
            </div>

            <div className="mt-6 max-h-[60vh] space-y-4 overflow-y-auto pr-2">
              {networkModal.interfaces.map((iface, index) => (
                <div
                  key={`${iface.id ?? iface.interfaceName ?? "iface"}-${index}`}
                  className="rounded-2xl border border-white/10 bg-white/5 p-4"
                >
                  <div className="flex flex-wrap items-center gap-3 text-sm">
                    <span className="rounded-full bg-indigo-500/20 px-3 py-1 text-indigo-300">
                      {iface.interfaceName ?? "未知接口"}
                    </span>
                    <span className="text-xs text-slate-400">
                      ID: {iface.id ?? "无"}
                    </span>
                  </div>
                  <div className="mt-4 space-y-2 text-sm text-slate-200">
                    <div>
                      <p className="text-xs uppercase tracking-[0.3em] text-slate-500">
                        IPv4
                      </p>
                      {iface.ipv4Address && iface.ipv4Address.length > 0 ? (
                        <ul className="mt-2 list-disc space-y-1 pl-5">
                          {iface.ipv4Address.map((ip) => (
                            <li key={ip} className="text-slate-100">
                              {ip}
                            </li>
                          ))}
                        </ul>
                      ) : (
                        <p className="mt-1 text-slate-500">暂无 IPv4 地址</p>
                      )}
                    </div>
                    <div>
                      <p className="text-xs uppercase tracking-[0.3em] text-slate-500">
                        IPv6
                      </p>
                      {iface.ipv6Address && iface.ipv6Address.length > 0 ? (
                        <ul className="mt-2 list-disc space-y-1 pl-5">
                          {iface.ipv6Address.map((ip) => (
                            <li key={ip} className="text-slate-100">
                              {ip}
                            </li>
                          ))}
                        </ul>
                      ) : (
                        <p className="mt-1 text-slate-500">暂无 IPv6 地址</p>
                      )}
                    </div>
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}

      {diskModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center px-4 py-10">
          <div
            className="absolute inset-0 bg-black/70 backdrop-blur-sm"
            onClick={() => setDiskModal(null)}
          />
          <div className="relative z-10 w-full max-w-3xl rounded-3xl border border-white/10 bg-slate-950 p-8 text-white shadow-2xl">
            <div className="flex items-start justify-between gap-4">
              <div>
                <p className="text-xs uppercase tracking-[0.4em] text-slate-400">
                  磁盘详情
                </p>
                <h3 className="mt-2 text-2xl font-semibold">
                  {diskModal.hostname}
                </h3>
              </div>
              <button
                type="button"
                onClick={() => setDiskModal(null)}
                className="rounded-full border border-white/10 px-4 py-2 text-sm text-slate-300 transition hover:border-indigo-400 hover:text-white"
              >
                关闭
              </button>
            </div>

            <div className="mt-6 max-h-[60vh] space-y-4 overflow-y-auto pr-2">
              {diskModal.disks.map((disk, index) => (
                <div
                  key={`${disk.diskName ?? "disk"}-${index}`}
                  className="rounded-2xl border border-white/10 bg-white/5 p-4"
                >
                  <div className="flex flex-wrap items-center gap-3 text-sm">
                    <span className="rounded-full bg-emerald-500/20 px-3 py-1 text-emerald-200">
                      {disk.diskName ?? "未知磁盘"}
                    </span>
                    <span className="text-xs text-slate-400">
                      {disk.diskKind ?? "类型未知"}
                    </span>
                    <span className="text-xs text-slate-400">
                      {disk.fileSystem ?? "文件系统未知"}
                    </span>
                  </div>
                  <div className="mt-3 text-sm text-slate-200">
                    容量：{formatBytes(disk.totalBytes)}
                  </div>
                </div>
              ))}
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
