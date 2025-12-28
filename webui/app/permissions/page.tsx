"use client";

import type { ManageUserView } from "@/api/models/ManageUserView";
import type { QueryPermissionUserListRequest } from "@/api/models/QueryPermissionUserListRequest";
import { PermissionControllerService } from "@/api/services/PermissionControllerService";
import { UserRole, Permission, PermissionKey } from "@/api/enums/enums";
import { RestBean_UserRoleCode } from "@/api/models/RestBean_UserRoleCode";
import { Modal } from "@/components/Modal";
import { useCallback, useEffect, useMemo, useState } from "react";
import { FiEdit2, FiCheck, FiX } from "react-icons/fi";

const PAGE_SIZE_OPTIONS = [5, 10, 20];

export default function PermissionsPage() {
  const [users, setUsers] = useState<ManageUserView[]>([]);
  const [pageIndex, setPageIndex] = useState(1);
  const [pageSize, setPageSize] = useState(PAGE_SIZE_OPTIONS[1]);
  const [total, setTotal] = useState(0);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  // Filter State
  const [searchUsername, setSearchUsername] = useState("");
  const [searchFullName, setSearchFullName] = useState("");
  const [searchEmail, setSearchEmail] = useState("");
  const [searchPhone, setSearchPhone] = useState("");
  const [searchRole, setSearchRole] = useState<number | null>(null);

  // Input State
  const [inputUsername, setInputUsername] = useState("");
  const [inputFullName, setInputFullName] = useState("");
  const [inputEmail, setInputEmail] = useState("");
  const [inputPhone, setInputPhone] = useState("");
  const [inputRole, setInputRole] = useState<number | null>(null);

  // Permission Modal State
  const [showPermissionModal, setShowPermissionModal] = useState(false);
  const [selectedUser, setSelectedUser] = useState<ManageUserView | null>(null);
  const [selectedPermissions, setSelectedPermissions] = useState<number[]>([]);
  const [availablePermissions, setAvailablePermissions] = useState<
    PermissionKey[]
  >([]);
  const [saveLoading, setSaveLoading] = useState(false);
  const [fetchLoading, setFetchLoading] = useState(false);
  const [saveError, setSaveError] = useState<string | null>(null);

  // Current User Role
  const [currentUserRole, setCurrentUserRole] = useState<string | null>(null);

  const loadUsers = useCallback(() => {
    let disposed = false;
    setLoading(true);
    setError(null);

    const requestBody: QueryPermissionUserListRequest = {
      pageIndex: pageIndex - 1,
      pageSize,
      username: searchUsername || undefined,
      fullName: searchFullName || undefined,
      email: searchEmail || undefined,
      phone: searchPhone || undefined,
      ruleCode: searchRole ?? undefined,
    };

    PermissionControllerService.getManageUserList(requestBody)
      .then((response) => {
        if (disposed) return;
        setUsers(response.data?.data ?? []);
        setTotal(response.data?.total ?? 0);
      })
      .catch((err) => {
        if (disposed) return;
        setError(err instanceof Error ? err.message : "获取管理用户列表失败");
      })
      .finally(() => {
        if (disposed) return;
        setLoading(false);
      });

    return () => {
      disposed = true;
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

  useEffect(() => {
    loadUsers();
  }, [loadUsers]);

  // Fetch available permissions once
  useEffect(() => {
    PermissionControllerService.getPermissionEnum()
      .then((response) => {
        // Map the string literals to our enum keys
        const mapped = (response.data || []).map((p) => {
          if (p === "CRATE_SERVER") return "CREATE_SERVER";
          return p;
        }) as PermissionKey[];
        setAvailablePermissions(mapped);
      })
      .catch((err) => {
        console.error("获取权限枚举失败", err);
      });

    // Fetch current user role
    PermissionControllerService.getCurrentUserRole()
      .then((response) => {
        setCurrentUserRole(response.data || null);
      })
      .catch((err) => {
        console.error("获取当前用户角色失败", err);
      });
  }, []);

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

  const totalPages = useMemo(
    () => Math.max(Math.ceil(total / pageSize), 1),
    [total, pageSize]
  );
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

  const handleEditPermissions = async (user: ManageUserView) => {
    if (!user.id) return;
    setSelectedUser(user);
    setFetchLoading(true);
    setSaveError(null);
    setSelectedPermissions([]);
    setShowPermissionModal(true);

    try {
      const response = await PermissionControllerService.getUserPermissions(
        user.id.toString()
      );
      const userPerms = response.data || [];

      // Map API string literals to our internal codes
      const codes = userPerms
        .map((p) => {
          const str = String(p);
          if (str === "CRATE_SERVER" || str === "CREATE_SERVER")
            return Permission.CREATE_SERVER.code as number;
          if (str === "UPDATE_SERVER")
            return Permission.UPDATE_SERVER.code as number;
          if (str === "DELETE_SERVER")
            return Permission.DELETE_SERVER.code as number;
          if (str === "SSH_CONNECT")
            return Permission.SSH_CONNECT.code as number;
          return null;
        })
        .filter((c): c is number => typeof c === "number");

      setSelectedPermissions(codes);
    } catch (err) {
      setSaveError(err instanceof Error ? err.message : "获取用户权限失败");
    } finally {
      setFetchLoading(false);
    }
  };

  const togglePermission = (code: number) => {
    setSelectedPermissions((prev) =>
      prev.includes(code) ? prev.filter((p) => p !== code) : [...prev, code]
    );
  };

  const handleSavePermissions = async () => {
    if (!selectedUser?.id) return;
    setSaveLoading(true);
    setSaveError(null);

    try {
      await PermissionControllerService.setPermission(
        selectedUser.id.toString(),
        selectedPermissions
      );
      setShowPermissionModal(false);
      // Success feedback could be helpful
    } catch (err) {
      setSaveError(err instanceof Error ? err.message : "设置权限失败");
    } finally {
      setSaveLoading(false);
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
                权限管理
              </h1>
              <p className="mt-4 text-base text-slate-300">
                分配和管理系统用户的具体功能权限。
              </p>
            </div>
            <div className="flex items-center gap-4 rounded-2xl border border-white/10 bg-white/5 px-6 py-4 text-sm text-slate-200">
              <div>
                <p className="text-xs uppercase tracking-[0.3em] text-slate-400">
                  可管理人数
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
              {currentUserRole !== RestBean_UserRoleCode.data.ADMIN && (
                <div className="space-y-1">
                  <label className="text-xs font-medium text-slate-400">
                    角色
                  </label>
                  <select
                    value={inputRole ?? ""}
                    onChange={(e) =>
                      setInputRole(
                        e.target.value ? Number(e.target.value) : null
                      )
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
              )}
            </div>
            <div className="mt-4 flex justify-end gap-3 border-t border-white/5 pt-4">
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
              <p className="text-lg font-semibold text-white">用户权限列表</p>
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
              <select
                value={pageSize}
                onChange={(e) => {
                  setPageSize(Number(e.target.value));
                  setPageIndex(1);
                }}
                className="rounded-2xl border border-white/10 bg-slate-900 px-4 py-2 text-sm text-white focus:border-indigo-500 focus:outline-none"
              >
                {PAGE_SIZE_OPTIONS.map((size) => (
                  <option key={size} value={size}>
                    {size} 条/页
                  </option>
                ))}
              </select>
            </div>
          </div>

          <div className="overflow-hidden rounded-2xl border border-white/5 bg-slate-950">
            <table className="min-w-full divide-y divide-white/5 text-left text-sm">
              <thead className="bg-slate-900/80 text-xs uppercase tracking-wide text-slate-400">
                <tr>
                  <th className="px-6 py-4 font-medium">用户名</th>
                  <th className="px-6 py-4 font-medium">姓名</th>
                  <th className="px-6 py-4 font-medium">邮箱</th>
                  <th className="px-6 py-4 font-medium">角色</th>
                  <th className="px-6 py-4 font-medium text-right">操作</th>
                </tr>
              </thead>
              <tbody className="divide-y divide-white/5 text-slate-200">
                {loading ? (
                  <tr>
                    <td
                      colSpan={5}
                      className="px-6 py-16 text-center text-slate-400"
                    >
                      正在加载...
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
                ) : users.length === 0 ? (
                  <tr>
                    <td
                      colSpan={5}
                      className="px-6 py-16 text-center text-slate-400"
                    >
                      暂无数据
                    </td>
                  </tr>
                ) : (
                  users.map((user) => (
                    <tr
                      key={user.id}
                      className="hover:bg-white/5 transition-colors"
                    >
                      <td className="px-6 py-4 font-semibold text-white">
                        {user.username}
                      </td>
                      <td className="px-6 py-4 text-slate-300">
                        {user.fullName || "-"}
                      </td>
                      <td className="px-6 py-4 text-slate-300">
                        {user.email || "-"}
                      </td>
                      <td className="px-6 py-4">
                        <span className="inline-flex items-center rounded-lg border border-white/10 bg-slate-900/50 px-2 py-1 text-xs font-medium text-slate-300">
                          {getRoleName(user.roleCode)}
                        </span>
                      </td>
                      <td className="px-6 py-4 text-right">
                        <button
                          onClick={() => handleEditPermissions(user)}
                          className="inline-flex items-center gap-2 rounded-lg bg-indigo-500/10 px-3 py-1.5 text-xs font-medium text-indigo-400 transition hover:bg-indigo-500/20"
                        >
                          <FiEdit2 className="h-3 w-3" />
                          <span>权限设置</span>
                        </button>
                      </td>
                    </tr>
                  ))
                )}
              </tbody>
            </table>
          </div>

          <div className="mt-6 flex items-center justify-between">
            <div className="text-sm text-slate-400">共 {total} 条数据</div>
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

      {/* Permission Modal */}
      <Modal
        isOpen={showPermissionModal}
        onClose={() => setShowPermissionModal(false)}
        title={`设置权限 - ${selectedUser?.username}`}
      >
        <div className="space-y-6">
          <p className="text-sm text-slate-400">
            请选择要分配给该用户的权限。点击图标进行勾选。
          </p>

          {fetchLoading ? (
            <div className="flex flex-col items-center justify-center py-12 text-slate-400">
              <div className="mb-4 h-8 w-8 animate-spin rounded-full border-2 border-indigo-500/20 border-t-indigo-500" />
              <p>正在获取当前权限...</p>
            </div>
          ) : (
            <div className="grid grid-cols-1 gap-3 sm:grid-cols-2">
              {availablePermissions.map((pk) => {
                const perm = Permission[pk];
                if (!perm) return null;
                const isSelected = selectedPermissions.includes(perm.code);
                return (
                  <button
                    key={pk}
                    onClick={() => togglePermission(perm.code)}
                    className={`flex items-center justify-between rounded-xl border p-4 transition-all ${
                      isSelected
                        ? "border-indigo-500 bg-indigo-500/10 text-white"
                        : "border-white/10 bg-slate-900 text-slate-400 hover:border-white/20 hover:bg-slate-800"
                    }`}
                  >
                    <span className="text-sm font-medium">{perm.key}</span>
                    <div
                      className={`rounded-full p-1 ${
                        isSelected
                          ? "bg-indigo-500 text-white"
                          : "bg-white/5 text-transparent"
                      }`}
                    >
                      <FiCheck className="h-3 w-3" />
                    </div>
                  </button>
                );
              })}
            </div>
          )}

          {saveError && (
            <div className="rounded-xl bg-rose-500/10 p-4 text-sm text-rose-400">
              {saveError}
            </div>
          )}

          <div className="flex justify-end gap-3 pt-4">
            <button
              onClick={() => setShowPermissionModal(false)}
              className="rounded-xl border border-white/10 bg-white/5 px-6 py-2 text-sm font-medium text-slate-300 transition hover:bg-white/10"
            >
              取消
            </button>
            <button
              onClick={handleSavePermissions}
              disabled={saveLoading}
              className="inline-flex items-center gap-2 rounded-xl bg-indigo-500 px-6 py-2 text-sm font-medium text-white transition hover:bg-indigo-600 disabled:opacity-50"
            >
              {saveLoading && (
                <div className="h-4 w-4 animate-spin rounded-full border-2 border-white/20 border-t-white" />
              )}
              <span>保存设置</span>
            </button>
          </div>
        </div>
      </Modal>
    </div>
  );
}
