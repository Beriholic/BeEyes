import { FadeContentDefault } from "@/components/FadeContent";
import { Navbar } from "@/components/Navbar";

export function TerminalLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="relative flex flex-col h-screen">
      <Navbar />
      <FadeContentDefault>{children}</FadeContentDefault>
    </div>
  );
}
