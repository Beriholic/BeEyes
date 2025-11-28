"use client";

import {
  Area,
  AreaChart,
  CartesianGrid,
  ResponsiveContainer,
  Tooltip,
  XAxis,
  YAxis,
} from "recharts";

interface DataPoint {
  x: number;
  y: number;
}

interface TimeSeriesChartProps {
  title: string;
  data: DataPoint[];
  color?: string;
  valueFormatter?: (n: number) => string;
}

const formatTime = (ts: number) => {
  try {
    return new Intl.DateTimeFormat("zh-CN", {
      month: "2-digit",
      day: "2-digit",
      hour: "2-digit",
      minute: "2-digit",
    }).format(new Date(ts));
  } catch {
    return String(ts);
  }
};

export function TimeSeriesChart({
  title,
  data,
  color = "#6366F1",
  valueFormatter,
}: TimeSeriesChartProps) {
  const latest = data.length ? data[data.length - 1].y : null;

  return (
    <div className="rounded-2xl border border-white/10 bg-white/5 p-4">
      <div className="mb-2 flex items-baseline justify-between">
        <p className="text-sm font-semibold text-white">{title}</p>
        <p className="text-sm text-indigo-300">
          {latest == null
            ? "-"
            : valueFormatter
            ? valueFormatter(latest)
            : latest.toFixed(1)}
        </p>
      </div>
      {data.length === 0 ? (
        <div className="flex h-[180px] items-center justify-center text-sm text-slate-400">
          暂无数据
        </div>
      ) : (
        <div className="h-[200px] w-full">
          <ResponsiveContainer>
            <AreaChart
              data={data}
              margin={{ top: 10, right: 10, left: 0, bottom: 0 }}
            >
              <defs>
                <linearGradient id="colorFill" x1="0" y1="0" x2="0" y2="1">
                  <stop offset="5%" stopColor={color} stopOpacity={0.5} />
                  <stop offset="95%" stopColor={color} stopOpacity={0} />
                </linearGradient>
              </defs>
              <CartesianGrid
                stroke="rgba(255,255,255,0.06)"
                horizontal
                vertical={false}
              />
              <XAxis
                dataKey="x"
                tickFormatter={formatTime}
                stroke="#94a3b8"
                tick={{ fontSize: 11 }}
              />
              <YAxis
                tickFormatter={(v: number) =>
                  valueFormatter ? valueFormatter(v) : `${v.toFixed(0)}%`
                }
                stroke="#94a3b8"
                tick={{ fontSize: 11 }}
                domain={[0, 100]}
              />
              <Tooltip
                contentStyle={{
                  backgroundColor: "#0b1220",
                  border: "1px solid rgba(255,255,255,0.1)",
                  borderRadius: 12,
                }}
                formatter={(value) => [
                  valueFormatter
                    ? valueFormatter(Number(value))
                    : `${Number(value).toFixed(1)}%`,
                  title,
                ]}
                labelFormatter={(label) => formatTime(Number(label))}
              />
              <Area
                type="monotone"
                dataKey="y"
                stroke={color}
                fillOpacity={1}
                fill="url(#colorFill)"
              />
            </AreaChart>
          </ResponsiveContainer>
        </div>
      )}
    </div>
  );
}
