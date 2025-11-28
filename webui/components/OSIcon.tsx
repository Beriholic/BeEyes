"use client";

import React from "react";
import {
  SiUbuntu,
  SiDebian,
  SiCentos,
  SiRedhat,
  SiArchlinux,
  SiFedora,
  SiAlmalinux,
  SiRockylinux,
  SiFreebsd,
  SiLinux,
} from "react-icons/si";
import { FaWindows, FaApple } from "react-icons/fa";

interface OSIconProps {
  osName?: string | null;
  size?: number;
}

export function OSIcon({ osName, size = 24 }: OSIconProps) {
  const name = (osName ?? "").toLowerCase();
  let Icon: React.ComponentType<{ size?: number; color?: string }> = SiLinux;
  let color = "#64748b";
  if (name.includes("ubuntu")) {
    Icon = SiUbuntu;
    color = "#E95420";
  } else if (name.includes("windows")) {
    Icon = FaWindows;
    color = "#00A4EF";
  } else if (
    name.includes("mac") ||
    name.includes("darwin") ||
    name.includes("os x") ||
    name.includes("osx")
  ) {
    Icon = FaApple;
    color = "#A3AAAE";
  } else if (name.includes("debian")) {
    Icon = SiDebian;
    color = "#A81D33";
  } else if (name.includes("centos")) {
    Icon = SiCentos;
    color = "#9C27B0";
  } else if (name.includes("redhat") || name.includes("rhel")) {
    Icon = SiRedhat;
    color = "#EE0000";
  } else if (name.includes("arch")) {
    Icon = SiArchlinux;
    color = "#1793D1";
  } else if (name.includes("fedora")) {
    Icon = SiFedora;
    color = "#294172";
  } else if (name.includes("alma")) {
    Icon = SiAlmalinux;
    color = "#262261";
  } else if (name.includes("rocky")) {
    Icon = SiRockylinux;
    color = "#10B981";
  } else if (name.includes("freebsd")) {
    Icon = SiFreebsd;
    color = "#AB2B28";
  }
  return (
    <span className="inline-flex items-center">
      <Icon size={size} color={color} />
    </span>
  );
}
