"use client";

import { useCallback, useEffect, useRef, useState } from "react";
import {
  FileControllerService,
} from "@/api/services/FileControllerService";
import type { SftpService_FileEntry } from "@/api/models/SftpService_FileEntry";

interface FileManagerProps {
  serverId: number;
  hostname: string;
  onOpenSSHConfig: () => void;
}

export function FileManager({
  serverId,
  hostname,
  onOpenSSHConfig,
}: FileManagerProps) {
  const [currentPath, setCurrentPath] = useState("/");
  const [files, setFiles] = useState<SftpService_FileEntry[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selectedFile, setSelectedFile] = useState<SftpService_FileEntry | null>(null);
  const [showUploadModal, setShowUploadModal] = useState(false);
  const [showMkdirModal, setShowMkdirModal] = useState(false);
  const [newDirName, setNewDirName] = useState("");
  const [selectedUploadFile, setSelectedUploadFile] = useState<File | null>(null);
  const [uploading, setUploading] = useState(false);
  const fileInputRef = useRef<HTMLInputElement>(null);

  const loadFiles = useCallback(() => {
    let disposed = false;
    setLoading(true);
    setError(null);

    FileControllerService.listFiles(String(serverId), currentPath)
      .then((response) => {
        if (disposed) return;
        // response.data is Array<SftpService_FileEntry> directly
        setFiles(response.data ?? []);
      })
      .catch((err) => {
        if (disposed) return;
        const message =
          err instanceof Error ? err.message : "获取文件列表失败";
        setError(message);
        if (message.includes("SSH")) {
          setError("SSH连接失败，请检查配置");
        }
      })
      .finally(() => {
        if (disposed) return;
        setLoading(false);
      });

    return () => {
      disposed = true;
    };
  }, [serverId, currentPath]);

  useEffect(() => {
    loadFiles();
  }, [loadFiles]);

  const navigateTo = (path: string) => {
    setCurrentPath(path);
    setSelectedFile(null);
  };

  const navigateUp = () => {
    if (currentPath === "/") return;
    const parts = currentPath.split("/").filter(Boolean);
    parts.pop();
    const newPath = "/" + parts.join("/");
    navigateTo(newPath || "/");
  };

  const handleFileClick = (file: SftpService_FileEntry) => {
    if (file.directory) {
      navigateTo(file.path ?? "/");
    } else {
      setSelectedFile(file);
    }
  };

  const handleDownload = async (file: SftpService_FileEntry) => {
    if (!file.path) return;
    try {
      // Use direct fetch since the generated client doesn't handle binary well
      const response = await fetch(
        `/api/v1/file/download?serverId=${serverId}&path=${encodeURIComponent(file.path)}`,
        {
          credentials: "include",
        }
      );
      if (!response.ok) {
        throw new Error("Download failed");
      }
      const blob = await response.blob();
      const url = URL.createObjectURL(blob);
      const a = document.createElement("a");
      a.href = url;
      a.download = file.name ?? "download";
      document.body.appendChild(a);
      a.click();
      document.body.removeChild(a);
      URL.revokeObjectURL(url);
    } catch (err) {
      console.error("Download failed:", err);
    }
  };

  const handleDelete = async (file: SftpService_FileEntry) => {
    if (!file.path) return;
    if (!confirm(`确定要删除 ${file.name} 吗？`)) return;
    try {
      await FileControllerService.deleteFile(String(serverId), file.path);
      loadFiles();
    } catch (err) {
      console.error("Delete failed:", err);
    }
  };

  const handleMkdir = async () => {
    if (!newDirName.trim()) return;
    const newPath = currentPath.endsWith("/")
      ? currentPath + newDirName
      : currentPath + "/" + newDirName;
    try {
      await FileControllerService.createDirectory(String(serverId), newPath);
      setShowMkdirModal(false);
      setNewDirName("");
      loadFiles();
    } catch (err) {
      console.error("Mkdir failed:", err);
    }
  };

  const handleUpload = async () => {
    if (!selectedUploadFile) return;

    setUploading(true);
    try {
      await FileControllerService.uploadFile(
        String(serverId),
        currentPath,
        currentPath,
        { file: selectedUploadFile }
      );

      setShowUploadModal(false);
      setSelectedUploadFile(null);
      if (fileInputRef.current) fileInputRef.current.value = "";
      loadFiles();
    } catch (err) {
      console.error("Upload failed:", err);
      alert("上传失败: " + (err instanceof Error ? err.message : "未知错误"));
    } finally {
      setUploading(false);
    }
  };

  const handleFileSelect = (event: React.ChangeEvent<HTMLInputElement>) => {
    const file = event.target.files?.[0];
    if (file) {
      setSelectedUploadFile(file);
    }
  };

  const formatSize = (bytes?: number): string => {
    if (!bytes || bytes === 0) return "-";
    const k = 1024;
    const sizes = ["B", "KB", "MB", "GB"];
    const i = Math.floor(Math.log(bytes) / Math.log(k));
    return parseFloat((bytes / Math.pow(k, i)).toFixed(1)) + " " + sizes[i];
  };

  return (
    <div className="flex h-full flex-col">
      {/* Header */}
      <div className="border-b border-white/10 bg-slate-900/50 p-4">
        <div className="flex items-center justify-between">
          <div className="flex items-center gap-4">
            <h2 className="text-lg font-semibold text-white">
              文件管理器
            </h2>
            <span className="text-sm text-slate-400">{hostname}</span>
          </div>
          <button
            type="button"
            onClick={onOpenSSHConfig}
            className="rounded-lg border border-white/10 bg-white/5 px-3 py-1.5 text-xs font-medium text-slate-300 transition hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white"
          >
            SSH配置
          </button>
        </div>
      </div>

      {/* Toolbar */}
      <div className="flex items-center gap-2 border-b border-white/10 bg-slate-900/30 p-3">
        <button
          type="button"
          onClick={navigateUp}
          disabled={currentPath === "/"}
          className="rounded-lg border border-white/10 bg-white/5 px-3 py-1.5 text-sm font-medium text-slate-300 transition hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
        >
          <span className="flex items-center gap-1">
            <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M15 19l-7-7 7-7" />
            </svg>
            返回
          </span>
        </button>
        <button
          type="button"
          onClick={loadFiles}
          className="rounded-lg border border-white/10 bg-white/5 px-3 py-1.5 text-sm font-medium text-slate-300 transition hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white"
        >
          <span className="flex items-center gap-1">
            <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15" />
            </svg>
            刷新
          </span>
        </button>
        <button
          type="button"
          onClick={() => setShowMkdirModal(true)}
          className="rounded-lg border border-white/10 bg-white/5 px-3 py-1.5 text-sm font-medium text-slate-300 transition hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white"
        >
          <span className="flex items-center gap-1">
            <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 13h6m-3-3v6m-9 1V7a2 2 0 012-2h6l2 2h6a2 2 0 012 2v8a2 2 0 01-2 2H5a2 2 0 01-2-2z" />
            </svg>
            新建目录
          </span>
        </button>
        <button
          type="button"
          onClick={() => setShowUploadModal(true)}
          className="rounded-lg bg-indigo-500 px-3 py-1.5 text-sm font-medium text-white transition hover:bg-indigo-600"
        >
          <span className="flex items-center gap-1">
            <svg className="h-4 w-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
              <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M4 16v1a3 3 0 003 3h10a3 3 0 003-3v-1m-4-8l-4-4m0 0L8 8m4-4v12" />
            </svg>
            上传文件
          </span>
        </button>
      </div>

      {/* Breadcrumb */}
      <div className="border-b border-white/10 bg-slate-900/20 px-4 py-2">
        <div className="flex items-center gap-1 text-sm">
          <button
            type="button"
            onClick={() => navigateTo("/")}
            className="text-slate-400 transition hover:text-white"
          >
            /
          </button>
          {currentPath !== "/" &&
            currentPath.split("/").filter(Boolean).map((part, index, arr) => {
              const path = "/" + arr.slice(0, index + 1).join("/");
              return (
                <span key={path} className="flex items-center">
                  <span className="text-slate-600">/</span>
                  <button
                    type="button"
                    onClick={() => navigateTo(path)}
                    className="text-slate-400 transition hover:text-white"
                  >
                    {part}
                  </button>
                </span>
              );
            })}
        </div>
      </div>

      {/* File List */}
      <div className="flex-1 overflow-auto">
        {loading ? (
          <div className="flex items-center justify-center p-8 text-slate-400">
            加载中...
          </div>
        ) : error ? (
          <div className="flex flex-col items-center justify-center gap-4 p-8">
            <div className="rounded-xl border border-rose-500/20 bg-rose-500/10 p-4 text-rose-400">
              {error}
            </div>
            <button
              type="button"
              onClick={onOpenSSHConfig}
              className="rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-slate-300 transition hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white"
            >
              去配置 SSH
            </button>
          </div>
        ) : files.length === 0 ? (
          <div className="flex items-center justify-center p-8 text-slate-400">
            空目录
          </div>
        ) : (
          <table className="w-full">
            <thead className="sticky top-0 bg-slate-900/90 text-left text-xs uppercase text-slate-400">
              <tr>
                <th className="px-4 py-3">名称</th>
                <th className="px-4 py-3">大小</th>
                <th className="px-4 py-3">修改时间</th>
                <th className="px-4 py-3">操作</th>
              </tr>
            </thead>
            <tbody className="text-sm">
              {files.map((file) => (
                <tr
                  key={file.path}
                  className={`border-t border-white/5 cursor-pointer transition hover:bg-white/5 ${
                    selectedFile?.path === file.path ? "bg-indigo-500/10" : ""
                  }`}
                  onClick={() => handleFileClick(file)}
                >
                  <td className="px-4 py-2">
                    <span className="flex items-center gap-2">
                      {file.directory ? (
                        <svg className="h-5 w-5 text-indigo-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M3 7v10a2 2 0 002 2h14a2 2 0 002-2V9a2 2 0 00-2-2h-6l-2-2H5a2 2 0 00-2 2z" />
                        </svg>
                      ) : (
                        <svg className="h-5 w-5 text-slate-400" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                          <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M9 12h6m-6 4h6m2 5H7a2 2 0 01-2-2V5a2 2 0 012-2h5.586a1 1 0 01.707.293l5.414 5.414a1 1 0 01.293.707V19a2 2 0 01-2 2z" />
                        </svg>
                      )}
                      <span className={file.directory ? "text-indigo-300" : "text-white"}>
                        {file.name}
                      </span>
                    </span>
                  </td>
                  <td className="px-4 py-2 text-slate-400">
                    {file.directory ? "-" : formatSize(file.size)}
                  </td>
                  <td className="px-4 py-2 text-slate-400">
                    {file.modifiedTime}
                  </td>
                  <td className="px-4 py-2">
                    <div className="flex gap-2">
                      {!file.directory && (
                        <button
                          type="button"
                          onClick={(e) => {
                            e.stopPropagation();
                            handleDownload(file);
                          }}
                          className="rounded px-2 py-1 text-xs text-slate-400 transition hover:bg-white/10 hover:text-white"
                        >
                          下载
                        </button>
                      )}
                      <button
                        type="button"
                        onClick={(e) => {
                          e.stopPropagation();
                          handleDelete(file);
                        }}
                        className="rounded px-2 py-1 text-xs text-rose-400 transition hover:bg-rose-500/10"
                      >
                        删除
                      </button>
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </div>

      {/* Upload Modal */}
      {showUploadModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="w-full max-w-md rounded-2xl border border-white/10 bg-slate-900 p-6">
            <h3 className="mb-4 text-lg font-semibold text-white">上传文件</h3>
            <p className="mb-4 text-sm text-slate-400">
              上传目录: <span className="text-white">{currentPath}</span>
            </p>
            <input
              ref={fileInputRef}
              type="file"
              onChange={handleFileSelect}
              className="mb-4 w-full rounded-lg border border-white/10 bg-slate-800 p-2 text-sm text-white file:mr-4 file:rounded file:border-0 file:bg-indigo-500 file:px-4 file:py-1 file:text-sm file:font-medium file:text-white"
            />
            {selectedUploadFile && (
              <p className="mb-4 text-sm text-slate-300">
                已选择: <span className="text-indigo-300">{selectedUploadFile.name}</span> ({(selectedUploadFile.size / 1024).toFixed(1)} KB)
              </p>
            )}
            <div className="flex justify-end gap-2">
              <button
                type="button"
                onClick={() => {
                  setShowUploadModal(false);
                  setSelectedUploadFile(null);
                  if (fileInputRef.current) fileInputRef.current.value = "";
                }}
                className="rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-slate-300 transition hover:border-white/20 hover:bg-white/10"
                disabled={uploading}
              >
                取消
              </button>
              <button
                type="button"
                onClick={handleUpload}
                disabled={!selectedUploadFile || uploading}
                className="rounded-lg bg-indigo-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-600 disabled:cursor-not-allowed disabled:bg-indigo-300"
              >
                {uploading ? "上传中..." : "上传"}
              </button>
            </div>
          </div>
        </div>
      )}

      {/* Mkdir Modal */}
      {showMkdirModal && (
        <div className="fixed inset-0 z-50 flex items-center justify-center bg-black/50">
          <div className="w-full max-w-md rounded-2xl border border-white/10 bg-slate-900 p-6">
            <h3 className="mb-4 text-lg font-semibold text-white">新建目录</h3>
            <p className="mb-4 text-sm text-slate-400">
              当前目录: <span className="text-white">{currentPath}</span>
            </p>
            <input
              type="text"
              value={newDirName}
              onChange={(e) => setNewDirName(e.target.value)}
              placeholder="目录名称"
              className="mb-4 w-full rounded-lg border border-white/10 bg-slate-800 px-4 py-2 text-sm text-white placeholder-slate-500 focus:border-indigo-500 focus:outline-none"
              onKeyDown={(e) => e.key === "Enter" && handleMkdir()}
            />
            <div className="flex justify-end gap-2">
              <button
                type="button"
                onClick={() => {
                  setShowMkdirModal(false);
                  setNewDirName("");
                }}
                className="rounded-lg border border-white/10 bg-white/5 px-4 py-2 text-sm font-medium text-slate-300 transition hover:border-white/20 hover:bg-white/10"
              >
                取消
              </button>
              <button
                type="button"
                onClick={handleMkdir}
                className="rounded-lg bg-indigo-500 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-600"
              >
                创建
              </button>
            </div>
          </div>
        </div>
      )}
    </div>
  );
}
