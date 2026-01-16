"use client";

import type { MachineView } from "@/api/models/MachineView";
import type { MachineRuntimeInfoDTO } from "@/api/models/MachineRuntimeInfoDTO";
import { MachineControllerService } from "@/api/services/MachineControllerService";
import { MetricControllerService } from "@/api/services/MetricControllerService";
import { Card } from "@/components/Card";
import { Button } from "@/components/Button";
import { PageBackground } from "@/components/PageBackground";
import { StatusBadge } from "@/components/StatusBadge";
import { OSIcon } from "@/components/OSIcon";
import { TimeSeriesChart } from "@/components/TimeSeriesChart";
import Link from "next/link";
import { useParams } from "next/navigation";
import { useEffect, useState } from "react";

type TimeUnit = 0 | 1 | 2 | 3 | 4 | 5;

const PRESETS: { label: string; time: number; unit: TimeUnit }[] = [
  { label: "5秒", time: 5, unit: 0 },
  { label: "5分钟", time: 5, unit: 1 },
  { label: "15分钟", time: 15, unit: 1 },
  { label: "30分钟", time: 30, unit: 1 },
  { label: "1小时", time: 1, unit: 2 },
  { label: "3小时", time: 3, unit: 2 },
  { label: "6小时", time: 6, unit: 2 },
  { label: "12小时", time: 12, unit: 2 },
  { label: "1天", time: 1, unit: 3 },
  { label: "3天", time: 3, unit: 3 },
  { label: "1周", time: 1, unit: 4 },
  { label: "2周", time: 2, unit: 4 },
  { label: "3周", time: 3, unit: 4 },
  { label: "1月", time: 1, unit: 5 },
  { label: "3月", time: 3, unit: 5 },
  { label: "6月", time: 6, unit: 5 },
  { label: "1年", time: 12, unit: 5 },
];

//

export default function MachineDetailPage() {
  const params = useParams();
  const serverId = String(params?.id ?? "");

  const [detail, setDetail] = useState<MachineView | null>(null);
  const [loadingDetail, setLoadingDetail] = useState(false);
  const [errorDetail, setErrorDetail] = useState<string | null>(null);

  const [history, setHistory] = useState<MachineRuntimeInfoDTO[]>([]);
  const [loadingHistory, setLoadingHistory] = useState(false);
  const [errorHistory, setErrorHistory] = useState<string | null>(null);

  const [time, setTime] = useState<number>(1);
  const [timeUnit, setTimeUnit] = useState<TimeUnit>(1);
  const [refreshInterval, setRefreshInterval] = useState<number>(0); // 0 = no auto-refresh

  const REFRESH_OPTIONS = [
    { label: "关闭", value: 0 },
    { label: "开启", value: 5 },
  ];

  const setPreset = (t: number, u: TimeUnit) => {
    setTime(t);
    setTimeUnit(u);
    // Reset auto-refresh when switching away from 5s range
    if (u !== 0 || t !== 5) {
      setRefreshInterval(0);
    }
  };

  // Whether auto-refresh is available (only for 5s range)
  const canAutoRefresh = timeUnit === 0 && time === 5;

  const reloadHistory = () => {
    if (!serverId) return;
    setLoadingHistory(true);
    setErrorHistory(null);
    const req = MetricControllerService.queryMachineHistoryRuntimeInfo(
      serverId,
      time,
      timeUnit,
    );
    req
      .then((res) => {
        setHistory(res?.data ?? []);
      })
      .catch((err) => {
        const msg = err instanceof Error ? err.message : "获取历史数据失败";
        setErrorHistory(msg);
        setHistory([]);
      })
      .finally(() => {
        setLoadingHistory(false);
      });
  };

  useEffect(() => {
    if (!serverId) return;
    let disposed = false;
    const frame = requestAnimationFrame(() => {
      if (disposed) return;
      setLoadingDetail(true);
      setErrorDetail(null);
    });
    const req = MachineControllerService.getMachineDetail(serverId);
    req
      .then((res) => {
        if (disposed) return;

        setDetail(res?.data ?? null);
      })
      .catch((err) => {
        if (disposed) return;
        const msg = err instanceof Error ? err.message : "获取机器详情失败";
        setErrorDetail(msg);
        setDetail(null);
      })
      .finally(() => {
        if (disposed) return;
        setLoadingDetail(false);
      });
    return () => {
      disposed = true;
      cancelAnimationFrame(frame);
      req.cancel();
    };
  }, [serverId]);

  useEffect(() => {
    if (!serverId) return;
    let disposed = false;
    const frame = requestAnimationFrame(() => {
      if (disposed) return;
      setLoadingHistory(true);
      setErrorHistory(null);
    });
    const req = MetricControllerService.queryMachineHistoryRuntimeInfo(
      serverId,
      time,
      timeUnit,
    );
    req
      .then((res) => {
        if (disposed) return;
        setHistory(res?.data ?? []);
      })
      .catch((err) => {
        if (disposed) return;
        const msg = err instanceof Error ? err.message : "获取历史数据失败";
        setErrorHistory(msg);
        setHistory([]);
      })
      .finally(() => {
        if (disposed) return;
        setLoadingHistory(false);
      });
    return () => {
      disposed = true;
      cancelAnimationFrame(frame);
      req.cancel();
    };
  }, [serverId, time, timeUnit]);

  // Auto-refresh effect
  useEffect(() => {
    if (!serverId || refreshInterval <= 0) return;

    const timer = setInterval(() => {
      setLoadingHistory(true);
      setErrorHistory(null);
      MetricControllerService.queryMachineHistoryRuntimeInfo(
        serverId,
        time,
        timeUnit,
      )
        .then((res) => {
          setHistory(res?.data ?? []);
        })
        .catch((err) => {
          const msg = err instanceof Error ? err.message : "获取历史数据失败";
          setErrorHistory(msg);
        })
        .finally(() => {
          setLoadingHistory(false);
        });
    }, refreshInterval * 1000);

    return () => clearInterval(timer);
  }, [serverId, time, timeUnit, refreshInterval]);

  const hostname = detail?.hostname ?? `ID-${detail?.id ?? "未知"}`;

  return (
    <PageBackground>
      <div className="mx-auto w-full max-w-6xl px-6 py-12 sm:px-8 lg:px-12">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-3">
            <OSIcon osName={detail?.osName} />
            <h1 className="text-3xl font-semibold text-white">{hostname}</h1>
          </div>
          <div className="flex items-center gap-3">
            <StatusBadge status={detail?.status} lastSeen={detail?.lastSeen} />
            <Link
              href="/"
              className="text-sm text-indigo-300 hover:text-indigo-200"
            >
              返回概览
            </Link>
          </div>
        </div>
        <p className="mt-2 text-sm text-slate-400">
          {detail?.description ?? "-"}
        </p>
      </div>

      <main className="mx-auto w-full max-w-6xl px-6 pb-16 sm:px-8 lg:px-12">
        <Card className="p-6">
          <div className="mb-6 grid gap-3 sm:grid-cols-2">
            <div className="rounded-2xl border border-white/10 bg-white/5 p-4 text-sm text-slate-200">
              <p>CPU 架构：{detail?.hardware?.cpuArch ?? "-"}</p>
              <p>CPU 核心：{detail?.hardware?.cpuCores ?? "-"}</p>
              <p>
                CPU 型号：
                {(detail?.hardware?.cpuName?.length ?? 0 > 0)
                  ? detail?.hardware?.cpuName
                  : "vCPU"}
              </p>
            </div>
            <div className="rounded-2xl border border-white/10 bg-white/5 p-4 text-sm text-slate-200">
              <p>
                系统：
                {[detail?.osName, detail?.osVersion]
                  .filter(Boolean)
                  .join(" ") || "-"}
              </p>
              <p>内核：{detail?.kernelVersion ?? "-"}</p>
            </div>
          </div>
          {loadingDetail && (
            <p className="mt-3 text-xs text-slate-500">正在加载详情...</p>
          )}
          {errorDetail && (
            <p className="mt-3 text-xs text-rose-400">{errorDetail}</p>
          )}

          <div className="mt-8 space-y-6">
            <div className="flex items-center justify-between">
              <div className="flex items-center gap-3">
                <label htmlFor="time-range" className="text-sm text-slate-400">
                  时间范围
                </label>
                {(() => {
                  const selectedIndex = PRESETS.findIndex(
                    (p) => p.time === time && p.unit === timeUnit,
                  );
                  return (
                    <select
                      id="time-range"
                      value={selectedIndex >= 0 ? String(selectedIndex) : "0"}
                      onChange={(e) => {
                        const idx = Number(e.target.value);
                        const preset = PRESETS[idx] ?? PRESETS[3];
                        setPreset(preset.time, preset.unit);
                      }}
                      className="rounded-2xl border border-white/10 bg-slate-900 px-4 py-2 text-sm text-white focus:border-indigo-500 focus:outline-none"
                    >
                      {PRESETS.map((p, i) => (
                        <option
                          key={`${p.label}-${p.time}-${p.unit}`}
                          value={String(i)}
                          className="text-black"
                        >
                          {`近${p.label}`}
                        </option>
                      ))}
                    </select>
                  );
                })()}
                {canAutoRefresh && (
                  <>
                    <label
                      htmlFor="refresh-interval"
                      className="text-sm text-slate-400"
                    >
                      自动刷新
                    </label>
                    <select
                      id="refresh-interval"
                      value={refreshInterval}
                      onChange={(e) =>
                        setRefreshInterval(Number(e.target.value))
                      }
                      className="rounded-2xl border border-white/10 bg-slate-900 px-4 py-2 text-sm text-white focus:border-indigo-500 focus:outline-none"
                    >
                      {REFRESH_OPTIONS.map((opt) => (
                        <option
                          key={opt.value}
                          value={opt.value}
                          className="text-black"
                        >
                          {opt.label}
                        </option>
                      ))}
                    </select>
                  </>
                )}
              </div>
              <Button
                type="button"
                onClick={reloadHistory}
                variant="outline"
                className="px-3 py-1 text-xs"
              >
                刷新
              </Button>
            </div>

            <div>
              <p className="mb-3 text-lg font-semibold text-white">指标趋势</p>
              <div className="grid gap-4 md:grid-cols-2">
                <TimeSeriesChart
                  title="CPU 使用"
                  data={history.map((h) => ({
                    x: new Date(String(h.timestamp)).getTime(),
                    y: Number(h.cpuUsage ?? 0),
                  }))}
                  color="#6366F1"
                  valueFormatter={(n) => `${n.toFixed(1)}%`}
                />
                <TimeSeriesChart
                  title="内存使用"
                  data={history.map((h) => ({
                    x: new Date(String(h.timestamp)).getTime(),
                    y: Number(h.memoryUsage ?? 0),
                  }))}
                  color="#10B981"
                  valueFormatter={(n) => `${n.toFixed(1)}%`}
                />
                <TimeSeriesChart
                  title="Swap 使用"
                  data={history.map((h) => ({
                    x: new Date(String(h.timestamp)).getTime(),
                    y: Number(h.swapUsage ?? 0),
                  }))}
                  color="#F59E0B"
                  valueFormatter={(n) => `${n.toFixed(1)}%`}
                />
                <TimeSeriesChart
                  title="磁盘使用"
                  data={history.map((h) => ({
                    x: new Date(String(h.timestamp)).getTime(),
                    y: Number(h.diskUsage ?? 0),
                  }))}
                  color="#0EA5E9"
                  valueFormatter={(n) => `${n.toFixed(1)}%`}
                />
              </div>
            </div>
            {loadingHistory && (
              <p className="text-sm text-slate-500">正在加载历史数据...</p>
            )}
            {errorHistory && (
              <p className="text-sm text-rose-400">{errorHistory}</p>
            )}
            <div>
              <p className="mb-3 text-lg font-semibold text-white">磁盘</p>
              {detail?.disks && detail.disks.length > 0 ? (
                <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                  {detail.disks
                    .filter((it) => it.fileSystem !== "virtiofs")
                    .filter((it) => it.fileSystem !== "overlay")
                    .map((disk, idx) => (
                      <div
                        key={`${disk.diskName}-${idx}`}
                        className="rounded-2xl border border-white/10 bg-white/5 p-4 text-sm text-slate-200"
                      >
                        <p className="font-semibold text-white">
                          {disk.diskName}
                        </p>
                        <p className="text-slate-400">
                          {disk.fileSystem} · {disk.diskKind}
                        </p>
                        <p className="mt-1">容量：{disk.totalBytes}</p>
                      </div>
                    ))}
                </div>
              ) : (
                <p className="text-sm text-slate-500">暂无磁盘信息</p>
              )}
            </div>
            <div>
              <p className="mb-3 text-lg font-semibold text-white">网络接口</p>
              {detail?.networkInterfaces &&
              detail.networkInterfaces.length > 0 ? (
                <div className="grid gap-3 sm:grid-cols-2 lg:grid-cols-3">
                  {detail.networkInterfaces.map((iface, idx) => (
                    <div
                      key={`${iface.id ?? iface.interfaceName}-${idx}`}
                      className="rounded-2xl border border-white/10 bg-white/5 p-4 text-sm text-slate-200"
                    >
                      <p className="font-semibold text-white">
                        {iface.interfaceName ?? "未知接口"}
                      </p>
                      <p className="text-slate-400">ID：{iface.id ?? "-"}</p>
                      <div className="mt-1 space-y-1">
                        <p>
                          IPv4：
                          {iface.ipv4Address && iface.ipv4Address.length > 0
                            ? iface.ipv4Address.join("、")
                            : "-"}
                        </p>
                        <p>
                          IPv6：
                          {iface.ipv6Address && iface.ipv6Address.length > 0
                            ? iface.ipv6Address.join("、")
                            : "-"}
                        </p>
                      </div>
                    </div>
                  ))}
                </div>
              ) : (
                <p className="text-sm text-slate-500">暂无网络信息</p>
              )}
            </div>
          </div>
        </Card>

        <div className="mt-6 text-sm text-slate-400">
          <p>服务器ID：{detail?.id ?? "-"}</p>
        </div>
      </main>
    </PageBackground>
  );
}
