import { Navbar } from "@/components/navbar";

export function HomeLayout({ children }: { children: React.ReactNode }) {
  return (
    <div className="relative flex flex-col h-screen">
      <Navbar />
      <main className="pt-16 px-6 flex-grow">{children}</main>
    </div>
  );
}
