import { Navbar } from "@/components/Navbar";
import { ScrollShadow } from "@heroui/react";

export default function SettingLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="flex flex-col">
      <Navbar />
      <ScrollShadow>
        <main className="pt-16 px-6 flex-grow">{children}</main>
      </ScrollShadow>
    </div>
  );
}
