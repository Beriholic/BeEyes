"use client";

import type { MachineManageView } from "@/api/models/MachineManageView";
import type { CreateMachineRequest } from "@/api/models/CreateMachineRequest";
import type { DeleteMachineRequest } from "@/api/models/DeleteMachineRequest";
import type { UpdateMachineRequest } from "@/api/models/UpdateMachineRequest";
import { MachineControllerService } from "@/api/services/MachineControllerService";
import { useCallback, useEffect, useMemo, useState, FormEvent } from "react";
import CountrySelect from "react-select-country-list";
import ReactCountryFlag from "react-country-flag";

const PAGE_SIZE_OPTIONS = [5, 10, 20];

export default function MachinesPage() {
  const [machines, setMachines] = useState<MachineManageView[]>([]);
  const [pageIndex, setPageIndex] = useState(1);
  const [pageSize, setPageSize] = useState(PAGE_SIZE_OPTIONS[0]);
  const [hostname, setHostname] = useState("");
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [copySuccess, setCopySuccess] = useState(false);
  const [deleteSuccess, setDeleteSuccess] = useState(false);
  const [updateSuccess, setUpdateSuccess] = useState(false); // 添加更新成功提示状态
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [showEditModal, setShowEditModal] = useState(false); // 添加编辑弹窗状态
  const [createLoading, setCreateLoading] = useState(false);
  const [updateLoading, setUpdateLoading] = useState(false); // 添加更新加载状态
  const [createError, setCreateError] = useState<string | null>(null);
  const [updateError, setUpdateError] = useState<string | null>(null); // 添加更新错误状态
  const [deleteConfirm, setDeleteConfirm] = useState<{
    show: boolean;
    machine: MachineManageView | null;
    loading: boolean;
  }>({ show: false, machine: null, loading: false });
  const [editMachine, setEditMachine] = useState<MachineManageView | null>(
    null
  ); // 添加编辑的机器状态
  const [formData, setFormData] = useState<CreateMachineRequest>({
    description: "",
    region: "",
  });
  const [editFormData, setEditFormData] = useState<{
    // 添加编辑表单数据状态
    description: string | null;
    region: string | null;
  }>({
    description: null,
    region: null,
  });
  const [selectedCountry, setSelectedCountry] = useState<{
    value: string;
    label: string;
  } | null>(null);
  const [selectedEditCountry, setSelectedEditCountry] = useState<{
    // 添加编辑选中的国家状态
    value: string;
    label: string;
  } | null>(null);
  const countryOptions = useMemo(() => CountrySelect().getData(), []);

  // 获取国家名称的函数
  const getCountryName = useCallback(
    (countryCode: string) => {
      const country = countryOptions.find(
        (c: { value: string; label: string }) => c.value === countryCode
      );
      return country ? country.label : countryCode;
    },
    [countryOptions]
  );

  const loadMachines = useCallback(() => {
    let disposed = false;
    const frame = requestAnimationFrame(() => {
      if (disposed) return;
      setLoading(true);
      setError(null);
    });

    const request = MachineControllerService.getMachineManageList(
      pageIndex - 1,
      pageSize,
      hostname || undefined
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
        const message = err instanceof Error ? err.message : "获取机器列表失败";
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
  }, [pageIndex, pageSize, hostname]);

  useEffect(() => loadMachines(), [loadMachines]);

  const totalPages = useMemo(() => {
    if (total <= 0) return 1;
    return Math.max(Math.ceil(total / pageSize), 1);
  }, [total, pageSize]);

  const canPrev = pageIndex > 1;
  const canNext = pageIndex < totalPages;

  const handlePageChange = (nextIndex: number) => {
    if (nextIndex < 1 || nextIndex > totalPages) return;
    setPageIndex(nextIndex);
  };

  const handleCreateMachine = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setCreateLoading(true);
    setCreateError(null);

    // 验证必填字段
    if (!formData.description || !formData.description.trim()) {
      setCreateError("服务器描述不能为空");
      setCreateLoading(false);
      return;
    }

    if (!selectedCountry || !selectedCountry.value) {
      setCreateError("请选择服务器地区");
      setCreateLoading(false);
      return;
    }

    try {
      const requestData = {
        description: formData.description?.trim() || null,
        region: selectedCountry.value,
      };
      console.log("提交的数据:", requestData);
      console.log("选中的国家:", selectedCountry);
      await MachineControllerService.createMachine(requestData);
      setShowCreateModal(false);
      setFormData({ description: "", region: "" });
      setSelectedCountry(null);
      // 刷新列表
      loadMachines();
    } catch (err) {
      const message = err instanceof Error ? err.message : "创建机器失败";
      setCreateError(message);
    } finally {
      setCreateLoading(false);
    }
  };

  // 添加处理编辑机器的函数
  const handleEditMachine = (machine: MachineManageView) => {
    setEditMachine(machine);
    setEditFormData({
      description: machine.description ?? null,
      region: machine.region ?? null,
    });

    // 设置选中的国家
    if (machine.region) {
      const country = countryOptions.find(
        (c: { value: string; label: string }) => c.value === machine.region
      );
      setSelectedEditCountry(country || null);
    } else {
      setSelectedEditCountry(null);
    }

    setShowEditModal(true);
  };

  // 添加更新机器的函数
  const handleUpdateMachine = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setUpdateLoading(true);
    setUpdateError(null);

    // 验证必填字段
    if (!editFormData.description || !editFormData.description.trim()) {
      setUpdateError("服务器描述不能为空");
      setUpdateLoading(false);
      return;
    }

    if (!selectedEditCountry || !selectedEditCountry.value) {
      setUpdateError("请选择服务器地区");
      setUpdateLoading(false);
      return;
    }

    try {
      if (!editMachine) {
        throw new Error("未选择要编辑的机器");
      }

      const requestData: UpdateMachineRequest = {
        id: editMachine.id,
        description: editFormData.description?.trim() || null,
        region: selectedEditCountry.value,
      };

      await MachineControllerService.updateMachine(requestData);
      setShowEditModal(false);
      setEditMachine(null);
      setEditFormData({ description: null, region: null });
      setSelectedEditCountry(null);
      setUpdateSuccess(true); // 显示更新成功提示
      setTimeout(() => setUpdateSuccess(false), 3000); // 3秒后自动隐藏提示
      // 刷新列表
      loadMachines();
    } catch (err) {
      const message = err instanceof Error ? err.message : "更新机器失败";
      setUpdateError(message);
    } finally {
      setUpdateLoading(false);
    }
  };

  const handleDeleteMachine = async () => {
    if (!deleteConfirm.machine) return;

    setDeleteConfirm((prev) => ({ ...prev, loading: true }));

    try {
      const requestData: DeleteMachineRequest = {
        serverId: deleteConfirm.machine.id,
      };

      await MachineControllerService.deleteMachine(requestData);
      setDeleteConfirm({ show: false, machine: null, loading: false });
      setDeleteSuccess(true);
      setTimeout(() => setDeleteSuccess(false), 3000);
      loadMachines();
    } catch (err) {
      const message = err instanceof Error ? err.message : "删除机器失败";
      setError(message);
      setDeleteConfirm((prev) => ({ ...prev, loading: false }));
    }
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
                机器管理
              </h1>
              <p className="mt-4 text-base text-slate-300">
                管理服务器基本信息、地区配置与 API 密钥。
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
                  {pageIndex}/{totalPages}
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
                当前为第 {pageIndex} 页（显示 {pageSize} 条）
              </p>
            </div>
            <div className="flex items-center gap-3">
              <div className="relative">
                <input
                  type="text"
                  value={hostname}
                  onChange={(e) => {
                    setHostname(e.target.value);
                    setPageIndex(1);
                  }}
                  placeholder="搜索主机名..."
                  className="w-48 rounded-2xl border border-white/10 bg-slate-900 px-4 py-2 pl-10 text-sm text-white placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none"
                />
                <svg
                  className="absolute left-3 top-1/2 h-4 w-4 -translate-y-1/2 text-slate-500"
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
              </div>
              <button
                type="button"
                onClick={() => setShowCreateModal(true)}
                className="rounded-2xl border border-indigo-500/50 bg-indigo-500/10 px-6 py-2 text-sm font-medium text-indigo-300 transition hover:border-indigo-400 hover:bg-indigo-500/20"
              >
                新增机器
              </button>
              <button
                type="button"
                onClick={() => loadMachines()}
                disabled={loading}
                className="flex items-center gap-2 rounded-2xl border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-slate-300 transition hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
                title="刷新列表"
              >
                <svg
                  className={`h-4 w-4 ${loading ? "animate-spin" : ""}`}
                  fill="none"
                  stroke="currentColor"
                  viewBox="0 0 24 24"
                >
                  <path
                    strokeLinecap="round"
                    strokeLinejoin="round"
                    strokeWidth={2}
                    d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
                  />
                </svg>
                <span>刷新</span>
              </button>
              <div className="flex items-center gap-3 text-sm text-slate-300">
                <label htmlFor="page-size" className="text-slate-400">
                  每页数量
                </label>
                <select
                  id="page-size"
                  value={pageSize}
                  onChange={(event) => {
                    setPageSize(Number(event.target.value));
                    setPageIndex(1);
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
          </div>

          <div className="overflow-hidden rounded-2xl border border-white/5 bg-slate-950">
            <table className="min-w-full divide-y divide-white/5 text-left text-sm">
              <thead className="bg-slate-900/80 text-xs uppercase tracking-wide text-slate-400">
                <tr>
                  <th className="px-6 py-4 font-medium">主机名</th>
                  <th className="px-6 py-4 font-medium">描述</th>
                  <th className="px-6 py-4 font-medium">地区</th>
                  <th className="px-6 py-4 font-medium">API 密钥</th>
                  <th className="px-6 py-4 font-medium">操作</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5 text-slate-200">
                {loading ? (
                  <tr>
                    <td
                      colSpan={5}
                      className="px-6 py-16 text-center text-slate-400"
                    >
                      正在加载机器列表...
                    </td>
                  </tr>
                ) : error ? (
                  <tr>
                    <td
                      colSpan={5}
                      className="px-6 py-16 text-center text-rose-400"
                    >
                      {error}
                    </td>
                  </tr>
                ) : machines.length === 0 ? (
                  <tr>
                    <td
                      colSpan={5}
                      className="px-6 py-16 text-center text-slate-400"
                    >
                      暂无数据
                    </td>
                  </tr>
                ) : (
                  machines.map((machine, index) => {
                    return (
                      <tr
                        key={`${machine.hostname ?? index}`}
                        className="hover:bg-white/5 whitespace-nowrap"
                      >
                        <td className="px-6 py-4">
                          <div className="font-semibold text-white">
                            {machine.hostname ?? "-"}
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <p className="max-w-xs text-sm text-slate-300">
                            {machine.description ?? "-"}
                          </p>
                        </td>
                        <td className="px-6 py-4">
                          <div className="text-sm text-slate-200">
                            {machine.region ? (
                              <div className="flex items-center gap-2">
                                <ReactCountryFlag
                                  countryCode={machine.region}
                                  svg
                                  style={{
                                    width: "1.5em",
                                    height: "1.5em",
                                  }}
                                />
                                <span>{getCountryName(machine.region)}</span>
                              </div>
                            ) : (
                              "-"
                            )}
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex items-center gap-2">
                            {machine.apiKey ? (
                              <>
                                <code className="rounded-lg border border-white/10 bg-slate-900/50 px-3 py-1.5 text-xs font-mono text-slate-300">
                                  {machine.apiKey.length > 20
                                    ? `${machine.apiKey.substring(0, 20)}...`
                                    : machine.apiKey}
                                </code>
                                <button
                                  type="button"
                                  onClick={async () => {
                                    if (machine.apiKey) {
                                      try {
                                        await navigator.clipboard.writeText(
                                          machine.apiKey
                                        );
                                        setCopySuccess(true);
                                        setTimeout(() => {
                                          setCopySuccess(false);
                                        }, 2000);
                                      } catch (err) {
                                        console.error("复制失败:", err);
                                      }
                                    }
                                  }}
                                  className="rounded-lg border border-white/10 px-3 py-1.5 text-xs text-slate-400 transition hover:border-indigo-400 hover:text-indigo-300"
                                  title="复制 API 密钥"
                                >
                                  复制
                                </button>
                              </>
                            ) : (
                              <span className="text-slate-500">未配置</span>
                            )}
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <div className="flex items-center gap-2">
                            <button
                              type="button"
                              onClick={() => handleEditMachine(machine)}
                              className="rounded-lg border border-indigo-500/30 bg-indigo-500/10 px-3 py-1.5 text-xs text-indigo-400 transition hover:border-indigo-500/50 hover:bg-indigo-500/20"
                              title="编辑机器"
                            >
                              编辑
                            </button>
                            <button
                              type="button"
                              onClick={() =>
                                setDeleteConfirm({
                                  show: true,
                                  machine,
                                  loading: false,
                                })
                              }
                              className="rounded-lg border border-rose-500/30 bg-rose-500/10 px-3 py-1.5 text-xs text-rose-400 transition hover:border-rose-500/50 hover:bg-rose-500/20"
                              title="删除机器"
                            >
                              删除
                            </button>
                          </div>
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
              正在查看第 {pageIndex} / {totalPages} 页，共 {total} 台机器
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

      {copySuccess && (
        <div className="fixed bottom-8 left-1/2 z-50 -translate-x-1/2 transform">
          <div className="flex items-center gap-3 rounded-2xl border border-emerald-500/30 bg-emerald-500/10 px-6 py-4 text-sm text-emerald-400 shadow-lg backdrop-blur">
            <svg
              className="h-5 w-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 13l4 4L19 7"
              />
            </svg>
            <span>API 密钥已复制到剪贴板</span>
          </div>
        </div>
      )}

      {/* 添加删除成功提示 */}
      {deleteSuccess && (
        <div className="fixed bottom-8 left-1/2 z-50 -translate-x-1/2 transform">
          <div className="flex items-center gap-3 rounded-2xl border border-emerald-500/30 bg-emerald-500/10 px-6 py-4 text-sm text-emerald-400 shadow-lg backdrop-blur">
            <svg
              className="h-5 w-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 13l4 4L19 7"
              />
            </svg>
            <span>机器删除成功</span>
          </div>
        </div>
      )}

      {/* 添加更新成功提示 */}
      {updateSuccess && (
        <div className="fixed bottom-8 left-1/2 z-50 -translate-x-1/2 transform">
          <div className="flex items-center gap-3 rounded-2xl border border-emerald-500/30 bg-emerald-500/10 px-6 py-4 text-sm text-emerald-400 shadow-lg backdrop-blur">
            <svg
              className="h-5 w-5"
              fill="none"
              stroke="currentColor"
              viewBox="0 0 24 24"
            >
              <path
                strokeLinecap="round"
                strokeLinejoin="round"
                strokeWidth={2}
                d="M5 13l4 4L19 7"
              />
            </svg>
            <span>机器更新成功</span>
          </div>
        </div>
      )}

      {showCreateModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center px-4 py-10">
          <div
            className="absolute inset-0 bg-black/70 backdrop-blur-sm"
            onClick={() => {
              setShowCreateModal(false);
              setFormData({ description: "", region: "" });
              setSelectedCountry(null);
              setCreateError(null);
            }}
          />
          <div className="relative z-10 w-full max-w-md rounded-3xl border border-white/10 bg-slate-950 p-8 text-white shadow-2xl">
            <div className="flex items-start justify-between gap-4 mb-6">
              <div>
                <p className="text-xs uppercase tracking-[0.4em] text-slate-400">
                  新增机器
                </p>
                <h3 className="mt-2 text-2xl font-semibold">创建新机器</h3>
              </div>
              <button
                type="button"
                onClick={() => {
                  setShowCreateModal(false);
                  setFormData({ description: "", region: "" });
                  setCreateError(null);
                }}
                className="rounded-full border border-white/10 px-4 py-2 text-sm text-slate-300 transition hover:border-indigo-400 hover:text-white"
              >
                关闭
              </button>
            </div>

            <form onSubmit={handleCreateMachine} className="space-y-6">
              {createError && (
                <div className="rounded-2xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-400">
                  {createError}
                </div>
              )}

              <div>
                <label
                  htmlFor="description"
                  className="mb-2 block text-sm font-medium text-slate-300"
                >
                  服务器描述 <span className="text-rose-400">*</span>
                </label>
                <input
                  id="description"
                  type="text"
                  required
                  value={formData.description ?? ""}
                  onChange={(e) =>
                    setFormData({ ...formData, description: e.target.value })
                  }
                  className="w-full rounded-2xl border border-white/10 bg-slate-900 px-4 py-3 text-white placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none"
                  placeholder="请输入服务器描述"
                />
              </div>

              <div>
                <label
                  htmlFor="region"
                  className="mb-2 block text-sm font-medium text-slate-300"
                >
                  服务器地区 <span className="text-rose-400">*</span>
                </label>
                <div className="relative">
                  <select
                    id="region"
                    required
                    value={selectedCountry?.value ?? ""}
                    onChange={(e) => {
                      const country = countryOptions.find(
                        (c: { value: string; label: string }) =>
                          c.value === e.target.value
                      );
                      setSelectedCountry(country || null);
                    }}
                    className="w-full appearance-none rounded-2xl border border-white/10 bg-slate-900 px-4 py-3 pl-12 text-white focus:border-indigo-500 focus:outline-none"
                  >
                    <option value="" className="text-black">
                      请选择服务器地区
                    </option>
                    {countryOptions.map(
                      (country: { value: string; label: string }) => (
                        <option
                          key={country.value}
                          value={country.value}
                          className="text-black"
                        >
                          {country.label}
                        </option>
                      )
                    )}
                  </select>
                  {selectedCountry && (
                    <div className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2">
                      <ReactCountryFlag
                        countryCode={selectedCountry.value}
                        svg
                        style={{
                          width: "1.5em",
                          height: "1.5em",
                        }}
                      />
                    </div>
                  )}
                  <div className="pointer-events-none absolute right-4 top-1/2 -translate-y-1/2">
                    <svg
                      className="h-5 w-5 text-slate-400"
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
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setShowCreateModal(false);
                    setFormData({ description: "", region: "" });
                    setCreateError(null);
                  }}
                  className="flex-1 rounded-2xl border border-white/10 px-4 py-3 text-sm font-medium text-slate-300 transition hover:border-white/20 hover:bg-white/5"
                  disabled={createLoading}
                >
                  取消
                </button>
                <button
                  type="submit"
                  disabled={createLoading}
                  className="flex-1 rounded-2xl border border-indigo-500/50 bg-indigo-500/10 px-4 py-3 text-sm font-medium text-indigo-300 transition hover:border-indigo-400 hover:bg-indigo-500/20 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {createLoading ? "创建中..." : "创建"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 编辑机器弹窗 */}
      {showEditModal && editMachine && (
        <div className="fixed inset-0 z-50 flex items-center justify-center px-4 py-10">
          <div
            className="absolute inset-0 bg-black/70 backdrop-blur-sm"
            onClick={() => {
              setShowEditModal(false);
              setEditMachine(null);
              setEditFormData({ description: null, region: null });
              setSelectedEditCountry(null);
              setUpdateError(null);
            }}
          />
          <div className="relative z-10 w-full max-w-md rounded-3xl border border-white/10 bg-slate-950 p-8 text-white shadow-2xl">
            <div className="flex items-start justify-between gap-4 mb-6">
              <div>
                <p className="text-xs uppercase tracking-[0.4em] text-slate-400">
                  编辑机器
                </p>
                <h3 className="mt-2 text-2xl font-semibold">更新机器信息</h3>
              </div>
              <button
                type="button"
                onClick={() => {
                  setShowEditModal(false);
                  setEditMachine(null);
                  setEditFormData({ description: null, region: null });
                  setUpdateError(null);
                }}
                className="rounded-full border border-white/10 px-4 py-2 text-sm text-slate-300 transition hover:border-indigo-400 hover:text-white"
              >
                关闭
              </button>
            </div>

            <form onSubmit={handleUpdateMachine} className="space-y-6">
              {updateError && (
                <div className="rounded-2xl border border-rose-500/30 bg-rose-500/10 px-4 py-3 text-sm text-rose-400">
                  {updateError}
                </div>
              )}

              <div>
                <label
                  htmlFor="edit-description"
                  className="mb-2 block text-sm font-medium text-slate-300"
                >
                  服务器描述 <span className="text-rose-400">*</span>
                </label>
                <input
                  id="edit-description"
                  type="text"
                  required
                  value={editFormData.description ?? ""}
                  onChange={(e) =>
                    setEditFormData({
                      ...editFormData,
                      description: e.target.value,
                    })
                  }
                  className="w-full rounded-2xl border border-white/10 bg-slate-900 px-4 py-3 text-white placeholder:text-slate-500 focus:border-indigo-500 focus:outline-none"
                  placeholder="请输入服务器描述"
                />
              </div>

              <div>
                <label
                  htmlFor="edit-region"
                  className="mb-2 block text-sm font-medium text-slate-300"
                >
                  服务器地区 <span className="text-rose-400">*</span>
                </label>
                <div className="relative">
                  <select
                    id="edit-region"
                    required
                    value={selectedEditCountry?.value ?? ""}
                    onChange={(e) => {
                      const country = countryOptions.find(
                        (c: { value: string; label: string }) =>
                          c.value === e.target.value
                      );
                      setSelectedEditCountry(country || null);
                    }}
                    className="w-full appearance-none rounded-2xl border border-white/10 bg-slate-900 px-4 py-3 pl-12 text-white focus:border-indigo-500 focus:outline-none"
                  >
                    <option value="" className="text-black">
                      请选择服务器地区
                    </option>
                    {countryOptions.map(
                      (country: { value: string; label: string }) => (
                        <option
                          key={country.value}
                          value={country.value}
                          className="text-black"
                        >
                          {country.label}
                        </option>
                      )
                    )}
                  </select>
                  {selectedEditCountry && (
                    <div className="pointer-events-none absolute left-4 top-1/2 -translate-y-1/2">
                      <ReactCountryFlag
                        countryCode={selectedEditCountry.value}
                        svg
                        style={{
                          width: "1.5em",
                          height: "1.5em",
                        }}
                      />
                    </div>
                  )}
                  <div className="pointer-events-none absolute right-4 top-1/2 -translate-y-1/2">
                    <svg
                      className="h-5 w-5 text-slate-400"
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
                  </div>
                </div>
              </div>

              <div className="flex items-center gap-3 pt-4">
                <button
                  type="button"
                  onClick={() => {
                    setShowEditModal(false);
                    setEditMachine(null);
                    setEditFormData({ description: null, region: null });
                    setUpdateError(null);
                  }}
                  className="flex-1 rounded-2xl border border-white/10 px-4 py-3 text-sm font-medium text-slate-300 transition hover:border-white/20 hover:bg-white/5"
                  disabled={updateLoading}
                >
                  取消
                </button>
                <button
                  type="submit"
                  disabled={updateLoading}
                  className="flex-1 rounded-2xl border border-indigo-500/50 bg-indigo-500/10 px-4 py-3 text-sm font-medium text-indigo-300 transition hover:border-indigo-400 hover:bg-indigo-500/20 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {updateLoading ? "更新中..." : "更新"}
                </button>
              </div>
            </form>
          </div>
        </div>
      )}

      {/* 删除确认弹窗 */}
      {deleteConfirm.show && (
        <div className="fixed inset-0 z-50 flex items-center justify-center px-4 py-10">
          <div
            className="absolute inset-0 bg-black/70 backdrop-blur-sm"
            onClick={() =>
              setDeleteConfirm({ show: false, machine: null, loading: false })
            }
          />
          <div className="relative z-10 w-full max-w-md rounded-3xl border border-white/10 bg-slate-950 p-8 text-white shadow-2xl">
            <div className="flex items-start justify-between gap-4 mb-6">
              <div>
                <p className="text-xs uppercase tracking-[0.4em] text-slate-400">
                  确认删除
                </p>
                <h3 className="mt-2 text-2xl font-semibold">删除机器</h3>
              </div>
              <button
                type="button"
                onClick={() =>
                  setDeleteConfirm({
                    show: false,
                    machine: null,
                    loading: false,
                  })
                }
                className="rounded-full border border-white/10 px-4 py-2 text-sm text-slate-300 transition hover:border-indigo-400 hover:text-white"
                disabled={deleteConfirm.loading}
              >
                关闭
              </button>
            </div>

            <div className="space-y-6">
              <p className="text-slate-300">确定要删除这台机器吗？</p>

              {deleteConfirm.machine && (
                <div className="rounded-2xl border border-white/10 bg-slate-900/50 p-4">
                  <div className="grid grid-cols-2 gap-4 text-sm">
                    <div>
                      <p className="text-slate-400">主机名</p>
                      <p className="text-white font-medium">
                        {deleteConfirm.machine.hostname || "-"}
                      </p>
                    </div>
                    <div>
                      <p className="text-slate-400">描述</p>
                      <p className="text-white font-medium">
                        {deleteConfirm.machine.description || "-"}
                      </p>
                    </div>
                  </div>
                </div>
              )}

              <div className="flex items-center gap-3 pt-4">
                <button
                  type="button"
                  onClick={() =>
                    setDeleteConfirm({
                      show: false,
                      machine: null,
                      loading: false,
                    })
                  }
                  className="flex-1 rounded-2xl border border-white/10 px-4 py-3 text-sm font-medium text-slate-300 transition hover:border-white/20 hover:bg-white/5"
                  disabled={deleteConfirm.loading}
                >
                  取消
                </button>
                <button
                  type="button"
                  onClick={handleDeleteMachine}
                  disabled={deleteConfirm.loading}
                  className="flex-1 rounded-2xl border border-rose-500/50 bg-rose-500/10 px-4 py-3 text-sm font-medium text-rose-300 transition hover:border-rose-500 hover:bg-rose-500/20 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {deleteConfirm.loading ? "删除中..." : "确认删除"}
                </button>
              </div>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
