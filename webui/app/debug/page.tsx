"use client";
import TerminalComponent from "@/components/Terminal";
import { Button } from "@heroui/button";
import { useEffect, useState } from "react";

export default function Page() {
  const [isMounted, setIsMounted] = useState(false);
  const [connect, setConnect] = useState(false);

  useEffect(() => {
    setIsMounted(true);
  }, []);

  if (!isMounted) return null;

  return <TerminalComponent id={"1890644362854076416"} rows={35} />;
}
