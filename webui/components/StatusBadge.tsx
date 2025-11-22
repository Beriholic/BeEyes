import React from "react";

interface StatusBadgeProps {
  status?: number | null;
}

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

export function StatusBadge({ status }: StatusBadgeProps) {
  const config =
    status !== null && status !== undefined && STATUS_MAP[status]
      ? STATUS_MAP[status]
      : {
          label: status !== null && status !== undefined ? `状态${status}` : "未知",
          color: "bg-slate-500/10 text-slate-400 border-slate-500/30",
          dot: "bg-slate-400",
        };

  return (
    <span
      className={`inline-flex items-center gap-2 rounded-2xl border px-3 py-1 text-xs font-medium ${config.color}`}
    >
      <span className={`h-2 w-2 rounded-full ${config.dot}`} />
      {config.label}
    </span>
  );
}
