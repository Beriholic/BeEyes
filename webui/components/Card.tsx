import React from "react";

interface CardProps extends React.HTMLAttributes<HTMLElement> {
  children: React.ReactNode;
  variant?: "glass" | "solid" | "light";
}

export function Card({ children, className = "", variant = "glass", ...props }: CardProps) {
  const variants = {
    glass: "border border-white/5 bg-slate-950/60 backdrop-blur shadow-2xl",
    solid: "border border-white/10 bg-white/5 backdrop-blur-xl",
    light: "border border-white/10 bg-white/90 text-slate-900 shadow-2xl backdrop-blur",
  };

  return (
    <section
      className={`rounded-3xl ${variants[variant]} ${className}`}
      {...props}
    >
      {children}
    </section>
  );
}
