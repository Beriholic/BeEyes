import FadeContent, { FadeContentDefault } from "@/components/FadeContent";
import { Navbar } from "@/components/Navbar";

export default function ApiKeyLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="relative flex flex-col h-screen">
      <Navbar />
      <FadeContentDefault>
        <main
          className="container max-w-7xl pt-16 px-6 flex-grow"
          suppressHydrationWarning
        >
          {children}
        </main>
      </FadeContentDefault>
    </div>
  );
}
