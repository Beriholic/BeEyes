"use client";

import { FadeContentDefault } from "./FadeContent";
import { Navbar } from "./navbar";

export function TerminalLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="relative flex flex-col h-screen">
      <Navbar />
      <FadeContentDefault>
        <div>{children}</div>
      </FadeContentDefault>
    </div>
  );
}
