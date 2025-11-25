"use client";

import type { UpdateSSHConfigRequest } from "@/api/models/UpdateSSHConfigRequest";
import { TerminalControllerService } from "@/api/services/TerminalControllerService";
import { useEffect, useState } from "react";

interface SSHConfigModalProps {
  isOpen: boolean;
  onClose: () => void;
  serverId: number;
  hostname: string;
}

export function SSHConfigModal({
  isOpen,
  onClose,
  serverId,
  hostname,
}: SSHConfigModalProps) {
  const [formData, setFormData] = useState({
    name: "",
    password: "",
    port: 22,
  });
  const [loading, setLoading] = useState(false);
  const [message, setMessage] = useState<{
    type: "success" | "error";
    text: string;
  } | null>(null);

  // Reset form when modal opens
  useEffect(() => {
    if (isOpen) {
      setFormData({ name: "", password: "", port: 22 });
      setMessage(null);
    }
  }, [isOpen]);

  // Handle ESC key
  useEffect(() => {
    const handleEsc = (e: KeyboardEvent) => {
      if (e.key === "Escape" && isOpen) {
        onClose();
      }
    };
    window.addEventListener("keydown", handleEsc);
    return () => window.removeEventListener("keydown", handleEsc);
  }, [isOpen, onClose]);

  const handleSubmit = async (e: React.FormEvent) => {
    e.preventDefault();

    if (!formData.name || !formData.password) {
      setMessage({ type: "error", text: "用户名和密码不能为空" });
      return;
    }

    if (formData.port < 1 || formData.port > 65535) {
      setMessage({ type: "error", text: "端口号必须在 1-65535 之间" });
      return;
    }

    setLoading(true);
    setMessage(null);

    try {
      const request: UpdateSSHConfigRequest = {
        serverId,
        name: formData.name,
        password: formData.password,
        port: formData.port,
      };
      await TerminalControllerService.updateSshConfig(request);
      setMessage({ type: "success", text: "SSH配置保存成功" });

      // Auto-close modal after 1 second on success
      setTimeout(() => {
        onClose();
      }, 1000);
    } catch (err) {
      const errorMsg = err instanceof Error ? err.message : "保存失败，请重试";
      setMessage({ type: "error", text: errorMsg });
    } finally {
      setLoading(false);
    }
  };

  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center">
      {/* Backdrop */}
      <div
        className="absolute inset-0 bg-black/60 backdrop-blur-sm"
        onClick={onClose}
      />

      {/* Modal */}
      <div className="relative z-10 w-full max-w-md rounded-2xl border border-white/10 bg-slate-900 shadow-2xl">
        {/* Header */}
        <div className="border-b border-white/10 p-6">
          <div className="flex items-start justify-between">
            <div>
              <h2 className="text-xl font-semibold text-white">{hostname}</h2>
              <p className="mt-1 text-sm text-slate-400">SSH 连接配置</p>
            </div>
            <button
              type="button"
              onClick={onClose}
              className="rounded-lg p-1 text-slate-400 transition hover:bg-white/10 hover:text-white"
            >
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
                  d="M6 18L18 6M6 6l12 12"
                />
              </svg>
            </button>
          </div>
        </div>

        {/* Form */}
        <form onSubmit={handleSubmit}>
          <div className="p-6">
            <div className="space-y-5">
              {/* Username */}
              <div>
                <label
                  htmlFor="ssh-username"
                  className="block text-sm font-medium text-slate-300"
                >
                  用户名
                </label>
                <input
                  id="ssh-username"
                  type="text"
                  value={formData.name}
                  onChange={(e) =>
                    setFormData((prev) => ({ ...prev, name: e.target.value }))
                  }
                  placeholder="root"
                  className="mt-2 w-full rounded-xl border border-white/10 bg-slate-800 px-4 py-3 text-white placeholder-slate-500 transition focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
                />
              </div>

              {/* Password */}
              <div>
                <label
                  htmlFor="ssh-password"
                  className="block text-sm font-medium text-slate-300"
                >
                  密码
                </label>
                <input
                  id="ssh-password"
                  type="password"
                  value={formData.password}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      password: e.target.value,
                    }))
                  }
                  placeholder="••••••••"
                  className="mt-2 w-full rounded-xl border border-white/10 bg-slate-800 px-4 py-3 text-white placeholder-slate-500 transition focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
                />
              </div>

              {/* Port */}
              <div>
                <label
                  htmlFor="ssh-port"
                  className="block text-sm font-medium text-slate-300"
                >
                  端口
                </label>
                <input
                  id="ssh-port"
                  type="number"
                  value={formData.port}
                  onChange={(e) =>
                    setFormData((prev) => ({
                      ...prev,
                      port: parseInt(e.target.value) || 22,
                    }))
                  }
                  min={1}
                  max={65535}
                  className="mt-2 w-full rounded-xl border border-white/10 bg-slate-800 px-4 py-3 text-white placeholder-slate-500 transition focus:border-indigo-500 focus:outline-none focus:ring-2 focus:ring-indigo-500/20"
                />
              </div>

              {/* Message */}
              {message && (
                <div
                  className={`rounded-xl border px-4 py-3 text-sm ${
                    message.type === "success"
                      ? "border-emerald-500/30 bg-emerald-500/10 text-emerald-300"
                      : "border-rose-500/30 bg-rose-500/10 text-rose-300"
                  }`}
                >
                  {message.text}
                </div>
              )}
            </div>
          </div>

          {/* Actions */}
          <div className="border-t border-white/10 p-6">
            <div className="flex gap-3">
              <button
                type="button"
                onClick={onClose}
                disabled={loading}
                className="flex-1 rounded-xl border border-white/10 bg-white/5 px-4 py-3 text-sm font-medium text-slate-300 transition hover:border-white/20 hover:bg-white/10 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
              >
                取消
              </button>
              <button
                type="submit"
                disabled={loading}
                className="flex-1 rounded-xl bg-gradient-to-r from-indigo-500 to-purple-500 px-4 py-3 text-sm font-semibold text-white shadow-lg shadow-indigo-500/30 transition hover:shadow-indigo-500/50 disabled:cursor-not-allowed disabled:opacity-50 disabled:shadow-none"
              >
                {loading ? "保存中..." : "保存配置"}
              </button>
            </div>
          </div>
        </form>
      </div>
    </div>
  );
}
