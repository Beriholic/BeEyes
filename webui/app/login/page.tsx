"use client";
import { useState } from "react";
import { ShaderGradientCanvas, ShaderGradient } from "@shadergradient/react";
import { Input } from "@heroui/input";
import "@/components/LoginInput.module.css";
import { Alert, Button } from "@heroui/react";
import { AnimatePresence, motion } from "motion/react";

import { Logo } from "@/components/icons";
import { api } from "@/api/instance";
import { Token, TokenKey, UserInfoKey } from "@/store/model";
import { useRouter } from "next/navigation";

interface LoginForm {
  username: string;
  password: string;
}

interface AlertContent {
  type:
    | "success"
    | "primary"
    | "default"
    | "secondary"
    | "warning"
    | "danger"
    | undefined;
  description?: string;
  title: string;
}
export default function LoginPage() {
  const router = useRouter();

  const [form, setForm] = useState<LoginForm>({
    username: "",
    password: "",
  });

  const [pops, setPops] = useState<AlertContent | null>(null);
  const showPops = (content: AlertContent) => {
    setPops(content);

    setTimeout(() => {
      setPops(null);
    }, 3000);
  };

  const login = async () => {
    const res = await api.authService.login({
      username: form.username,
      password: form.password,
    });

    if (res.code !== 200) {
      showPops({
        type: "danger",
        title: "登陆失败",
        description: res.message,
      });

      return;
    }

    showPops({
      type: "success",
      title: "登陆成功",
      description: "正在跳转至首页",
    });

    localStorage.setItem(
      TokenKey,
      JSON.stringify({
        value: res.data.token,
        expire: res.data.expire,
      })
    );

    localStorage.setItem(
      UserInfoKey,
      JSON.stringify({
        username: res.data.username,
        role: res.data.role,
      })
    );

    router.push("/");
  };

  return (
    <section className="relative flex h-screen justify-center items-center">
      <ShaderGradientCanvas
        style={{
          position: "absolute",
          top: 0,
        }}
      >
        <ShaderGradient
          control="query"
          urlString="https://www.shadergradient.co/customize?animate=on&axesHelper=off&bgColor1#000000&bgColor2=#000000&brightness=1&cAzimuthAngle=180&cDistance=2.8&cPolarAngle=80&cameraZoom=9.1&color1=#343680&color2=#0700ca&color3=#212121&destination=onCanvas&embedMode=off&envPreset=city&format=gif&fov=45&frameRate=10&gizmoHelper=hide&grain=off&lightType=3d&pixelDensity=1&positionX=0&positionY=0&positionZ=0&range=enabled&rangeEnd=40&rangeStart=0&reflection=0.1&rotationX=50&rotationY=0&rotationZ=-60&shader=defaults&type=waterPlane&uAmplitude=0&uDensity=1.5&uFrequency=0&uSpeed=0.3&uStrength=1.5&uTime=8&wireframe=false"
          zoomOut={false}
        />
      </ShaderGradientCanvas>
      <div className="relative z-10 p-10 size-3/5 rounded-2xl bg-primary-300 flex flex-row justify-between">
        <div className="flex flex-col w-1/2">
          <div>
            <div className="text-4xl font-bold ">登陆</div>
            <div className="text-base">欢迎来到 BeEyes 运维监控系统</div>
          </div>
          <div className="flex flex-col gap-5 my-auto">
            <Input
              color="primary"
              placeholder="用户名"
              value={form.username}
              onChange={(e) => {
                setForm({
                  ...form,
                  username: e.target.value,
                });
              }}
            />
            <Input
              color="primary"
              placeholder="密码"
              value={form.password}
              onChange={(e) => {
                setForm({
                  ...form,
                  password: e.target.value,
                });
              }}
            />
          </div>
          <div>
            <Button className="mt-auto" color="primary" onPress={login}>
              登陆
            </Button>
          </div>
        </div>
        <div className="flex items-center justify-center w-1/2">
          <Logo className="w-24 h-24" />
          <div className="text-2xl mt-2">BeEyes 运维监测平台</div>
        </div>
      </div>
      <AnimatePresence>
        {pops && (
          <motion.div
            animate={{ opacity: 1, y: 0 }}
            className="fixed top-0 right-0 z-50 flex flex-col w-1/4 p-2"
            exit={{ opacity: 0, y: -100 }}
            initial={{ opacity: 0, y: -100 }}
          >
            <Alert
              color={pops.type}
              description={pops.description}
              title={pops.title}
            />
          </motion.div>
        )}
      </AnimatePresence>
    </section>
  );
}
