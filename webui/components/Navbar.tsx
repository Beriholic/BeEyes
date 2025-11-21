"use client";

import type { UserBaseView } from "@/api/models/UserBaseView";
import { AuthControllerService } from "@/api/services/AuthControllerService";
import { ProfileControllerService } from "@/api/services/ProfileControllerService";
import Link from "next/link";
import { usePathname, useRouter } from "next/navigation";
import Jdenticon from "react-jdenticon";
import { useEffect, useMemo, useRef, useState } from "react";

const NAV_ITEMS = [
  { label: "监控", key: "monitor", href: "/" },
  { label: "机器", key: "machines", href: "/machines" },
  { label: "终端", key: "terminal", href: "/terminal" },
  { label: "告警", key: "alarm", href: "/alarm" },
];

const getActiveKey = (pathname: string | null): string => {
  if (!pathname) return "monitor";
  if (pathname.startsWith("/settings")) return "";
  const matched = NAV_ITEMS.find((item) =>
    item.href === "/"
      ? pathname === "/"
      : pathname.startsWith(item.href)
  );
  return matched?.key ?? "monitor";
};

export function Navbar() {
  const pathname = usePathname();
  const router = useRouter();
  const activeKey = useMemo(() => getActiveKey(pathname), [pathname]);
  const [user, setUser] = useState<UserBaseView | null>(null);
  const [loading, setLoading] = useState(false);
  const [menuOpen, setMenuOpen] = useState(false);
  const [logoutPending, setLogoutPending] = useState(false);
  const menuRef = useRef<HTMLDivElement | null>(null);

  const isHidden = useMemo(() => {
    if (!pathname) return false;
    return ["/login"].some((prefix) => pathname.startsWith(prefix));
  }, [pathname]);

  useEffect(() => {
    if (isHidden) return;

    let disposed = false;
    const frame = requestAnimationFrame(() => {
      if (disposed) return;
      setLoading(true);
    });
    const request = ProfileControllerService.getSelfInfo();

    request
      .then((response) => {
        if (disposed) return;
        setUser(response?.data ?? null);
      })
      .catch(() => {
        if (disposed) return;
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
  }, [isHidden]);

  useEffect(() => {
    if (!menuOpen) return;
    const handleClick = (event: MouseEvent) => {
      if (!menuRef.current) return;
      if (!menuRef.current.contains(event.target as Node)) {
        setMenuOpen(false);
      }
    };
    document.addEventListener("mousedown", handleClick);
    return () => document.removeEventListener("mousedown", handleClick);
  }, [menuOpen]);

  const handleSettings = () => {
    setMenuOpen(false);
    router.push("/settings");
  };

  const handleLogout = async () => {
    if (logoutPending) return;
    setLogoutPending(true);
    try {
      await AuthControllerService.logout();
    } catch (error) {
      console.error(error);
    } finally {
      setLogoutPending(false);
      setMenuOpen(false);
      router.replace("/login");
    }
  };

  if (isHidden) {
    return null;
  }

  return (
    <nav className="sticky top-0 z-40 border-b border-white/5 bg-slate-950 shadow-[0_10px_30px_rgba(0,0,0,0.35)] supports-[backdrop-filter]:bg-slate-950/90 supports-[backdrop-filter]:backdrop-blur">
      <div className="mx-auto flex w-full max-w-6xl items-center justify-between px-6 py-4 sm:px-8 lg:px-12">
        <div className="flex items-center gap-8">
          <Link
            href="/"
            className="text-lg font-semibold tracking-widest text-white transition hover:text-indigo-300"
          >
            BeEyes
          </Link>
          <div className="flex items-center gap-4 text-sm text-slate-400">
            {NAV_ITEMS.map((item) => {
              const isActive = activeKey === item.key;
              return (
                <Link
                  key={item.key}
                  href={item.href}
                  className={`rounded-full px-4 py-2 transition ${
                    isActive
                      ? "bg-white/10 text-white shadow-[0_0_30px_rgba(59,130,246,0.25)]"
                      : "hover:text-white"
                  }`}
                >
                  {item.label}
                </Link>
              );
            })}
          </div>
        </div>
        <div className="flex items-center gap-4">
          <div className="text-right text-xs text-slate-400">
            <p className="font-semibold text-sm text-white">
              {loading ? "加载中..." : user?.username ?? "-"}
            </p>
            <p>{user?.fullName ?? ""}</p>
          </div>
          <div className="relative" ref={menuRef}>
            <button
              type="button"
              aria-label="用户菜单"
              onClick={() => setMenuOpen((prev) => !prev)}
              className="flex h-12 w-12 items-center justify-center overflow-hidden rounded-2xl bg-white/5 shadow-lg transition hover:ring-2 hover:ring-indigo-400 focus:outline-none focus-visible:ring-2 focus-visible:ring-indigo-400"
            >
              {!loading ? (
                <Jdenticon
                  size={48}
                  value={String(user?.id ?? user?.username ?? "guest")}
                />
              ) : (
                <span className="text-xs text-white">···</span>
              )}
            </button>
            {menuOpen && (
              <div className="absolute right-0 mt-3 w-44 overflow-hidden rounded-2xl border border-white/10 bg-slate-900/95 p-2 text-sm text-slate-200 shadow-2xl backdrop-blur">
                <button
                  type="button"
                  className="flex w-full items-center justify-between rounded-xl px-3 py-2 transition hover:bg-white/10"
                  onClick={handleSettings}
                >
                  设置
                  <span className="text-xs text-slate-500">⌘ ,</span>
                </button>
                <button
                  type="button"
                  className="mt-1 flex w-full items-center justify-between rounded-xl px-3 py-2 text-rose-300 transition hover:bg-rose-500/20"
                  onClick={handleLogout}
                  disabled={logoutPending}
                >
                  退出登录
                  <span className="text-xs text-rose-200">
                    {logoutPending ? "..." : "⌘ Q"}
                  </span>
                </button>
              </div>
            )}
          </div>
        </div>
      </div>
    </nav>
  );
}

