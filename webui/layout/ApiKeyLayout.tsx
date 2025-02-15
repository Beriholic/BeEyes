import FadeContent from "@/components/FadeContent";
import { Navbar } from "@/components/navbar";

export default function ApiKeyLayout({
  children,
}: {
  children: React.ReactNode;
}) {
  return (
    <div className="relative flex flex-col h-screen">
      <Navbar />
      <FadeContent
        blur={true}
        duration={500}
        easing="ease-out"
        initialOpacity={0}
      >
        <main
          className="container max-w-7xl pt-16 px-6 flex-grow"
          suppressHydrationWarning
        >
          {children}
        </main>
      </FadeContent>
    </div>
  );
}
