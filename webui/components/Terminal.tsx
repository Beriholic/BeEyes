"use client";
import { PopMsgOK, PopMsgWarn } from "@/store/pops";
import { useEffect, useRef } from "react";
import { Terminal } from "xterm";
import { AttachAddon } from "xterm-addon-attach";
import { FitAddon } from "xterm-addon-fit";
import "xterm/css/xterm.css";

interface TerminalComponentProps {
  className?: string;
  id: string;
  rows: number;
  fontSize?: number;
  onDispose?: () => void;
}

export default function TerminalComponent({
  className = "",
  id,
  fontSize = 15,
  rows,
  onDispose,
}: TerminalComponentProps) {
  const terminalRef = useRef<HTMLDivElement>(null);
  const terminalInstance = useRef<Terminal | null>(null);
  const socketRef = useRef<WebSocket | null>(null);

  useEffect(() => {
    let term: Terminal | null;
    let socket: WebSocket | null;
    let attachAddon: AttachAddon | null;
    let fitAddon: FitAddon | null;

    const timeout = setTimeout(() => {
      if (!terminalRef.current) {
        console.error("Terminal container is not mounted.");
        return;
      }

      term = new Terminal({
        lineHeight: 1.2,
        rows: rows,
        fontSize: fontSize,
        fontFamily: "Monaco, Menlo, Consolas, 'Courier New', monospace",
        fontWeight: "bold",
        theme: { background: "#000000" },
        cursorBlink: true,
        cursorStyle: "underline",
        scrollback: 100,
        tabStopWidth: 4,
      });

      const token = localStorage.getItem("token") ?? "";
      socket = new WebSocket(`ws://127.0.0.1:8080/terminal/${id}/${token}`);
      socketRef.current = socket;

      attachAddon = new AttachAddon(socket);
      term.loadAddon(attachAddon);

      fitAddon = new FitAddon();
      term.loadAddon(fitAddon);

      term.open(terminalRef.current);
      term.focus();

      fitAddon.fit();
      const resizeObserver = new ResizeObserver(() => {
        fitAddon?.fit();
      });
      resizeObserver.observe(terminalRef.current);

      socket.onclose = (evt) => {
        if (evt.code !== 1000) {
          PopMsgWarn({
            title: `连接失败: ${evt.reason}`,
            description: "请检查网络连接和 SSH 端口是否开放",
          });
        } else {
          PopMsgOK({
            title: "远程SSH连接已断开",
          });
        }
        onDispose?.();
        resizeObserver.disconnect();
      };

      terminalInstance.current = term;
    }, 100);

    return () => {
      clearTimeout(timeout);
      socket?.close();
      term?.dispose();
      attachAddon?.dispose();
      fitAddon?.dispose();
    };
  }, [id, onDispose, rows, fontSize]);

  return <div className={className} ref={terminalRef} />;
}
