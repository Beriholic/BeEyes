"use client";

import { useState } from "react";
import { FaBars, FaGauge, FaTerminal } from "react-icons/fa6";
import { ThemeSwitch } from "./theme-switch";
import clsx from "clsx";
import { Logo } from "./icons";
import { useRouter } from "next/navigation";

export default function MachineSidebar({
  curIndex,
  onIndexChange,
}: {
  curIndex: number;
  onIndexChange: (index: number) => void;
}) {
  const router = useRouter();
  const [hide, setHide] = useState(false);

  return (
    <div
      className={clsx(
        "flex flex-col max-h-screen overflow-hidden",
        "transition-all duration-300 ease-in-out",
        "border-r-2 border-r-default-100 rounded-2xl",
        hide ? "w-16" : "w-1/6  p-4"
      )}
    >
      <div className="py-2 text-2xl flex items-center gap-2 mx-auto">
        <Logo onClick={() => setHide(!hide)} />
        <button
          className={clsx(
            "font-semibold overflow-hidden whitespace-nowrap",
            "transition-opacity duration-200",
            hide ? "opacity-0 w-0" : "opacity-100 w-auto"
          )}
          onClick={() => router.push("/")}
        >
          BeEyes
        </button>
      </div>

      <div className="flex flex-col p-4 gap-4 justify-between h-full">
        <div className="flex flex-col gap-4 items-start justify-center">
          <MenuItem
            hide={hide}
            icon={<FaGauge />}
            text="仪表盘"
            isSelected={curIndex === 0}
            onClick={() => {
              onIndexChange(0);
            }}
          />
          <MenuItem
            hide={hide}
            icon={<FaTerminal />}
            text="终端"
            isSelected={curIndex === 1}
            onClick={() => {
              onIndexChange(1);
            }}
          />
        </div>

        <div className="flex flex-col gap-2">
          <MenuItem
            hide={hide}
            icon={<FaBars />}
            text="设置"
            isSelected={curIndex === 2}
            onClick={() => {
              onIndexChange(2);
            }}
          />
          <ThemeSwitch />
        </div>
      </div>
    </div>
  );
}

function MenuItem({
  hide,
  icon,
  text,
  isSelected,
  onClick,
}: {
  hide: boolean;
  icon: any;
  text: string;
  isSelected: boolean;
  onClick: () => void;
}) {
  return (
    <button
      className={clsx(
        "flex items-center gap-2 text-xl transition-colors",
        "transition-colors duration-200",
        isSelected ? "text-primary" : ""
      )}
      onClick={onClick}
    >
      {icon}
      <span
        className={clsx(
          "overflow-hidden whitespace-nowrap",
          "transition-opacity duration-200",
          hide ? "opacity-0 w-0" : "opacity-100 w-auto"
        )}
      >
        {text}
      </span>
    </button>
  );
}
