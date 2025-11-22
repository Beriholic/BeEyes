"use client";

import { AuthControllerService } from "@/api/services/AuthControllerService";
import { Button } from "@/components/Button";
import { Card } from "@/components/Card";
import { Input } from "@/components/Input";
import { PageBackground } from "@/components/PageBackground";
import { Eye, EyeOff } from 'lucide-react';
import { useRouter } from "next/navigation";
import { FormEvent, useState, useTransition } from "react";

type MessageState =
  | {
      type: "error" | "success";
      text: string;
    }
  | null;

export default function LoginPage() {
  const router = useRouter();
  const [form, setForm] = useState({ username: "", password: "" });
  const [showPassword, setShowPassword] = useState(false);
  const [message, setMessage] = useState<MessageState>(null);
  const [isPending, startTransition] = useTransition();

  const handleSubmit = (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault();
    setMessage(null);

    const username = form.username.trim();
    const password = form.password;

    if (!username || !password) {
      setMessage({ type: "error", text: "请填写账号与密码" });
      return;
    }

    startTransition(async () => {
      try {
        const result = await AuthControllerService.login({
          username,
          password,
        });

        if (result?.code && result.code !== 200) {
          setMessage({
            type: "error",
            text: result.msg ?? "登录失败，请稍后重试",
          });
          return;
        }

        setMessage({
          type: "success",
          text: result?.msg ?? "登录成功，正在跳转...",
        });

        setTimeout(() => {
          router.replace("/");
        }, 800);
      } catch (error) {
        const fallback =
          error instanceof Error
            ? error.message
            : "请求失败，请检查网络后重试";
        setMessage({ type: "error", text: fallback });
      }
    });
  };

  return (
    <PageBackground>
      <div className="flex min-h-screen flex-col justify-center px-6 py-12 sm:px-8 lg:px-12">
        <div className="mx-auto flex w-full max-w-5xl flex-col gap-10 lg:flex-row">
          <Card className="flex flex-1 flex-col justify-center p-10">
            <p className="mb-4 text-sm uppercase tracking-[0.4em] text-slate-400">
              BeEyes Ops Center
            </p>
            <h1 className="text-4xl font-semibold leading-snug text-white lg:text-5xl">
              轻松掌握服务器实时健康状态
            </h1>
            <p className="mt-6 text-lg text-slate-300">
              登录后即可查看实时监控、资源趋势以及告警信息，随时随地掌控各项关键指标。
            </p>
            <div className="mt-10 grid gap-4 text-sm text-slate-300">
              <div className="flex items-center gap-3">
                <span className="h-2 w-2 rounded-full bg-emerald-400" />
                多维度指标可视化与异常捕获
              </div>
              <div className="flex items-center gap-3">
                <span className="h-2 w-2 rounded-full bg-sky-400" />
                支持多机房、多集群联合分析
              </div>
              <div className="flex items-center gap-3">
                <span className="h-2 w-2 rounded-full bg-amber-400" />
                自定义告警策略，及时通知
              </div>
            </div>
          </Card>

          <Card variant="light" className="flex flex-1 flex-col justify-center p-8">
            <header className="mb-8">
              <p className="text-sm font-semibold uppercase tracking-[0.4em] text-indigo-500">
                登录
              </p>
              <h2 className="mt-2 text-3xl font-semibold text-slate-900">
                欢迎回来
              </h2>
              <p className="mt-2 text-sm text-slate-500">
                使用已有账号进入 BeEyes 运维监控平台
              </p>
            </header>

            <form className="space-y-6" onSubmit={handleSubmit}>
              <Input
                id="username"
                name="username"
                label="账号"
                type="text"
                autoComplete="username"
                placeholder="请输入账号或邮箱"
                value={form.username}
                onChange={(event) =>
                  setForm((prev) => ({
                    ...prev,
                    username: event.target.value,
                  }))
                }
              />

              <Input
                id="password"
                name="password"
                label="密码"
                type={showPassword ? "text" : "password"}
                autoComplete="current-password"
                placeholder="请输入密码"
                value={form.password}
                onChange={(event) =>
                  setForm((prev) => ({
                    ...prev,
                    password: event.target.value,
                  }))
                }
                rightElement={
                  <button
                    type="button"
                    onClick={() => setShowPassword((prev) => !prev)}
                    className="text-sm font-medium text-indigo-500 transition hover:text-indigo-600"
                  >
                    {showPassword ? <Eye size={18} /> : <EyeOff size={18} />}
                  </button>
                }
              />

              {message && (
                <p
                  className={`rounded-2xl px-4 py-3 text-sm ${
                    message.type === "error"
                      ? "bg-rose-50 text-rose-500"
                      : "bg-emerald-50 text-emerald-600"
                  }`}
                >
                  {message.text}
                </p>
              )}

              <Button
                type="submit"
                isLoading={isPending}
                className="w-full"
              >
                {isPending ? "登录中..." : "立即登录"}
              </Button>
            </form>

            <p className="mt-8 text-center text-sm text-slate-500">
              还没有账号？请联系管理员获取访问权限
            </p>
          </Card>
        </div>
      </div>
    </PageBackground>
  );
}
