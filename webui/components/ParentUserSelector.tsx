import React, { useState, useEffect, useCallback } from "react";
import { ManageControllerService } from "@/api/services/ManageControllerService";
import type { ManageUserView } from "@/api/models/ManageUserView";
import { UserRole } from "@/api/enums/enums";
import { FiSearch, FiUser, FiX } from "react-icons/fi";

interface ParentUserSelectorProps {
  value?: string | null; // parentId
  onChange: (value: string | null) => void;
  disabled?: boolean;
  fallbackLabel?: string;
  required?: boolean;
}

export function ParentUserSelector({
  value,
  onChange,
  disabled,
  fallbackLabel,
  required,
}: ParentUserSelectorProps) {
  const [isOpen, setIsOpen] = useState(false);
  const [selectedUser, setSelectedUser] = useState<ManageUserView | null>(null);

  // List state
  const [users, setUsers] = useState<ManageUserView[]>([]);
  const [loading, setLoading] = useState(false);
  const [total, setTotal] = useState(0);
  const [pageIndex, setPageIndex] = useState(1);
  const [pageSize, setPageSize] = useState(10);

  // Filters
  const [searchUsername, setSearchUsername] = useState("");
  const [filterRole, setFilterRole] = useState<number>(UserRole.ADMIN.code); // Default to ADMIN

  // Fetch selected user details if value is present but selectedUser is missing
  // (Optional: if we want to show the name of the pre-selected parent)
  // For now, we might rely on the parent component passing the initial object or just ID.
  // If only ID is passed, we might need to fetch the user to show the name.
  // But for simplicity, let's assume we just show the ID if name is unknown, or we fetch it.
  // Actually, let's try to find it in the list or fetch it separately?
  // Fetching separately is better but extra API call.
  // Let's just show the ID if we don't have the name, or ask the parent to pass the name?
  // The API list returns ManageUserView.
  // Let's implement a "fetch by ID" or just show "User #ID" initially?
  // Or maybe we can't fetch a single user easily?
  // ManageControllerService.list supports filtering by username, maybe we can find it?
  // Let's leave that for now and just focus on the selector.

  const loadUsers = useCallback(async () => {
    setLoading(true);
    try {
      const res = await ManageControllerService.list({
        pageIndex: pageIndex - 1, // API uses 0-based
        pageSize,
        username: searchUsername || undefined,
        ruleCode: filterRole, // Filter by role
      });
      setUsers(res.data?.data || []);
      setTotal(res.data?.total || 0);
    } catch (error) {
      console.error("Failed to load users", error);
    } finally {
      setLoading(false);
    }
  }, [pageIndex, pageSize, searchUsername, filterRole]);

  useEffect(() => {
    if (isOpen) {
      loadUsers();
    }
  }, [isOpen, loadUsers]);

  // Handle selection
  const handleSelect = (user: ManageUserView) => {
    setSelectedUser(user);
    onChange(user.id?.toString() || null);
    setIsOpen(false);
  };

  const handleClear = () => {
    setSelectedUser(null);
    onChange(null);
  };

  return (
    <>
      <div className="relative w-full">
        <input
          type="text"
          readOnly
          disabled={disabled}
          value={
            selectedUser
              ? `${selectedUser.username} (${
                  selectedUser.fullName || "No Name"
                })`
              : value
              ? fallbackLabel || value
              : ""
          }
          placeholder="Select Parent User"
          className="w-full cursor-pointer rounded-xl border border-white/10 bg-slate-900 px-4 py-2.5 pl-10 text-sm text-white placeholder-slate-500 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500 disabled:opacity-50"
          onClick={() => !disabled && setIsOpen(true)}
          required={required}
        />
        <FiUser className="absolute left-3 top-3 text-slate-400" />
        {(value || selectedUser) && !disabled && (
          <button
            type="button"
            onClick={(e) => {
              e.stopPropagation();
              handleClear();
            }}
            className="absolute right-3 top-3 text-slate-400 hover:text-white"
          >
            <FiX />
          </button>
        )}
      </div>

      {/* Selector Modal */}
      {isOpen && (
        <div className="fixed inset-0 z-[60] flex items-center justify-center px-4 py-10">
          <div
            className="absolute inset-0 bg-black/70 backdrop-blur-sm"
            onClick={() => setIsOpen(false)}
          />
          <div className="relative z-10 w-full max-w-4xl rounded-3xl border border-white/10 bg-slate-950 p-8 text-white shadow-2xl">
            <div className="flex items-start justify-between gap-4 mb-6">
              <h3 className="text-xl font-semibold">选择上级用户</h3>
              <button
                onClick={() => setIsOpen(false)}
                className="rounded-full border border-white/10 px-4 py-2 text-sm text-slate-300 hover:text-white"
              >
                关闭
              </button>
            </div>

            {/* Filters */}
            <div className="mb-4 flex gap-4">
              <div className="relative flex-1">
                <FiSearch className="absolute left-3 top-3 text-slate-400" />
                <input
                  type="text"
                  placeholder="搜索用户名..."
                  value={searchUsername}
                  onChange={(e) => setSearchUsername(e.target.value)}
                  className="w-full rounded-xl border border-white/10 bg-slate-900 px-4 py-2 pl-10 text-sm text-white placeholder-slate-500 outline-none focus:border-indigo-500"
                />
              </div>
              <select
                value={filterRole}
                onChange={(e) => setFilterRole(Number(e.target.value))}
                className="rounded-xl border border-white/10 bg-slate-900 px-4 py-2 text-sm text-white outline-none focus:border-indigo-500"
              >
                <option value={UserRole.ADMIN.code}>管理员 (Admin)</option>
                <option value={UserRole.SUPER_ADMIN.code}>
                  超级管理员 (Super Admin)
                </option>
              </select>
              <button
                onClick={loadUsers}
                className="rounded-xl bg-indigo-600 px-4 py-2 text-sm font-medium text-white hover:bg-indigo-700"
              >
                搜索
              </button>
            </div>

            {/* List */}
            <div className="min-h-[300px] overflow-x-auto">
              <table className="w-full text-left text-sm">
                <thead>
                  <tr className="border-b border-white/10 text-slate-400">
                    <th className="px-4 py-3 font-medium">ID</th>
                    <th className="px-4 py-3 font-medium">用户名</th>
                    <th className="px-4 py-3 font-medium">全名</th>
                    <th className="px-4 py-3 font-medium">角色</th>
                    <th className="px-4 py-3 font-medium text-right">操作</th>
                  </tr>
                </thead>
                <tbody className="divide-y divide-white/5">
                  {loading ? (
                    <tr>
                      <td
                        colSpan={5}
                        className="py-8 text-center text-slate-500"
                      >
                        加载中...
                      </td>
                    </tr>
                  ) : users.length === 0 ? (
                    <tr>
                      <td
                        colSpan={5}
                        className="py-8 text-center text-slate-500"
                      >
                        暂无数据
                      </td>
                    </tr>
                  ) : (
                    users.map((user) => (
                      <tr key={user.id} className="group hover:bg-white/5">
                        <td className="px-4 py-3 text-slate-400">{user.id}</td>
                        <td className="px-4 py-3 font-medium text-white">
                          {user.username}
                        </td>
                        <td className="px-4 py-3 text-slate-300">
                          {user.fullName || "-"}
                        </td>
                        <td className="px-4 py-3 text-slate-300">
                          {Object.values(UserRole).find(
                            (r) => r.code === user.roleCode
                          )?.key || user.roleCode}
                        </td>
                        <td className="px-4 py-3 text-right">
                          <button
                            onClick={() => handleSelect(user)}
                            className="rounded-lg bg-emerald-500/10 px-3 py-1 text-xs font-medium text-emerald-500 transition hover:bg-emerald-500/20"
                          >
                            选择
                          </button>
                        </td>
                      </tr>
                    ))
                  )}
                </tbody>
              </table>
            </div>

            {/* Pagination */}
            <div className="mt-4 flex items-center justify-between border-t border-white/10 pt-4">
              <div className="text-xs text-slate-400">共 {total} 条</div>
              <div className="flex gap-2">
                <select
                  value={pageSize}
                  onChange={(e) => {
                    setPageSize(Number(e.target.value));
                    setPageIndex(1);
                  }}
                  className="rounded-lg border border-white/10 bg-slate-900 px-2 py-1 text-xs text-slate-300 outline-none"
                >
                  <option value={5}>5 条/页</option>
                  <option value={10}>10 条/页</option>
                  <option value={20}>20 条/页</option>
                </select>
                <div className="flex gap-1">
                  <button
                    disabled={pageIndex === 1}
                    onClick={() => setPageIndex((p) => p - 1)}
                    className="rounded-lg border border-white/10 px-2 py-1 text-xs text-slate-300 hover:bg-white/5 disabled:opacity-50"
                  >
                    上一页
                  </button>
                  <span className="flex items-center px-2 text-xs text-slate-400">
                    {pageIndex}
                  </span>
                  <button
                    disabled={pageIndex * pageSize >= total}
                    onClick={() => setPageIndex((p) => p + 1)}
                    className="rounded-lg border border-white/10 px-2 py-1 text-xs text-slate-300 hover:bg-white/5 disabled:opacity-50"
                  >
                    下一页
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
