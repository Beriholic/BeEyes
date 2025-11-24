"use client";

import type { RuntimeInfo } from "@/api/models/RuntimeInfo";

interface RuntimeMetricsProps {
  runtimeInfo?: RuntimeInfo | null;
}

const formatBytes = (bytes?: number | string | null) => {
  if (!bytes) return "0 B";
  const numBytes = typeof bytes === "string" ? Number(bytes) : bytes;
  if (numBytes <= 0) return "0 B";
  const units = ["B", "KB", "MB", "GB", "TB"];
  let value = numBytes;
  let index = 0;
  while (value >= 1024 && index < units.length - 1) {
    value /= 1024;
    index += 1;
  }
  return `${value.toFixed(value >= 10 ? 0 : 1)} ${units[index]}`;
};

const formatSpeed = (bytesPerSecond?: number | string | null) => {
  if (!bytesPerSecond) return "0 B/s";
  const numSpeed =
    typeof bytesPerSecond === "string"
      ? Number(bytesPerSecond)
      : bytesPerSecond;
  if (numSpeed <= 0) return "0 B/s";

  const units = ["B/s", "KB/s", "MB/s", "GB/s"];
  let value = numSpeed;
  let index = 0;
  while (value >= 1024 && index < units.length - 1) {
    value /= 1024;
    index += 1;
  }
  return `${value.toFixed(value >= 10 ? 0 : 1)} ${units[index]}`;
};

interface ProgressBarProps {
  label: string;
  percentage?: number | null;
  usedText?: string;
  totalText?: string;
  color?: string;
}

const ProgressBar = ({
  label,
  percentage,
  usedText,
  totalText,
  color = "bg-indigo-500",
}: ProgressBarProps) => {
  const percent = percentage ?? 0;
  const displayPercent = Math.min(Math.max(percent, 0), 100);

  return (
    <div className="space-y-1">
      <div className="flex items-center justify-between text-xs">
        <span className="font-medium text-slate-300">{label}</span>
        <div className="flex items-center gap-2">
          {usedText && totalText && (
            <span className="text-slate-400">
              {usedText} / {totalText}
            </span>
          )}
          <span className="font-semibold text-white">
            {displayPercent.toFixed(1)}%
          </span>
        </div>
      </div>
      <div className="h-2 w-full overflow-hidden rounded-full bg-slate-800">
        <div
          className={`h-full transition-all duration-500 ${color}`}
          style={{ width: `${displayPercent}%` }}
        />
      </div>
    </div>
  );
};

export function RuntimeMetrics({ runtimeInfo }: RuntimeMetricsProps) {
  if (!runtimeInfo) {
    return null;
  }

  const { cpu_info, memory_info, disk_info, network_info } = runtimeInfo;

  // Calculate average network speeds across all interfaces
  const avgUploadSpeed =
    network_info?.interfaces?.reduce(
      (sum, iface) => sum + Number(iface.upload_speed ?? 0),
      0
    ) ?? 0;
  const avgDownloadSpeed =
    network_info?.interfaces?.reduce(
      (sum, iface) => sum + Number(iface.download_speed ?? 0),
      0
    ) ?? 0;

  // Calculate total disk usage
  const totalDiskSpace =
    disk_info?.reduce((sum, disk) => sum + Number(disk.total ?? 0), 0) ?? 0;
  const usedDiskSpace =
    disk_info?.reduce((sum, disk) => sum + Number(disk.used ?? 0), 0) ?? 0;
  const diskPercent =
    totalDiskSpace > 0 ? (usedDiskSpace / totalDiskSpace) * 100 : 0;

  return (
    <div className="border-t border-white/5 bg-slate-900/50 px-6 py-4">
      <div className="grid grid-cols-1 gap-4 md:grid-cols-2 lg:grid-cols-3">
        {/* CPU Usage */}
        {cpu_info && (
          <ProgressBar
            label="CPU"
            percentage={cpu_info.usage}
            color="bg-blue-500"
          />
        )}

        {/* Memory Usage */}
        {memory_info && (
          <ProgressBar
            label="内存"
            percentage={memory_info.percent_memory}
            usedText={formatBytes(memory_info.used_memory)}
            totalText={formatBytes(memory_info.total_memory)}
            color="bg-emerald-500"
          />
        )}

        {/* Swap Usage */}
        {memory_info &&
          memory_info.total_swap &&
          memory_info.total_swap > 0 && (
            <ProgressBar
              label="交换分区"
              percentage={memory_info.percent_swap}
              usedText={formatBytes(memory_info.used_swap)}
              totalText={formatBytes(memory_info.total_swap)}
              color="bg-amber-500"
            />
          )}

        {/* Disk Usage */}
        {disk_info && disk_info.length > 0 && (
          <ProgressBar
            label="磁盘"
            percentage={diskPercent}
            usedText={formatBytes(usedDiskSpace)}
            totalText={formatBytes(totalDiskSpace)}
            color="bg-purple-500"
          />
        )}

        {/* Network Upload Speed */}
        <div className="space-y-1">
          <div className="flex items-center justify-between text-xs">
            <span className="font-medium text-slate-300">上传速度</span>
            <span className="font-semibold text-white">
              {formatSpeed(avgUploadSpeed)}
            </span>
          </div>
          <div className="flex items-center gap-2 text-[11px] text-slate-400">
            <svg
              className="h-3 w-3"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M7 11l5-5m0 0l5 5m-5-5v12"
              />
            </svg>
            <span>平均上传</span>
          </div>
        </div>

        {/* Network Download Speed */}
        <div className="space-y-1">
          <div className="flex items-center justify-between text-xs">
            <span className="font-medium text-slate-300">下载速度</span>
            <span className="font-semibold text-white">
              {formatSpeed(avgDownloadSpeed)}
            </span>
          </div>
          <div className="flex items-center gap-2 text-[11px] text-slate-400">
            <svg
              className="h-3 w-3"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M17 13l-5 5m0 0l-5-5m5 5V6"
              />
            </svg>
            <span>平均下载</span>
          </div>
        </div>
      </div>
    </div>
  );
}
