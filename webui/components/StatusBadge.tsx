import React from "react";

interface StatusBadgeProps {
  status?: number | null;
  lastSeen?: string | null;
}

const STATUS_MAP: Record<
  number,
  { label: string; color: string; dot: string }
> = {
  0: {
    label: "在线",
    color: "bg-emerald-500/10 text-emerald-500 border-emerald-500/30",
    dot: "bg-emerald-400",
  },
  1: {
    label: "离线",
    color: "bg-rose-500/10 text-rose-500 border-rose-500/30",
    dot: "bg-rose-400",
  },
  2: {
    label: "已注册",
    color: "bg-blue-500/10 text-blue-500 border-blue-500/30",
    dot: "bg-blue-400",
  },
  3: {
    label: "未注册",
    color: "bg-amber-500/10 text-amber-600 border-amber-500/40",
    dot: "bg-amber-400",
  },
  4: {
    label: "未知",
    color: "bg-slate-500/10 text-slate-400 border-slate-500/30",
    dot: "bg-slate-400",
  },
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

export function StatusBadge({ status, lastSeen }: StatusBadgeProps) {
  const config =
    status !== null && status !== undefined && STATUS_MAP[status]
      ? STATUS_MAP[status]
      : {
          label:
            status !== null && status !== undefined ? `状态${status}` : "未知",
          color: "bg-slate-500/10 text-slate-400 border-slate-500/30",
          dot: "bg-slate-400",
        };

  const isOffline = status === 4 || Number(status) === 4;
  const tooltipText =
    isOffline && lastSeen ? `最近上线：${formatDate(lastSeen)}` : "";

  return (
    <span
      className={`inline-flex items-center gap-2 rounded-2xl border px-3 py-1 text-xs font-medium whitespace-nowrap ${
        config.color
      } ${isOffline && lastSeen ? "relative group cursor-help" : ""}`}
      title={tooltipText}
    >
      <span className={`h-2 w-2 rounded-full ${config.dot}`} />
      {config.label}
      {isOffline && lastSeen && (
        <span className="invisible group-hover:visible absolute bottom-full left-1/2 -translate-x-1/2 mb-2 px-3 py-2 text-xs text-white bg-slate-900 rounded-lg border border-white/10 whitespace-nowrap shadow-xl z-50">
          最近上线：{formatDate(lastSeen)}
          <span className="absolute top-full left-1/2 -translate-x-1/2 -mt-px border-4 border-transparent border-t-slate-900" />
        </span>
      )}
    </span>
  );
}
