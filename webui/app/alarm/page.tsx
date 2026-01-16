"use client";

import { AlertControllerService } from "@/api/services/AlertControllerService";
import { MachineControllerService } from "@/api/services/MachineControllerService";
import type { AlertRuleDTO } from "@/api/models/AlertRuleDTO";
import type { CreateAlertRequest } from "@/api/models/CreateAlertRequest";
import type { UpdateAlertRequest } from "@/api/models/UpdateAlertRequest";
import type { DeleteAlertRequest } from "@/api/models/DeleteAlertRequest";
import type { Dynamic_AlertLogDO } from "@/api/models/Dynamic_AlertLogDO";
import type { QueryAlertLogRequest } from "@/api/models/QueryAlertLogRequest";
import type { MachineView } from "@/api/models/MachineView";
import { Modal } from "@/components/Modal";
import { SearchablePaginatedSelect } from "@/components/SearchablePaginatedSelect";
import { flushSync } from "react-dom";
import { useCallback, useEffect, useMemo, useState } from "react";

const PAGE_SIZE_OPTIONS = [5, 10, 20];

const AlertMetricType = {
  CPU: { key: "CPU", code: 1, label: "CPU使用率" },
  MEMORY: { key: "MEMORY", code: 2, label: "内存使用率" },
  DISK: { key: "DISK", code: 3, label: "磁盘使用率" },
  STATUS: { key: "STATUS", code: 4, label: "服务器状态" },
} as const;

const AlertCondition = {
  GT: { key: "GT", code: 1, label: "大于" },
  LT: { key: "LT", code: 2, label: "小于" },
  EQ: { key: "EQ", code: 3, label: "等于" },
} as const;

const AlertStatus = {
  TRIGGERING: { key: "TRIGGERING", code: 0, label: "触发中" },
  RESOLVED: { key: "RESOLVED", code: 1, label: "已恢复" },
} as const;

// STATUS only supports EQ condition, other metrics support all
const getAvailableConditions = (metricType: number) => {
  if (metricType === AlertMetricType.STATUS.code) {
    return [AlertCondition.EQ];
  }
  return Object.values(AlertCondition);
};

export default function AlarmPage() {
  const [activeTab, setActiveTab] = useState<"rules" | "logs">("rules");

  const [rules, setRules] = useState<AlertRuleDTO[]>([]);
  const [logs, setLogs] = useState<Dynamic_AlertLogDO[]>([]);
  const [machines, setMachines] = useState<MachineView[]>([]);

  const [rulesLoading, setRulesLoading] = useState(false);
  const [logsLoading, setLogsLoading] = useState(false);
  const [machinesLoading, setMachinesLoading] = useState(false);
  const [rulesError, setRulesError] = useState<string | null>(null);
  const [logsError, setLogsError] = useState<string | null>(null);

  const [stats, setStats] = useState<{ triggering: number; resolved: number } | null>(null);

  const [showCreateRuleModal, setShowCreateRuleModal] = useState(false);
  const [showEditRuleModal, setShowEditRuleModal] = useState(false);
  const [showDeleteRuleModal, setShowDeleteRuleModal] = useState(false);

  const [selectedRule, setSelectedRule] = useState<AlertRuleDTO | null>(null);

  const [createFormData, setCreateFormData] = useState<CreateAlertRequest>({
    name: "",
    serverId: null,
    metricType: 1,
    condition: 1,
    threshold: 80,
    durationSeconds: null,
    silenceSeconds: 300,
    enabled: true,
  });

  const [editFormData, setEditFormData] = useState<UpdateAlertRequest>({
    id: "",
    name: "",
    serverId: null,
    metricType: 1,
    condition: 1,
    threshold: 80,
    durationSeconds: null,
    silenceSeconds: 300,
    enabled: true,
  });

  const [createLoading, setCreateLoading] = useState(false);
  const [updateLoading, setUpdateLoading] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);

  const [pageIndex, setPageIndex] = useState(1);
  const [pageSize] = useState(PAGE_SIZE_OPTIONS[0]);
  const [totalLogs, setTotalLogs] = useState(0);

  const [rulesPageIndex, setRulesPageIndex] = useState(1);
  const [rulesPageSize] = useState(PAGE_SIZE_OPTIONS[1]);
  const [rulesTotal, setRulesTotal] = useState(0);
  const [rulesNameFilter, setRulesNameFilter] = useState("");
  const [rulesServerIdFilter, setRulesServerIdFilter] = useState<string>("");

  const [logServerId, setLogServerId] = useState<string>("");
  const [logStatus, setLogStatus] = useState<number | null>(null);

  const [createError, setCreateError] = useState<string | null>(null);
  const [updateError, setUpdateError] = useState<string | null>(null);
  const [deleteError, setDeleteError] = useState<string | null>(null);

  const loadMachines = useCallback(() => {
    setMachinesLoading(true);
    MachineControllerService.getMachineList(0, 20)
      .then((response) => {
        setMachines(response.data?.data ?? []);
      })
      .catch((err) => {
        console.error("Failed to load machines:", err);
        setMachines([]);
      })
      .finally(() => {
        setMachinesLoading(false);
      });
  }, []);

  const loadRules = useCallback((pageIndex: number = 1) => {
    setRulesLoading(true);
    setRulesError(null);
    AlertControllerService.listRules(
      pageIndex,
      rulesPageSize,
      rulesNameFilter || undefined,
      rulesServerIdFilter || undefined
    )
      .then((response) => {
        const pageData = response.data?.data;
        setRules(pageData ?? []);
        setRulesTotal(Number(response.data?.total) ?? 0);
      })
      .catch((err) => {
        const message = err instanceof Error ? err.message : "加载告警规则失败";
        setRulesError(message);
        setRules([]);
      })
      .finally(() => {
        setRulesLoading(false);
      });
  }, [rulesPageSize, rulesNameFilter, rulesServerIdFilter]);

  const loadStats = useCallback(() => {
    AlertControllerService.getStats()
      .then((response) => {
        const data = response.data?.data;
        if (data && typeof data === "object") {
          const statsData = data as Record<string, number>;
          setStats({
            triggering: statsData.triggering ?? 0,
            resolved: statsData.resolved ?? 0,
          });
        }
      })
      .catch((err) => {
        console.error("Failed to load stats:", err);
      });
  }, []);

  useEffect(() => {
    loadMachines();
    loadRules(rulesPageIndex);
    loadStats();
  }, [rulesPageIndex, loadRules, loadMachines, loadStats]);

  const loadLogs = useCallback(() => {
    setLogsLoading(true);
    setLogsError(null);

    const request: QueryAlertLogRequest = {
      pageIndex: pageIndex - 1,
      pageSize,
      serverId: logServerId || undefined,
      status: logStatus ?? undefined,
    };

    AlertControllerService.listAlertLogs(request)
      .then((response) => {
        const pageData = response.data?.data;
        setLogs(pageData ?? []);
        setTotalLogs(Number(response.data?.total) ?? 0);
      })
      .catch((err) => {
        const message = err instanceof Error ? err.message : "加载告警日志失败";
        setLogsError(message);
        setLogs([]);
      })
      .finally(() => {
        setLogsLoading(false);
      });
  }, [pageIndex, pageSize, logServerId, logStatus]);

  useEffect(() => {
    if (activeTab === "logs") {
      loadLogs();
    }
  }, [activeTab, loadLogs]);

  const totalPages = useMemo(() => {
    return Math.ceil(totalLogs / pageSize) || 1;
  }, [totalLogs, pageSize]);

  const canPrev = pageIndex > 1;
  const canNext = pageIndex < totalPages;

  const handlePageChange = (nextIndex: number) => {
    if (nextIndex < 1 || nextIndex > totalPages) return;
    setPageIndex(nextIndex);
  };

  const getMetricTypeName = (code?: number | null) => {
    if (code === null || code === undefined) return "-";
    const type = Object.values(AlertMetricType).find((t) => t.code === code);
    return type ? type.label : `未知(${code})`;
  };

  const getConditionName = (code?: number | null) => {
    if (code === null || code === undefined) return "-";
    const condition = Object.values(AlertCondition).find((c) => c.code === code);
    return condition ? condition.label : `未知(${code})`;
  };

  const getStatusName = (code?: number | null) => {
    if (code === null || code === undefined) return "-";
    const status = Object.values(AlertStatus).find((s) => s.code === code);
    return status ? status.label : `未知(${code})`;
  };

  const handleCreateRule = async (e: React.FormEvent) => {
    e.preventDefault();
    setCreateLoading(true);
    setCreateError(null);

    try {
      await AlertControllerService.createRule(createFormData);
      setShowCreateRuleModal(false);
      setCreateFormData({
        name: "",
        serverId: null,
        metricType: 1,
        condition: 1,
        threshold: 80,
        durationSeconds: null,
        silenceSeconds: 300,
        enabled: true,
      });
      loadRules();
    } catch (err) {
      const message = err instanceof Error ? err.message : "创建告警规则失败";
      setCreateError(message);
    } finally {
      setCreateLoading(false);
    }
  };

  const handleEditRule = (rule: AlertRuleDTO) => {
    setSelectedRule(rule);
    setEditFormData({
      id: rule.id,
      name: rule.name,
      serverId: rule.serverId?.toString() ?? undefined,
      metricType: rule.metricType ?? 1,
      condition: rule.condition ?? 1,
      threshold: rule.threshold ?? 80,
      durationSeconds: rule.durationSeconds,
      silenceSeconds: rule.silenceSeconds,
      enabled: rule.enabled ?? true,
    });
    setUpdateError(null);
    setShowEditRuleModal(true);
  };

  const handleUpdateRule = async (e: React.FormEvent) => {
    e.preventDefault();
    setUpdateLoading(true);
    setUpdateError(null);

    try {
      await AlertControllerService.updateRule(editFormData);
      setShowEditRuleModal(false);
      setSelectedRule(null);
      loadRules();
    } catch (err) {
      const message = err instanceof Error ? err.message : "更新告警规则失败";
      setUpdateError(message);
    } finally {
      setUpdateLoading(false);
    }
  };

  const handleDeleteRule = (rule: AlertRuleDTO) => {
    setSelectedRule(rule);
    setDeleteError(null);
    setShowDeleteRuleModal(true);
  };

  const handleDeleteConfirm = async () => {
    if (!selectedRule?.id) return;
    setDeleteLoading(true);
    setDeleteError(null);

    try {
      const request: DeleteAlertRequest = { id: selectedRule.id };
      await AlertControllerService.deleteRule(request);
      setShowDeleteRuleModal(false);
      setSelectedRule(null);
      loadRules();
    } catch (err) {
      const message = err instanceof Error ? err.message : "删除告警规则失败";
      setDeleteError(message);
    } finally {
      setDeleteLoading(false);
    }
  };

  const handleToggleRule = async (rule: AlertRuleDTO) => {
    try {
      const request: UpdateAlertRequest = {
        id: rule.id,
        name: rule.name,
        serverId: rule.serverId?.toString() ?? undefined,
        metricType: rule.metricType ?? 1,
        condition: rule.condition ?? 1,
        threshold: rule.threshold ?? 80,
        durationSeconds: rule.durationSeconds,
        silenceSeconds: rule.silenceSeconds,
        enabled: !rule.enabled,
      };
      await AlertControllerService.updateRule(request);
      loadRules();
    } catch (err) {
      console.error("Failed to toggle rule:", err);
    }
  };

  const handleManualCheck = async () => {
    try {
      await AlertControllerService.manualCheck();
      loadStats();
      if (activeTab === "logs") {
        loadLogs();
      }
      alert("手动检查已触发");
    } catch (err) {
      const message = err instanceof Error ? err.message : "手动检查失败";
      alert(message);
    }
  };

  const handleSearchLogs = () => {
    flushSync(() => {
      setPageIndex(1);
    });
    loadLogs();
  };

  const handleResetLogs = () => {
    flushSync(() => {
      setLogServerId("");
      setLogStatus(null);
      setPageIndex(1);
    });
    loadLogs();
  };

  const getServerHostname = (serverId?: number | null) => {
    if (!serverId) return "全部服务器";
    const machine = machines.find((m) => m.id?.toString() === serverId.toString());
    return machine ? machine.hostname ?? `Server ${serverId}` : `Server ${serverId}`;
  };

  return (
    <div className="min-h-screen bg-slate-950 text-white">
      <div className="relative overflow-hidden bg-linear-to-br from-slate-900 via-slate-950 to-black">
        <div className="absolute inset-0 bg-[radial-gradient(circle_at_top,rgba(59,130,246,0.18),transparent_55%)]" />
        <header className="relative mx-auto flex w-full max-w-6xl flex-col gap-6 px-6 py-16 sm:px-8 lg:px-12">
          <div className="text-sm font-semibold uppercase tracking-[0.6em] text-slate-400">
            BeEyes Ops Center
          </div>
          <div className="flex flex-col gap-4 lg:flex-row lg:items-end lg:justify-between">
            <div>
              <h1 className="text-4xl font-semibold leading-tight text-white lg:text-5xl">
                告警中心
              </h1>
              <p className="mt-4 text-base text-slate-300">
                管理告警策略、查看告警日志与统计分析。
              </p>
            </div>
            {stats && (
              <div className="flex items-center gap-4 rounded-2xl border border-white/10 bg-white/5 px-6 py-4 text-sm text-slate-200">
                <div>
                  <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
                    触发中
                  </p>
                  <p className="text-2xl font-semibold text-rose-400">
                    {stats.triggering}
                  </p>
                </div>
                <div className="h-10 w-px bg-white/10" />
                <div>
                  <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
                    已恢复
                  </p>
                  <p className="text-2xl font-semibold text-emerald-400">
                    {stats.resolved}
                  </p>
                </div>
                <div className="h-10 w-px bg-white/10" />
                <div>
                  <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
                    告警规则
                  </p>
                  <p className="text-2xl font-semibold">{rules.length}</p>
                </div>
              </div>
            )}
          </div>
        </header>
      </div>

      <main className="mx-auto w-full max-w-6xl px-6 pb-16 sm:px-8 lg:px-12">
        <section className="-mt-10 rounded-3xl border border-white/5 bg-slate-950/60 p-6 shadow-2xl backdrop-blur">
          <div className="mb-6 flex items-center justify-between">
            <div className="flex gap-2 rounded-2xl bg-slate-900/50 p-1">
              <button
                type="button"
                onClick={() => setActiveTab("rules")}
                className={`rounded-xl px-6 py-2 text-sm font-medium transition ${
                  activeTab === "rules"
                    ? "bg-indigo-500 text-white"
                    : "text-slate-400 hover:text-white"
                }`}
              >
                告警规则
              </button>
              <button
                type="button"
                onClick={() => setActiveTab("logs")}
                className={`rounded-xl px-6 py-2 text-sm font-medium transition ${
                  activeTab === "logs"
                    ? "bg-indigo-500 text-white"
                    : "text-slate-400 hover:text-white"
                }`}
              >
                告警日志
              </button>
            </div>
            <button
              type="button"
              onClick={handleManualCheck}
              className="flex items-center gap-2 rounded-2xl border border-amber-500/50 bg-amber-500/10 px-4 py-2 text-sm font-medium text-amber-300 transition hover:border-amber-400 hover:bg-amber-500/20"
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
                  d="M13 10V3L4 14h7v7l9-11h-7z"
                />
              </svg>
              手动检查
            </button>
          </div>

          {activeTab === "rules" ? (
            <>
              <div className="mb-6 rounded-2xl border border-white/5 bg-slate-900/50 p-4">
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
                  <div className="space-y-1">
                    <label className="text-xs font-medium text-slate-400">
                      规则名称
                    </label>
                    <input
                      type="text"
                      value={rulesNameFilter}
                      onChange={(e) => {
                        setRulesNameFilter(e.target.value);
                        setRulesPageIndex(1);
                      }}
                      placeholder="搜索规则名称"
                      className="w-full rounded-xl border border-white/10 bg-slate-950 px-3 py-2 text-sm text-white focus:border-indigo-500 focus:outline-none"
                    />
                  </div>
                  <div className="space-y-1">
                    <label className="text-xs font-medium text-slate-400">
                      服务器
                    </label>
                    <SearchablePaginatedSelect
                      value={rulesServerIdFilter || null}
                      onChange={(serverId) => {
                        setRulesServerIdFilter(serverId ?? "");
                        setRulesPageIndex(1);
                      }}
                      placeholder="全部服务器"
                      className="w-full"
                    />
                  </div>
                </div>
                <div className="mt-4 flex justify-end gap-3 border-t border-white/5 pt-4">
                  <button
                    type="button"
                    onClick={() => {
                      setRulesNameFilter("");
                      setRulesServerIdFilter("");
                      setRulesPageIndex(1);
                    }}
                    className="rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
                  >
                    重置
                  </button>
                  <button
                    type="button"
                    onClick={() => loadRules(1)}
                    className="rounded-xl bg-indigo-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-600 shadow-lg shadow-indigo-500/20"
                  >
                    查询
                  </button>
                </div>
              </div>

              <div className="mb-6 flex items-center justify-between">
                <div>
                  <p className="text-lg font-semibold text-white">告警规则列表</p>
                  <p className="text-sm text-slate-400">
                    共 {rulesTotal} 条告警规则
                  </p>
                </div>
                <button
                  type="button"
                  onClick={() => setShowCreateRuleModal(true)}
                  className="rounded-2xl border border-indigo-500/50 bg-indigo-500/10 px-6 py-2 text-sm font-medium text-indigo-300 transition hover:border-indigo-400 hover:bg-indigo-500/20"
                >
                  新增规则
                </button>
              </div>

              <div className="overflow-x-auto rounded-2xl border border-white/5 bg-slate-950 [&::-webkit-scrollbar]:h-2 [&::-webkit-scrollbar-track]:bg-slate-900 [&::-webkit-scrollbar-thumb]:bg-slate-700 [&::-webkit-scrollbar-thumb]:rounded-full">
                <table className="w-max min-w-full divide-y divide-white/5 text-left text-sm">
                  <thead className="bg-slate-900/80 text-xs uppercase tracking-wide text-slate-400">
                    <tr>
                      <th className="px-6 py-4 font-medium w-64 shrink-0">规则名称</th>
                      <th className="px-6 py-4 font-medium shrink-0">服务器</th>
                      <th className="px-6 py-4 font-medium shrink-0">指标</th>
                      <th className="px-6 py-4 font-medium shrink-0">条件</th>
                      <th className="px-6 py-4 font-medium shrink-0">阈值</th>
                      <th className="px-6 py-4 font-medium shrink-0">持续时间</th>
                      <th className="px-6 py-4 font-medium shrink-0">静默时间</th>
                      <th className="px-6 py-4 font-medium shrink-0">状态</th>
                      <th className="px-6 py-4 font-medium text-right shrink-0">操作</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-white/5 text-slate-200">
                    {rulesLoading ? (
                      <tr>
                        <td
                          colSpan={9}
                          className="px-6 py-16 text-center text-slate-400"
                        >
                          正在加载告警规则...
                        </td>
                      </tr>
                    ) : rulesError ? (
                      <tr>
                        <td
                          colSpan={9}
                          className="px-6 py-16 text-center text-rose-400"
                        >
                          {rulesError}
                        </td>
                      </tr>
                    ) : rules.length === 0 ? (
                      <tr>
                        <td
                          colSpan={9}
                          className="px-6 py-16 text-center text-slate-400"
                        >
                          暂无告警规则
                        </td>
                      </tr>
                    ) : (
                      rules.map((rule, index) => (
                        <tr
                          key={`${rule.id ?? index}`}
                          className="hover:bg-white/5"
                        >
                          <td className="px-6 py-4">
                            <div className="max-w-xs truncate font-semibold text-white" title={rule.name ?? "-"}>
                              {rule.name ?? "-"}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <p className="text-sm text-slate-300">
                              {getServerHostname(rule.serverId)}
                            </p>
                          </td>
                          <td className="px-6 py-4">
                            <div className="text-sm text-slate-200">
                              {getMetricTypeName(rule.metricType)}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <div className="text-sm text-slate-200">
                              {getConditionName(rule.condition)}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <div className="text-sm text-slate-200">
                              {rule.metricType === AlertMetricType.STATUS.code
                                ? (rule.threshold === 1 ? "离线" : rule.threshold === 0 ? "在线" : "-")
                                : `${rule.threshold ?? "-"}%`}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <div className="text-sm text-slate-200">
                              {rule.durationSeconds
                                ? `${rule.durationSeconds}秒`
                                : "-"}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <div className="text-sm text-slate-200">
                              {rule.silenceSeconds
                                ? `${rule.silenceSeconds}秒`
                                : "-"}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <div className="flex items-center gap-2">
                              <button
                                type="button"
                                onClick={() => handleToggleRule(rule)}
                                className={`relative inline-flex h-6 w-11 items-center rounded-full transition-colors ${
                                  rule.enabled
                                    ? "bg-emerald-500/20"
                                    : "bg-slate-700"
                                }`}
                              >
                                <span
                                  className={`inline-block h-4 w-4 transform rounded-full bg-white transition-transform ${
                                    rule.enabled ? "translate-x-6" : "translate-x-1"
                                  }`}
                                />
                              </button>
                              <span
                                className={`text-xs ${
                                  rule.enabled
                                    ? "text-emerald-400"
                                    : "text-slate-400"
                                }`}
                              >
                                {rule.enabled ? "启用" : "禁用"}
                              </span>
                            </div>
                          </td>
                          <td className="px-6 py-4 text-right">
                            <div className="flex justify-end gap-2">
                              <button
                                type="button"
                                onClick={() => handleEditRule(rule)}
                                className="rounded-lg border border-indigo-500/30 bg-indigo-500/10 px-3 py-1.5 text-xs text-indigo-400 transition hover:border-indigo-500/50 hover:bg-indigo-500/20"
                              >
                                编辑
                              </button>
                              <button
                                type="button"
                                onClick={() => handleDeleteRule(rule)}
                                className="rounded-lg border border-rose-500/30 bg-rose-500/10 px-3 py-1.5 text-xs text-rose-400 transition hover:border-rose-500/50 hover:bg-rose-500/20"
                              >
                                删除
                              </button>
                            </div>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>

              <div className="mt-6 flex justify-between items-center">
                <span className="text-sm text-slate-400">
                  第 {rulesPageIndex} 页，共 {Math.ceil(rulesTotal / rulesPageSize)} 页
                </span>
                <div className="flex gap-3">
                  <button
                    type="button"
                    disabled={rulesPageIndex <= 1}
                    onClick={() => setRulesPageIndex((p) => Math.max(1, p - 1))}
                    className="rounded-2xl border border-white/10 px-4 py-2 transition hover:border-indigo-400 hover:text-white disabled:cursor-not-allowed disabled:border-white/5 disabled:text-slate-500"
                  >
                    上一页
                  </button>
                  <button
                    type="button"
                    disabled={rulesPageIndex >= Math.ceil(rulesTotal / rulesPageSize)}
                    onClick={() => setRulesPageIndex((p) => p + 1)}
                    className="rounded-2xl border border-white/10 px-4 py-2 transition hover:border-indigo-400 hover:text-white disabled:cursor-not-allowed disabled:border-white/5 disabled:text-slate-500"
                  >
                    下一页
                  </button>
                </div>
              </div>
            </>
          ) : (
            <>
              <div className="mb-6 rounded-2xl border border-white/5 bg-slate-900/50 p-4">
                <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-4">
                  <div className="space-y-1">
                    <label className="text-xs font-medium text-slate-400">
                      服务器
                    </label>
                    <select
                      value={logServerId}
                      onChange={(e) => setLogServerId(e.target.value)}
                      className="w-full rounded-xl border border-white/10 bg-slate-950 px-3 py-2 text-sm text-white focus:border-indigo-500 focus:outline-none"
                    >
                      <option value="">全部</option>
                      {machines.map((machine) => (
                        <option
                          key={machine.id}
                          value={machine.id?.toString() ?? ""}
                          className="text-black"
                        >
                          {machine.hostname ?? `Server ${machine.id}`}
                        </option>
                      ))}
                    </select>
                  </div>
                  <div className="space-y-1">
                    <label className="text-xs font-medium text-slate-400">
                      状态
                    </label>
                    <select
                      value={logStatus ?? ""}
                      onChange={(e) =>
                        setLogStatus(
                          e.target.value ? Number(e.target.value) : null
                        )
                      }
                      className="w-full rounded-xl border border-white/10 bg-slate-950 px-3 py-2 text-sm text-white focus:border-indigo-500 focus:outline-none"
                    >
                      <option value="">全部</option>
                      <option value="0" className="text-black">
                        触发中
                      </option>
                      <option value="1" className="text-black">
                        已恢复
                      </option>
                    </select>
                  </div>
                </div>
                <div className="mt-4 flex justify-end gap-3 border-t border-white/5 pt-4">
                  <button
                    type="button"
                    onClick={handleResetLogs}
                    className="rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
                  >
                    重置
                  </button>
                  <button
                    type="button"
                    onClick={handleSearchLogs}
                    className="rounded-xl bg-indigo-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-600 shadow-lg shadow-indigo-500/20"
                  >
                    查询
                  </button>
                </div>
              </div>

              <div className="mb-6">
                <p className="text-lg font-semibold text-white">告警日志</p>
                <p className="text-sm text-slate-400">
                  共 {totalLogs} 条，第 {pageIndex} / {totalPages} 页
                </p>
              </div>

              <div className="overflow-x-auto rounded-2xl border border-white/5 bg-slate-950">
                <table className="min-w-full divide-y divide-white/5 text-left text-sm">
                  <thead className="bg-slate-900/80 text-xs uppercase tracking-wide text-slate-400">
                    <tr>
                      <th className="px-6 py-4 font-medium">服务器</th>
                      <th className="px-6 py-4 font-medium">告警内容</th>
                      <th className="px-6 py-4 font-medium">状态</th>
                      <th className="px-6 py-4 font-medium">开始时间</th>
                      <th className="px-6 py-4 font-medium">恢复时间</th>
                    </tr>
                  </thead>
                  <tbody className="divide-y divide-white/5 text-slate-200">
                    {logsLoading ? (
                      <tr>
                        <td
                          colSpan={5}
                          className="px-6 py-16 text-center text-slate-400"
                        >
                          正在加载告警日志...
                        </td>
                      </tr>
                    ) : logsError ? (
                      <tr>
                        <td
                          colSpan={5}
                          className="px-6 py-16 text-center text-rose-400"
                        >
                          {logsError}
                        </td>
                      </tr>
                    ) : logs.length === 0 ? (
                      <tr>
                        <td
                          colSpan={5}
                          className="px-6 py-16 text-center text-slate-400"
                        >
                          暂无告警日志
                        </td>
                      </tr>
                    ) : (
                      logs.map((log, index) => (
                        <tr
                          key={`${log.id ?? index}`}
                          className="hover:bg-white/5"
                        >
                          <td className="px-6 py-4">
                            <div className="font-semibold text-white">
                              {getServerHostname(log.serverId)}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <p className="max-w-md text-sm text-slate-300">
                              {log.message ?? "-"}
                            </p>
                          </td>
                          <td className="px-6 py-4">
                            <div
                              className={`inline-flex items-center rounded-lg border px-2 py-1 text-xs font-medium ${
                                log.status === 0
                                  ? "border-rose-500/30 bg-rose-500/10 text-rose-400"
                                  : "border-emerald-500/30 bg-emerald-500/10 text-emerald-400"
                              }`}
                            >
                              {getStatusName(log.status)}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <div className="text-sm text-slate-300">
                              {log.startedAt ?? "-"}
                            </div>
                          </td>
                          <td className="px-6 py-4">
                            <div className="text-sm text-slate-300">
                              {log.resolvedAt ?? "-"}
                            </div>
                          </td>
                        </tr>
                      ))
                    )}
                  </tbody>
                </table>
              </div>

              <div className="mt-6 flex justify-end gap-3">
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
            </>
          )}
        </section>
      </main>

      {showCreateRuleModal && (
        <Modal
          isOpen={showCreateRuleModal}
          onClose={() => {
            setShowCreateRuleModal(false);
            setCreateFormData({
              name: "",
              serverId: null,
              metricType: 1,
              condition: 1,
              threshold: 80,
              durationSeconds: null,
              silenceSeconds: 300,
              enabled: true,
            });
            setCreateError(null);
          }}
          title="新增告警规则"
        >
          <form onSubmit={handleCreateRule} className="space-y-6">
            <div className="space-y-4">
              <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300">
                  规则名称 <span className="text-rose-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={createFormData.name}
                  onChange={(e) =>
                    setCreateFormData({ ...createFormData, name: e.target.value })
                  }
                  className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  placeholder="请输入规则名称"
                />
              </div>
              <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300">
                  监控服务器 <span className="text-rose-500">*</span>
                </label>
                <SearchablePaginatedSelect
                  value={createFormData.serverId ?? null}
                  onChange={(serverId) =>
                    setCreateFormData({ ...createFormData, serverId })
                  }
                  placeholder="请选择服务器"
                  className="w-full"
                />
              </div>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div className="space-y-1">
                  <label className="text-sm font-medium text-slate-300">
                    监控指标 <span className="text-rose-500">*</span>
                  </label>
                  <select
                    required
                    value={createFormData.metricType}
                    onChange={(e) => {
                      const newMetricType = Number(e.target.value);
                      const updates: Partial<typeof createFormData> = { metricType: newMetricType };
                      // For STATUS metric, auto-set condition to EQ and threshold to 1 (OFFLINE in backend)
                      if (newMetricType === AlertMetricType.STATUS.code) {
                        updates.condition = AlertCondition.EQ.code;
                        updates.threshold = 1;
                      }
                      setCreateFormData({ ...createFormData, ...updates });
                    }}
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  >
                    {Object.values(AlertMetricType).map((type) => (
                      <option
                        key={type.code}
                        value={type.code}
                        className="text-black"
                      >
                        {type.label}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1">
                  <label className="text-sm font-medium text-slate-300">
                    触发条件 <span className="text-rose-500">*</span>
                  </label>
                  <select
                    required
                    value={createFormData.condition}
                    onChange={(e) =>
                      setCreateFormData({
                        ...createFormData,
                        condition: Number(e.target.value),
                      })
                    }
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  >
                    {getAvailableConditions(createFormData.metricType ?? 1).map((condition) => (
                      <option
                        key={condition.code}
                        value={condition.code}
                        className="text-black"
                      >
                        {condition.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300">
                  阈值 <span className="text-rose-500">*</span>
                </label>
                {createFormData.metricType === AlertMetricType.STATUS.code ? (
                  <select
                    required
                    value={createFormData.threshold}
                    onChange={(e) =>
                      setCreateFormData({
                        ...createFormData,
                        threshold: Number(e.target.value),
                      })
                    }
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  >
                    <option value={1} className="text-black">服务器离线时触发</option>
                    <option value={0} className="text-black">服务器在线时触发</option>
                  </select>
                ) : (
                  <input
                    type="number"
                    required
                    min="0"
                    max="100"
                    value={createFormData.threshold}
                    onChange={(e) =>
                      setCreateFormData({
                        ...createFormData,
                        threshold: Number(e.target.value),
                      })
                    }
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                    placeholder="请输入阈值（0-100）"
                  />
                )}
              </div>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div className="space-y-1">
                  <label className="text-sm font-medium text-slate-300">
                    持续时间（秒）
                  </label>
                  <input
                    type="number"
                    min="0"
                    value={createFormData.durationSeconds ?? ""}
                    onChange={(e) =>
                      setCreateFormData({
                        ...createFormData,
                        durationSeconds: e.target.value
                          ? Number(e.target.value)
                          : null,
                      })
                    }
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                    placeholder="留空表示立即触发"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-sm font-medium text-slate-300">
                    静默时间（秒）
                  </label>
                  <input
                    type="number"
                    min="0"
                    value={createFormData.silenceSeconds ?? ""}
                    onChange={(e) =>
                      setCreateFormData({
                        ...createFormData,
                        silenceSeconds: e.target.value
                          ? Number(e.target.value)
                          : null,
                      })
                    }
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                    placeholder="默认300秒"
                  />
                </div>
              </div>
              <div className="flex items-center gap-3">
                <input
                  type="checkbox"
                  id="enabled"
                  checked={createFormData.enabled}
                  onChange={(e) =>
                    setCreateFormData({
                      ...createFormData,
                      enabled: e.target.checked,
                    })
                  }
                  className="h-4 w-4 rounded border-white/20 bg-slate-900 text-indigo-500 focus:border-indigo-500 focus:ring-indigo-500"
                />
                <label htmlFor="enabled" className="text-sm text-slate-300">
                  启用规则
                </label>
              </div>
            </div>

            {createError && (
              <div className="rounded-xl bg-rose-500/10 p-4 text-sm text-rose-400">
                {createError}
              </div>
            )}

            <div className="flex justify-end gap-3 pt-4">
              <button
                type="button"
                onClick={() => setShowCreateRuleModal(false)}
                className="rounded-xl border border-white/10 bg-white/5 px-6 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
              >
                取消
              </button>
              <button
                type="submit"
                disabled={createLoading}
                className="flex items-center gap-2 rounded-xl bg-indigo-500 px-6 py-2 text-sm font-medium text-white transition hover:bg-indigo-600 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {createLoading && (
                  <svg
                    className="h-4 w-4 animate-spin"
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
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    />
                  </svg>
                )}
                创建
              </button>
            </div>
          </form>
        </Modal>
      )}

      {showEditRuleModal && selectedRule && (
        <Modal
          isOpen={showEditRuleModal}
          onClose={() => {
            setShowEditRuleModal(false);
            setSelectedRule(null);
            setUpdateError(null);
          }}
          title="编辑告警规则"
        >
          <form onSubmit={handleUpdateRule} className="space-y-6">
            <div className="space-y-4">
              <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300">
                  规则名称 <span className="text-rose-500">*</span>
                </label>
                <input
                  type="text"
                  required
                  value={editFormData.name}
                  onChange={(e) =>
                    setEditFormData({ ...editFormData, name: e.target.value })
                  }
                  className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  placeholder="请输入规则名称"
                />
              </div>
              <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300">
                  监控服务器 <span className="text-rose-500">*</span>
                </label>
                <SearchablePaginatedSelect
                  value={editFormData.serverId ?? null}
                  onChange={(serverId) =>
                    setEditFormData({
                      ...editFormData,
                      serverId: serverId ?? undefined,
                    })
                  }
                  placeholder="请选择服务器"
                  className="w-full"
                />
              </div>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div className="space-y-1">
                  <label className="text-sm font-medium text-slate-300">
                    监控指标 <span className="text-rose-500">*</span>
                  </label>
                  <select
                    required
                    value={editFormData.metricType}
                    onChange={(e) => {
                      const newMetricType = Number(e.target.value);
                      const updates: Partial<typeof editFormData> = { metricType: newMetricType };
                      // For STATUS metric, auto-set condition to EQ and threshold to 1 (OFFLINE in backend)
                      if (newMetricType === AlertMetricType.STATUS.code) {
                        updates.condition = AlertCondition.EQ.code;
                        updates.threshold = 1;
                      }
                      setEditFormData({ ...editFormData, ...updates });
                    }}
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  >
                    {Object.values(AlertMetricType).map((type) => (
                      <option
                        key={type.code}
                        value={type.code}
                        className="text-black"
                      >
                        {type.label}
                      </option>
                    ))}
                  </select>
                </div>
                <div className="space-y-1">
                  <label className="text-sm font-medium text-slate-300">
                    触发条件 <span className="text-rose-500">*</span>
                  </label>
                  <select
                    required
                    value={editFormData.condition}
                    onChange={(e) =>
                      setEditFormData({
                        ...editFormData,
                        condition: Number(e.target.value),
                      })
                    }
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  >
                    {getAvailableConditions(editFormData.metricType ?? 1).map((condition) => (
                      <option
                        key={condition.code}
                        value={condition.code}
                        className="text-black"
                      >
                        {condition.label}
                      </option>
                    ))}
                  </select>
                </div>
              </div>
              <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300">
                  阈值 <span className="text-rose-500">*</span>
                </label>
                {editFormData.metricType === AlertMetricType.STATUS.code ? (
                  <select
                    required
                    value={editFormData.threshold}
                    onChange={(e) =>
                      setEditFormData({
                        ...editFormData,
                        threshold: Number(e.target.value),
                      })
                    }
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  >
                    <option value={1} className="text-black">服务器离线时触发</option>
                    <option value={0} className="text-black">服务器在线时触发</option>
                  </select>
                ) : (
                  <input
                    type="number"
                    required
                    min="0"
                    max="100"
                    value={editFormData.threshold}
                    onChange={(e) =>
                      setEditFormData({
                        ...editFormData,
                        threshold: Number(e.target.value),
                      })
                    }
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                    placeholder="请输入阈值（0-100）"
                  />
                )}
              </div>
              <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
                <div className="space-y-1">
                  <label className="text-sm font-medium text-slate-300">
                    持续时间（秒）
                  </label>
                  <input
                    type="number"
                    min="0"
                    value={editFormData.durationSeconds ?? ""}
                    onChange={(e) =>
                      setEditFormData({
                        ...editFormData,
                        durationSeconds: e.target.value
                          ? Number(e.target.value)
                          : null,
                      })
                    }
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                    placeholder="留空表示立即触发"
                  />
                </div>
                <div className="space-y-1">
                  <label className="text-sm font-medium text-slate-300">
                    静默时间（秒）
                  </label>
                  <input
                    type="number"
                    min="0"
                    value={editFormData.silenceSeconds ?? ""}
                    onChange={(e) =>
                      setEditFormData({
                        ...editFormData,
                        silenceSeconds: e.target.value
                          ? Number(e.target.value)
                          : null,
                      })
                    }
                    className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                    placeholder="默认300秒"
                  />
                </div>
              </div>
              <div className="flex items-center gap-3">
                <input
                  type="checkbox"
                  id="edit-enabled"
                  checked={editFormData.enabled}
                  onChange={(e) =>
                    setEditFormData({
                      ...editFormData,
                      enabled: e.target.checked,
                    })
                  }
                  className="h-4 w-4 rounded border-white/20 bg-slate-900 text-indigo-500 focus:border-indigo-500 focus:ring-indigo-500"
                />
                <label htmlFor="edit-enabled" className="text-sm text-slate-300">
                  启用规则
                </label>
              </div>
            </div>

            {updateError && (
              <div className="rounded-xl bg-rose-500/10 p-4 text-sm text-rose-400">
                {updateError}
              </div>
            )}

            <div className="flex justify-end gap-3 pt-4">
              <button
                type="button"
                onClick={() => setShowEditRuleModal(false)}
                className="rounded-xl border border-white/10 bg-white/5 px-6 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
              >
                取消
              </button>
              <button
                type="submit"
                disabled={updateLoading}
                className="flex items-center gap-2 rounded-xl bg-indigo-500 px-6 py-2 text-sm font-medium text-white transition hover:bg-indigo-600 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {updateLoading && (
                  <svg
                    className="h-4 w-4 animate-spin"
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
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    />
                  </svg>
                )}
                保存修改
              </button>
            </div>
          </form>
        </Modal>
      )}

      {showDeleteRuleModal && selectedRule && (
        <Modal
          isOpen={showDeleteRuleModal}
          onClose={() => {
            setShowDeleteRuleModal(false);
            setSelectedRule(null);
            setDeleteError(null);
          }}
          title="删除告警规则"
        >
          <div className="space-y-6">
            <div className="rounded-xl bg-rose-500/10 p-4 text-rose-200">
              <div className="flex items-start gap-3">
                <svg
                  className="mt-0.5 h-5 w-5 shrink-0"
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M12 9v2m0 4h.01m-6.938 4h13.856c1.54 0 2.502-1.667 1.732-3L13.732 4c-.77-1.333-2.694-1.333-3.464 0L3.34 16c-.77 1.333.192 3 1.732 3z"
                  />
                </svg>
                <div className="text-sm">
                  <p className="font-medium">确认删除该告警规则？</p>
                  <p className="mt-1 opacity-80">
                    规则 &quot;{selectedRule.name}&quot; 将被永久删除，此操作无法撤销。
                  </p>
                </div>
              </div>
            </div>

            {deleteError && (
              <div className="rounded-xl bg-rose-500/10 p-4 text-sm text-rose-400">
                {deleteError}
              </div>
            )}

            <div className="flex justify-end gap-3 pt-4">
              <button
                type="button"
                onClick={() => setShowDeleteRuleModal(false)}
                className="rounded-xl border border-white/10 bg-white/5 px-6 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
              >
                取消
              </button>
              <button
                type="button"
                onClick={handleDeleteConfirm}
                disabled={deleteLoading}
                className="flex items-center gap-2 rounded-xl bg-rose-500 px-6 py-2 text-sm font-medium text-white transition hover:bg-rose-600 disabled:cursor-not-allowed disabled:opacity-50"
              >
                {deleteLoading && (
                  <svg
                    className="h-4 w-4 animate-spin"
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
                      d="M4 12a8 8 0 018-8V0C5.373 0 0 5.373 0 12h4zm2 5.291A7.962 7.962 0 014 12H0c0 3.042 1.135 5.824 3 7.938l3-2.647z"
                    />
                  </svg>
                )}
                确认删除
              </button>
            </div>
          </div>
        </Modal>
      )}
    </div>
  );
}
