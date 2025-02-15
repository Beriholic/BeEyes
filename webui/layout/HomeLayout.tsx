import Dock from "@/components/Dock";
import { Navbar } from "@/components/navbar";
import dockConfig from "@/config/dock";
import { ScrollShadow } from "@heroui/react";
export function HomeLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="relative flex flex-col h-screen">
      <Navbar />
      <ScrollShadow>
        <main className="pt-16 px-6 flex-grow">{children}</main>
      </ScrollShadow>
      <Dock
        items={dockConfig()}
        panelHeight={68}
        baseItemSize={50}
        magnification={70}
      />
    </div>
  );
}
