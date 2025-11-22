import React from "react";

interface PageBackgroundProps {
  children?: React.ReactNode;
  className?: string;
}

export function PageBackground({ children, className = "" }: PageBackgroundProps) {
  return (
    <div className={`min-h-screen bg-slate-950 text-white ${className}`}>
      <div className="fixed inset-0 z-0 pointer-events-none">
         <div className="absolute inset-0 bg-linear-to-br from-slate-900 via-slate-950 to-black" />
         <div className="absolute inset-0 bg-[radial-gradient(circle_at_top,rgba(59,130,246,0.18),transparent_55%)]" />
      </div>
      <div className="relative z-10">
        {children}
      </div>
    </div>
  );
}
