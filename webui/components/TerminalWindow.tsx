"use client";

import { useEffect, useRef, useState } from "react";
import { Terminal } from "xterm";
import { FitAddon } from "xterm-addon-fit";
import "xterm/css/xterm.css";

interface TerminalWindowProps {
  serverId: number;
  hostname: string;
  onBack?: () => void;
}

// Helper function to get cookie value
function getCookie(name: string): string | null {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) {
    return parts.pop()?.split(";").shift() ?? null;
  }
  return null;
}

export function TerminalWindow({
  serverId,
  hostname,
  onBack,
}: TerminalWindowProps) {
  const terminalRef = useRef<HTMLDivElement>(null);
  const xtermRef = useRef<Terminal | null>(null);
  const wsRef = useRef<WebSocket | null>(null);
  const fitAddonRef = useRef<FitAddon | null>(null);
  const [connectionStatus, setConnectionStatus] = useState<
    "disconnected" | "connecting" | "connected" | "error"
  >("disconnected");
  const [errorMessage, setErrorMessage] = useState<string>("");

  const connect = () => {
    const token = getCookie("beeyes-token");
    if (!token) {
      setConnectionStatus("error");
      setErrorMessage("未找到认证令牌，请重新登录");
      return;
    }

    setConnectionStatus("connecting");
    setErrorMessage("");

    // Determine WebSocket protocol based on current page protocol
    const protocol = window.location.protocol === "https:" ? "wss:" : "ws:";
    const host = window.location.host;
    const wsUrl = `${protocol}//${host}/terminal/${serverId}/${token}`;

    try {
      const ws = new WebSocket(wsUrl);
      wsRef.current = ws;

      ws.onopen = () => {
        setConnectionStatus("connected");
        if (xtermRef.current) {
          xtermRef.current.write("\r\n\x1b[32m已连接到服务器\x1b[0m\r\n");
        }
      };

      ws.onmessage = (event) => {
        if (xtermRef.current && event.data) {
          xtermRef.current.write(event.data);
        }
      };

      ws.onerror = () => {
        setConnectionStatus("error");
        setErrorMessage("WebSocket 连接错误");
      };

      ws.onclose = (event) => {
        setConnectionStatus("disconnected");
        if (xtermRef.current) {
          let reason = "连接已断开";
          if (event.reason) {
            reason += `: ${event.reason}`;
          }
          xtermRef.current.write(`\r\n\x1b[33m${reason}\x1b[0m\r\n`);
        }
      };
    } catch (err) {
      setConnectionStatus("error");
      setErrorMessage(err instanceof Error ? err.message : "连接失败");
    }
  };

  const disconnect = () => {
    if (wsRef.current) {
      wsRef.current.close();
      wsRef.current = null;
    }
    setConnectionStatus("disconnected");
  };

  useEffect(() => {
    if (!terminalRef.current) return;

    // Initialize xterm.js
    const term = new Terminal({
      cursorBlink: true,
      fontSize: 14,
      fontFamily: 'Menlo, Monaco, "Courier New", monospace',
      theme: {
        background: "#0f172a",
        foreground: "#e2e8f0",
        cursor: "#818cf8",
        black: "#1e293b",
        red: "#f87171",
        green: "#4ade80",
        yellow: "#facc15",
        blue: "#60a5fa",
        magenta: "#c084fc",
        cyan: "#22d3ee",
        white: "#f1f5f9",
        brightBlack: "#475569",
        brightRed: "#fca5a5",
        brightGreen: "#86efac",
        brightYellow: "#fde047",
        brightBlue: "#93c5fd",
        brightMagenta: "#d8b4fe",
        brightCyan: "#67e8f9",
        brightWhite: "#f8fafc",
      },
    });

    const fitAddon = new FitAddon();
    term.loadAddon(fitAddon);
    term.open(terminalRef.current);
    fitAddon.fit();

    xtermRef.current = term;
    fitAddonRef.current = fitAddon;

    // Welcome message
    term.writeln("\x1b[1;36m欢迎使用 BeEyes 远程终端\x1b[0m");
    term.writeln(`\x1b[90m服务器: ${hostname}\x1b[0m`);
    term.writeln("\x1b[90m点击连接按钮开始\x1b[0m");
    term.writeln("");

    // Handle terminal input
    term.onData((data) => {
      if (wsRef.current && wsRef.current.readyState === WebSocket.OPEN) {
        wsRef.current.send(data);
      }
    });

    // Handle window resize
    const handleResize = () => {
      if (fitAddonRef.current) {
        fitAddonRef.current.fit();
      }
    };
    window.addEventListener("resize", handleResize);

    return () => {
      window.removeEventListener("resize", handleResize);
      if (wsRef.current) {
        wsRef.current.close();
      }
      term.dispose();
    };
  }, [hostname]);

  // Reconnect when serverId changes
  useEffect(() => {
    if (connectionStatus === "connected") {
      disconnect();
    }
  }, [serverId]);

  const getStatusColor = () => {
    switch (connectionStatus) {
      case "connected":
        return "bg-emerald-500";
      case "connecting":
        return "bg-yellow-500 animate-pulse";
      case "error":
        return "bg-rose-500";
      default:
        return "bg-slate-500";
    }
  };

  const getStatusText = () => {
    switch (connectionStatus) {
      case "connected":
        return "已连接";
      case "connecting":
        return "连接中...";
      case "error":
        return "连接错误";
      default:
        return "未连接";
    }
  };

  return (
    <div className="flex h-full flex-col">
      {/* Status Bar */}
      <div className="flex items-center justify-between border-b border-white/10 bg-slate-900/80 px-4 py-2">
        <div className="flex items-center gap-3">
          <div className="flex items-center gap-2">
            <div className={`h-2 w-2 rounded-full ${getStatusColor()}`} />
            <span className="text-xs font-medium text-slate-300">
              {getStatusText()}
            </span>
          </div>
          {errorMessage && (
            <span className="text-xs text-rose-400">{errorMessage}</span>
          )}
        </div>
        <div className="flex gap-2">
          {onBack && (
            <button
              type="button"
              onClick={onBack}
              className="rounded-lg border border-white/10 bg-white/5 px-3 py-1 text-xs font-medium text-slate-300 transition hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white"
            >
              返回配置
            </button>
          )}
          {connectionStatus === "disconnected" ||
          connectionStatus === "error" ? (
            <button
              type="button"
              onClick={connect}
              className="rounded-lg bg-indigo-500 px-3 py-1 text-xs font-medium text-white transition hover:bg-indigo-600"
            >
              连接
            </button>
          ) : (
            <button
              type="button"
              onClick={disconnect}
              className="rounded-lg bg-rose-500 px-3 py-1 text-xs font-medium text-white transition hover:bg-rose-600"
            >
              断开
            </button>
          )}
        </div>
      </div>

      {/* Terminal */}
      <div className="flex-1 overflow-hidden bg-slate-950 p-4">
        <div ref={terminalRef} className="h-full w-full" />
      </div>
    </div>
  );
}
