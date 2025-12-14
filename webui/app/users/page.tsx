"use client";

import type { CreateUserRequest } from "@/api/models/CreateUserRequest";
import type { CreateUserView } from "@/api/models/CreateUserView";
import type { ManageUserView } from "@/api/models/ManageUserView";
import type { ResetUserView } from "@/api/models/ResetUserView";
import type { UpdateUserRequest } from "@/api/models/UpdateUserRequest";
import { ManageControllerService } from "@/api/services/ManageControllerService";
import { Modal } from "@/components/Modal";
import { UserRole } from "@/constants/enums";
import { useCallback, useEffect, useMemo, useState } from "react";
import {
  FiCheck,
  FiCopy,
  FiEdit2,
  FiKey,
  FiRefreshCw,
  FiTrash2,
} from "react-icons/fi";

const PAGE_SIZE_OPTIONS = [5, 10, 20];

export default function UsersPage() {
  const [users, setUsers] = useState<ManageUserView[]>([]);
  const [pageIndex, setPageIndex] = useState(1);
  const [pageSize, setPageSize] = useState(PAGE_SIZE_OPTIONS[1]); // Default to 10
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Create User Modal State
  const [showCreateModal, setShowCreateModal] = useState(false);
  const [createLoading, setCreateLoading] = useState(false);
  const [createError, setCreateError] = useState<string | null>(null);
  const [createFormData, setCreateFormData] = useState<CreateUserRequest>({
    username: "",
    fullName: "",
    email: "",
    phone: "",
    roleCode: undefined,
  });

  // Edit User Modal State
  const [showEditModal, setShowEditModal] = useState(false);
  const [editLoading, setEditLoading] = useState(false);
  const [editError, setEditError] = useState<string | null>(null);
  const [editFormData, setEditFormData] = useState<UpdateUserRequest>({
    userId: "",
    username: "",
    fullName: "",
    email: "",
    phone: "",
    roleCode: undefined,
  });

  // Delete User Modal State
  const [showDeleteModal, setShowDeleteModal] = useState(false);
  const [deleteLoading, setDeleteLoading] = useState(false);
  const [deleteError, setDeleteError] = useState<string | null>(null);
  const [userToDelete, setUserToDelete] = useState<ManageUserView | null>(null);

  // Reset Password Modal State
  const [showResetModal, setShowResetModal] = useState(false);
  const [resetLoading, setResetLoading] = useState(false);
  const [resetError, setResetError] = useState<string | null>(null);
  const [userToReset, setUserToReset] = useState<ManageUserView | null>(null);

  // Reset Success Modal State
  const [showResetSuccessModal, setShowResetSuccessModal] = useState(false);
  const [resetResult, setResetResult] = useState<ResetUserView | null>(null);
  const [copyResetSuccess, setCopyResetSuccess] = useState(false);

  // Create Success Modal State
  const [showSuccessModal, setShowSuccessModal] = useState(false);
  const [createdUser, setCreatedUser] = useState<CreateUserView | null>(null);
  const [copySuccess, setCopySuccess] = useState(false);

  // Search Filters (Active)
  const [searchUsername, setSearchUsername] = useState("");
  const [searchFullName, setSearchFullName] = useState("");
  const [searchEmail, setSearchEmail] = useState("");
  const [searchPhone, setSearchPhone] = useState("");
  const [searchRole, setSearchRole] = useState<number | null>(null);

  // Search Inputs (UI)
  const [inputUsername, setInputUsername] = useState("");
  const [inputFullName, setInputFullName] = useState("");
  const [inputEmail, setInputEmail] = useState("");
  const [inputPhone, setInputPhone] = useState("");
  const [inputRole, setInputRole] = useState<number | null>(null);

  const handleSearch = () => {
    setSearchUsername(inputUsername);
    setSearchFullName(inputFullName);
    setSearchEmail(inputEmail);
    setSearchPhone(inputPhone);
    setSearchRole(inputRole);
    setPageIndex(1);
  };

  const handleReset = () => {
    setInputUsername("");
    setInputFullName("");
    setInputEmail("");
    setInputPhone("");
    setInputRole(null);
    setSearchUsername("");
    setSearchFullName("");
    setSearchEmail("");
    setSearchPhone("");
    setSearchRole(null);
    setPageIndex(1);
  };

  const loadUsers = useCallback(() => {
    let disposed = false;
    const frame = requestAnimationFrame(() => {
      if (disposed) return;
      setLoading(true);
      setError(null);
    });

    const request = ManageControllerService.list({
      pageIndex: pageIndex - 1, // API usually expects 0-based index
      pageSize,
      username: searchUsername || undefined,
      fullName: searchFullName || undefined,
      email: searchEmail || undefined,
      phone: searchPhone || undefined,
      ruleCode: searchRole,
    });

    request
      .then((response) => {
        if (disposed) return;
        const list = response?.data?.data ?? [];
        setUsers(list);
        setTotal(response?.data?.total ?? 0);
      })
      .catch((err) => {
        if (disposed) return;
        const message = err instanceof Error ? err.message : "获取用户列表失败";
        setError(message);
        setUsers([]);
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
  }, [
    pageIndex,
    pageSize,
    searchUsername,
    searchFullName,
    searchEmail,
    searchPhone,
    searchRole,
  ]);

  useEffect(() => loadUsers(), [loadUsers]);

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

  const getRoleName = (code?: number | null) => {
    if (code === undefined || code === null) return "-";
    for (const key in UserRole) {
      const roleKey = key as keyof typeof UserRole;
      if (UserRole[roleKey].code === code) {
        return UserRole[roleKey].key;
      }
    }
    return "UNKNOWN";
  };

  const handleCreateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    setCreateLoading(true);
    setCreateError(null);

    try {
      const response = await ManageControllerService.createUser(createFormData);
      if (response.data) {
        setCreatedUser(response.data);
        setShowCreateModal(false);
        setShowSuccessModal(true);
        // Reset form
        setCreateFormData({
          username: "",
          fullName: "",
          email: "",
          phone: "",
          roleCode: undefined,
        });
        loadUsers();
      }
    } catch (err) {
      const message = err instanceof Error ? err.message : "创建用户失败";
      setCreateError(message);
    } finally {
      setCreateLoading(false);
    }
  };

  const copyUserInfo = async () => {
    if (!createdUser) return;
    const info = `
用户名: ${createdUser.username}
初始密码: ${createdUser.password}
姓名: ${createdUser.fullName || "-"}
邮箱: ${createdUser.email}
手机号: ${createdUser.phone || "-"}
角色: ${getRoleName(createdUser.roleCode)}
`.trim();

    try {
      await navigator.clipboard.writeText(info);
      setCopySuccess(true);
      setTimeout(() => setCopySuccess(false), 2000);
    } catch (err) {
      console.error("复制失败", err);
    }
  };

  // Edit Handlers
  const handleEditClick = (user: ManageUserView) => {
    setEditFormData({
      userId: user.id?.toString(),
      username: user.username,
      fullName: user.fullName,
      email: user.email,
      phone: user.phone,
      roleCode: user.code || undefined,
    });
    setEditError(null);
    setShowEditModal(true);
  };

  const handleUpdateUser = async (e: React.FormEvent) => {
    e.preventDefault();
    setEditLoading(true);
    setEditError(null);
    try {
      await ManageControllerService.updateUser(editFormData);
      setShowEditModal(false);
      loadUsers();
    } catch (err) {
      const message = err instanceof Error ? err.message : "更新用户失败";
      setEditError(message);
    } finally {
      setEditLoading(false);
    }
  };

  // Delete Handlers
  const handleDeleteClick = (user: ManageUserView) => {
    setUserToDelete(user);
    setDeleteError(null);
    setShowDeleteModal(true);
  };

  const handleDeleteUser = async () => {
    if (!userToDelete?.id) return;
    setDeleteLoading(true);
    setDeleteError(null);
    try {
      await ManageControllerService.deleteUser({
        userId: userToDelete.id.toString(),
      });
      setShowDeleteModal(false);
      loadUsers();
    } catch (err) {
      const message = err instanceof Error ? err.message : "删除用户失败";
      setDeleteError(message);
    } finally {
      setDeleteLoading(false);
    }
  };

  // Reset Password Handlers
  const handleResetClick = (user: ManageUserView) => {
    setUserToReset(user);
    setResetError(null);
    setShowResetModal(true);
  };

  const handleResetPassword = async () => {
    if (!userToReset?.id) return;
    setResetLoading(true);
    setResetError(null);
    try {
      const response = await ManageControllerService.resetUserPassword({
        userId: userToReset.id.toString(),
      });
      setShowResetModal(false);
      setResetResult(response.data || null);
      setShowResetSuccessModal(true);
    } catch (err) {
      const message = err instanceof Error ? err.message : "重置密码失败";
      setResetError(message);
    } finally {
      setResetLoading(false);
    }
  };

  const copyResetInfo = async () => {
    if (!resetResult?.password) return;
    const info = `
用户名: ${resetResult.username}
新密码: ${resetResult.password}
姓名: ${resetResult.fullName || "-"}
邮箱: ${resetResult.email}
手机号: ${resetResult.phone || "-"}
角色: ${getRoleName(resetResult.roleCode)}
`.trim();
    try {
      await navigator.clipboard.writeText(info);
      setCopyResetSuccess(true);
      setTimeout(() => setCopyResetSuccess(false), 2000);
    } catch (err) {
      console.error("复制失败", err);
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
                用户管理
              </h1>
              <p className="mt-4 text-base text-slate-300">
                管理系统用户及其权限。
              </p>
            </div>
            <div className="flex items-center gap-4 rounded-2xl border border-white/10 bg-white/5 px-6 py-4 text-sm text-slate-200">
              <div>
                <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
                  总人数
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
          {/* Filter Section */}
          <div className="mb-6 rounded-2xl border border-white/5 bg-slate-900/50 p-4">
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2 lg:grid-cols-3 xl:grid-cols-5">
              <div className="space-y-1">
                <label className="text-xs font-medium text-slate-400">
                  用户名
                </label>
                <input
                  type="text"
                  value={inputUsername}
                  onChange={(e) => setInputUsername(e.target.value)}
                  placeholder="输入用户名"
                  className="w-full rounded-xl border border-white/10 bg-slate-950 px-3 py-2 text-sm text-white placeholder:text-slate-600 focus:border-indigo-500 focus:outline-none"
                />
              </div>
              <div className="space-y-1">
                <label className="text-xs font-medium text-slate-400">
                  姓名
                </label>
                <input
                  type="text"
                  value={inputFullName}
                  onChange={(e) => setInputFullName(e.target.value)}
                  placeholder="输入姓名"
                  className="w-full rounded-xl border border-white/10 bg-slate-950 px-3 py-2 text-sm text-white placeholder:text-slate-600 focus:border-indigo-500 focus:outline-none"
                />
              </div>
              <div className="space-y-1">
                <label className="text-xs font-medium text-slate-400">
                  邮箱
                </label>
                <input
                  type="text"
                  value={inputEmail}
                  onChange={(e) => setInputEmail(e.target.value)}
                  placeholder="输入邮箱"
                  className="w-full rounded-xl border border-white/10 bg-slate-950 px-3 py-2 text-sm text-white placeholder:text-slate-600 focus:border-indigo-500 focus:outline-none"
                />
              </div>
              <div className="space-y-1">
                <label className="text-xs font-medium text-slate-400">
                  手机号
                </label>
                <input
                  type="text"
                  value={inputPhone}
                  onChange={(e) => setInputPhone(e.target.value)}
                  placeholder="输入手机号"
                  className="w-full rounded-xl border border-white/10 bg-slate-950 px-3 py-2 text-sm text-white placeholder:text-slate-600 focus:border-indigo-500 focus:outline-none"
                />
              </div>
              <div className="space-y-1">
                <label className="text-xs font-medium text-slate-400">
                  角色
                </label>
                <select
                  value={inputRole ?? ""}
                  onChange={(e) =>
                    setInputRole(e.target.value ? Number(e.target.value) : null)
                  }
                  className="w-full rounded-xl border border-white/10 bg-slate-950 px-3 py-2 text-sm text-white focus:border-indigo-500 focus:outline-none"
                >
                  <option value="">全部</option>
                  {Object.values(UserRole).map((role) => (
                    <option key={role.key} value={role.code}>
                      {role.key}
                    </option>
                  ))}
                </select>
              </div>
            </div>
            <div className="mt-4 flex justify-end gap-3 border-t border-white/5 pt-4">
              <button
                type="button"
                onClick={() => setShowCreateModal(true)}
                className="mr-auto rounded-xl border border-indigo-500/50 bg-indigo-500/10 px-4 py-2 text-sm font-medium text-indigo-300 transition hover:border-indigo-400 hover:bg-indigo-500/20"
              >
                新增用户
              </button>
              <button
                type="button"
                onClick={handleReset}
                className="rounded-xl border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
              >
                重置
              </button>
              <button
                type="button"
                onClick={handleSearch}
                className="rounded-xl bg-indigo-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-600 shadow-lg shadow-indigo-500/20"
              >
                查询
              </button>
            </div>
          </div>

          <div className="flex flex-col gap-4 pb-6 sm:flex-row sm:items-center sm:justify-between">
            <div>
              <p className="text-lg font-semibold text-white">用户列表</p>
              <p className="text-sm text-slate-400">
                当前为第 {pageIndex} 页（显示 {pageSize} 条）
              </p>
            </div>
            <div className="flex items-center gap-3">
              <button
                type="button"
                onClick={() => loadUsers()}
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
                  <th className="px-6 py-4 font-medium">用户名</th>
                  <th className="px-6 py-4 font-medium">姓名</th>
                  <th className="px-6 py-4 font-medium">邮箱</th>
                  <th className="px-6 py-4 font-medium">手机号</th>
                  <th className="px-6 py-4 font-medium">角色</th>
                  <th className="px-6 py-4 font-medium text-right">操作</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5 text-slate-200">
                {loading ? (
                  <tr>
                    <td
                      colSpan={6}
                      className="px-6 py-16 text-center text-slate-400"
                    >
                      正在加载用户列表...
                    </td>
                  </tr>
                ) : error ? (
                  <tr>
                    <td
                      colSpan={6}
                      className="px-6 py-16 text-center text-rose-400"
                    >
                      {error}
                    </td>
                  </tr>
                ) : users.length === 0 ? (
                  <tr>
                    <td
                      colSpan={6}
                      className="px-6 py-16 text-center text-slate-400"
                    >
                      暂无数据
                    </td>
                  </tr>
                ) : (
                  users.map((user, index) => {
                    return (
                      <tr
                        key={`${user.id ?? index}`}
                        className="hover:bg-white/5 whitespace-nowrap"
                      >
                        <td className="px-6 py-4">
                          <div className="font-semibold text-white">
                            {user.username ?? "-"}
                          </div>
                        </td>
                        <td className="px-6 py-4">
                          <p className="max-w-xs text-sm text-slate-300">
                            {user.fullName ?? "-"}
                          </p>
                        </td>
                        <td className="px-6 py-4">
                          <p className="text-sm text-slate-300">
                            {user.email ?? "-"}
                          </p>
                        </td>
                        <td className="px-6 py-4">
                          <p className="text-sm text-slate-300">
                            {user.phone ?? "-"}
                          </p>
                        </td>
                        <td className="px-6 py-4">
                          <div className="inline-flex items-center rounded-lg border border-white/10 bg-slate-900/50 px-2 py-1 text-xs font-medium text-slate-300">
                            {getRoleName(user.code)}
                          </div>
                        </td>
                        <td className="px-6 py-4 text-right">
                          <div className="flex justify-end gap-2">
                            <button
                              onClick={() => handleResetClick(user)}
                              title="重置密码"
                              className="rounded-lg p-2 text-slate-400 transition hover:bg-amber-500/10 hover:text-amber-400"
                            >
                              <FiKey className="h-4 w-4" />
                            </button>
                            <button
                              onClick={() => handleEditClick(user)}
                              title="编辑"
                              className="rounded-lg p-2 text-slate-400 transition hover:bg-indigo-500/10 hover:text-indigo-400"
                            >
                              <FiEdit2 className="h-4 w-4" />
                            </button>
                            <button
                              onClick={() => handleDeleteClick(user)}
                              title="删除"
                              className="rounded-lg p-2 text-slate-400 transition hover:bg-rose-500/10 hover:text-rose-400"
                            >
                              <FiTrash2 className="h-4 w-4" />
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

          <div className="mt-6 flex items-center justify-between">
            <div className="text-sm text-slate-400">
              显示 {users.length > 0 ? (pageIndex - 1) * pageSize + 1 : 0} 到{" "}
              {Math.min(pageIndex * pageSize, total)} 条，共 {total} 条
            </div>
            <div className="flex gap-2">
              <button
                type="button"
                onClick={() => handlePageChange(pageIndex - 1)}
                disabled={!canPrev}
                className="rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-300 transition hover:border-indigo-400 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
              >
                上一页
              </button>
              <button
                type="button"
                onClick={() => handlePageChange(pageIndex + 1)}
                disabled={!canNext}
                className="rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-sm text-slate-300 transition hover:border-indigo-400 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
              >
                下一页
              </button>
            </div>
          </div>
        </section>
      </main>

      {/* Create User Modal */}
      <Modal
        isOpen={showCreateModal}
        onClose={() => setShowCreateModal(false)}
        title="新增用户"
      >
        <form onSubmit={handleCreateUser} className="space-y-6">
          <div className="space-y-4">
            <div className="space-y-1">
              <label className="text-sm font-medium text-slate-300">
                用户名 <span className="text-rose-500">*</span>
              </label>
              <input
                type="text"
                required
                value={createFormData.username}
                onChange={(e) =>
                  setCreateFormData((prev) => ({
                    ...prev,
                    username: e.target.value,
                  }))
                }
                className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                placeholder="请输入用户名"
              />
            </div>
            <div className="space-y-1">
              <label className="text-sm font-medium text-slate-300">
                邮箱 <span className="text-rose-500">*</span>
              </label>
              <input
                type="email"
                required
                value={createFormData.email}
                onChange={(e) =>
                  setCreateFormData((prev) => ({
                    ...prev,
                    email: e.target.value,
                  }))
                }
                className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                placeholder="请输入邮箱"
              />
            </div>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300">
                  姓名
                </label>
                <input
                  type="text"
                  value={createFormData.fullName || ""}
                  onChange={(e) =>
                    setCreateFormData((prev) => ({
                      ...prev,
                      fullName: e.target.value || null,
                    }))
                  }
                  className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  placeholder="请输入姓名"
                />
              </div>
              <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300">
                  手机号
                </label>
                <input
                  type="tel"
                  value={createFormData.phone || ""}
                  onChange={(e) =>
                    setCreateFormData((prev) => ({
                      ...prev,
                      phone: e.target.value || null,
                    }))
                  }
                  className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  placeholder="请输入手机号"
                />
              </div>
            </div>
            <div className="space-y-1">
              <label className="text-sm font-medium text-slate-300">
                角色 <span className="text-rose-500">*</span>
              </label>
              <select
                required
                value={createFormData.roleCode ?? ""}
                onChange={(e) =>
                  setCreateFormData((prev) => ({
                    ...prev,
                    roleCode: Number(e.target.value),
                  }))
                }
                className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
              >
                <option value="" disabled>
                  请选择角色
                </option>
                {Object.values(UserRole).map((role) => (
                  <option key={role.key} value={role.code}>
                    {role.key}
                  </option>
                ))}
              </select>
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
              onClick={() => setShowCreateModal(false)}
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
              确认创建
            </button>
          </div>
        </form>
      </Modal>

      {/* Edit User Modal */}
      <Modal
        isOpen={showEditModal}
        onClose={() => setShowEditModal(false)}
        title="编辑用户"
      >
        <form onSubmit={handleUpdateUser} className="space-y-6">
          <div className="space-y-4">
            <div className="space-y-1">
              <label className="text-sm font-medium text-slate-300">
                用户名
              </label>
              <input
                type="text"
                value={editFormData.username || ""}
                onChange={(e) =>
                  setEditFormData((prev) => ({
                    ...prev,
                    username: e.target.value,
                  }))
                }
                className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                placeholder="请输入用户名"
              />
            </div>
            <div className="space-y-1">
              <label className="text-sm font-medium text-slate-300">
                邮箱 <span className="text-rose-500">*</span>
              </label>
              <input
                type="email"
                required
                value={editFormData.email || ""}
                onChange={(e) =>
                  setEditFormData((prev) => ({
                    ...prev,
                    email: e.target.value,
                  }))
                }
                className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                placeholder="请输入邮箱"
              />
            </div>
            <div className="grid grid-cols-1 gap-4 sm:grid-cols-2">
              <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300">
                  姓名
                </label>
                <input
                  type="text"
                  value={editFormData.fullName || ""}
                  onChange={(e) =>
                    setEditFormData((prev) => ({
                      ...prev,
                      fullName: e.target.value || null,
                    }))
                  }
                  className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  placeholder="请输入姓名"
                />
              </div>
              <div className="space-y-1">
                <label className="text-sm font-medium text-slate-300">
                  手机号
                </label>
                <input
                  type="tel"
                  value={editFormData.phone || ""}
                  onChange={(e) =>
                    setEditFormData((prev) => ({
                      ...prev,
                      phone: e.target.value || null,
                    }))
                  }
                  className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
                  placeholder="请输入手机号"
                />
              </div>
            </div>
            <div className="space-y-1">
              <label className="text-sm font-medium text-slate-300">
                角色 <span className="text-rose-500">*</span>
              </label>
              <select
                required
                value={editFormData.roleCode ?? ""}
                onChange={(e) =>
                  setEditFormData((prev) => ({
                    ...prev,
                    roleCode: Number(e.target.value),
                  }))
                }
                className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-white focus:border-indigo-500 focus:outline-none"
              >
                {Object.values(UserRole).map((role) => (
                  <option key={role.key} value={role.code}>
                    {role.key}
                  </option>
                ))}
              </select>
            </div>
          </div>

          {editError && (
            <div className="rounded-xl bg-rose-500/10 p-4 text-sm text-rose-400">
              {editError}
            </div>
          )}

          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={() => setShowEditModal(false)}
              className="rounded-xl border border-white/10 bg-white/5 px-6 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
            >
              取消
            </button>
            <button
              type="submit"
              disabled={editLoading}
              className="flex items-center gap-2 rounded-xl bg-indigo-500 px-6 py-2 text-sm font-medium text-white transition hover:bg-indigo-600 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {editLoading && (
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

      {/* Delete Confirmation Modal */}
      <Modal
        isOpen={showDeleteModal}
        onClose={() => setShowDeleteModal(false)}
        title="删除用户"
      >
        <div className="space-y-6">
          <div className="rounded-xl bg-rose-500/10 p-4 text-rose-200">
            <div className="flex items-start gap-3">
              <FiTrash2 className="mt-0.5 h-5 w-5 shrink-0" />
              <div className="text-sm">
                <p className="font-medium">确认删除该用户？</p>
                <p className="mt-1 opacity-80">
                  用户 &quot;{userToDelete?.username}&quot;
                  将被永久删除，此操作无法撤销。
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
              onClick={() => setShowDeleteModal(false)}
              className="rounded-xl border border-white/10 bg-white/5 px-6 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
            >
              取消
            </button>
            <button
              type="button"
              onClick={handleDeleteUser}
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

      {/* Reset Password Confirmation Modal */}
      <Modal
        isOpen={showResetModal}
        onClose={() => setShowResetModal(false)}
        title="重置密码"
      >
        <div className="space-y-6">
          <div className="rounded-xl bg-amber-500/10 p-4 text-amber-200">
            <div className="flex items-start gap-3">
              <FiRefreshCw className="mt-0.5 h-5 w-5 shrink-0" />
              <div className="text-sm">
                <p className="font-medium">确认重置密码？</p>
                <p className="mt-1 opacity-80">
                  用户 &quot;{userToReset?.username}&quot;
                  的密码将被重置为系统生成的随机密码。
                </p>
              </div>
            </div>
          </div>

          {resetError && (
            <div className="rounded-xl bg-rose-500/10 p-4 text-sm text-rose-400">
              {resetError}
            </div>
          )}

          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={() => setShowResetModal(false)}
              className="rounded-xl border border-white/10 bg-white/5 px-6 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
            >
              取消
            </button>
            <button
              type="button"
              onClick={handleResetPassword}
              disabled={resetLoading}
              className="flex items-center gap-2 rounded-xl bg-amber-500 px-6 py-2 text-sm font-medium text-white transition hover:bg-amber-600 disabled:cursor-not-allowed disabled:opacity-50"
            >
              {resetLoading && (
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
              确认重置
            </button>
          </div>
        </div>
      </Modal>

      {/* Reset Password Success Modal */}
      <Modal
        isOpen={showResetSuccessModal}
        onClose={() => setShowResetSuccessModal(false)}
        title="重置成功"
      >
        <div className="space-y-6">
          <div className="flex flex-col items-center justify-center py-6 text-center">
            <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-emerald-500/10 text-emerald-500">
              <FiCheck className="h-8 w-8" />
            </div>
            <h4 className="text-lg font-semibold text-white">密码重置成功</h4>
            <p className="mt-2 text-sm text-slate-400">
              请妥善保管新密码，该密码仅显示一次。
            </p>
          </div>

          {resetResult && (
            <div className="rounded-xl border border-white/10 bg-slate-900 p-6">
              <dl className="space-y-4 text-sm">
                <div className="flex justify-between">
                  <dt className="text-slate-400">用户名</dt>
                  <dd className="font-mono text-white">
                    {resetResult.username}
                  </dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-400">新密码</dt>
                  <dd className="font-mono text-emerald-400">
                    {resetResult.password}
                  </dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-400">姓名</dt>
                  <dd className="text-white">{resetResult.fullName || "-"}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-400">邮箱</dt>
                  <dd className="text-white">{resetResult.email}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-400">手机号</dt>
                  <dd className="text-white">{resetResult.phone || "-"}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-400">角色</dt>
                  <dd className="text-white">
                    {getRoleName(resetResult.roleCode)}
                  </dd>
                </div>
              </dl>
            </div>
          )}

          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={() => setShowResetSuccessModal(false)}
              className="rounded-xl border border-white/10 bg-white/5 px-6 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
            >
              关闭
            </button>
            <button
              type="button"
              onClick={copyResetInfo}
              className="flex items-center gap-2 rounded-xl bg-indigo-500 px-6 py-2 text-sm font-medium text-white transition hover:bg-indigo-600 shadow-lg shadow-indigo-500/20"
            >
              {copyResetSuccess ? (
                <>
                  <FiCheck className="h-4 w-4" />
                  已复制
                </>
              ) : (
                <>
                  <FiCopy className="h-4 w-4" />
                  复制信息
                </>
              )}
            </button>
          </div>
        </div>
      </Modal>

      {/* Success Modal */}
      <Modal
        isOpen={showSuccessModal}
        onClose={() => setShowSuccessModal(false)}
        title="创建成功"
      >
        <div className="space-y-6">
          <div className="flex flex-col items-center justify-center py-6 text-center">
            <div className="mb-4 flex h-16 w-16 items-center justify-center rounded-full bg-emerald-500/10 text-emerald-500">
              <FiCheck className="h-8 w-8" />
            </div>
            <h4 className="text-lg font-semibold text-white">用户创建成功</h4>
            <p className="mt-2 text-sm text-slate-400">
              请妥善保管以下用户信息，初始密码仅显示一次。
            </p>
          </div>

          {createdUser && (
            <div className="rounded-xl border border-white/10 bg-slate-900 p-6">
              <dl className="space-y-4 text-sm">
                <div className="flex justify-between">
                  <dt className="text-slate-400">用户名</dt>
                  <dd className="font-mono text-white">
                    {createdUser.username}
                  </dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-400">初始密码</dt>
                  <dd className="font-mono text-emerald-400">
                    {createdUser.password}
                  </dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-400">姓名</dt>
                  <dd className="text-white">{createdUser.fullName || "-"}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-400">邮箱</dt>
                  <dd className="text-white">{createdUser.email}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-400">手机号</dt>
                  <dd className="text-white">{createdUser.phone || "-"}</dd>
                </div>
                <div className="flex justify-between">
                  <dt className="text-slate-400">角色</dt>
                  <dd className="text-white">
                    {getRoleName(createdUser.roleCode)}
                  </dd>
                </div>
              </dl>
            </div>
          )}

          <div className="flex justify-end gap-3 pt-4">
            <button
              type="button"
              onClick={() => setShowSuccessModal(false)}
              className="rounded-xl border border-white/10 bg-white/5 px-6 py-2 text-sm text-slate-300 transition hover:bg-white/10 hover:text-white"
            >
              关闭
            </button>
            <button
              type="button"
              onClick={copyUserInfo}
              className="flex items-center gap-2 rounded-xl bg-indigo-500 px-6 py-2 text-sm font-medium text-white transition hover:bg-indigo-600 shadow-lg shadow-indigo-500/20"
            >
              {copySuccess ? (
                <>
                  <FiCheck className="h-4 w-4" />
                  已复制
                </>
              ) : (
                <>
                  <FiCopy className="h-4 w-4" />
                  复制信息
                </>
              )}
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
