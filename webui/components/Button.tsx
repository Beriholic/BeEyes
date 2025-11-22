import React from "react";

interface ButtonProps extends React.ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: "primary" | "outline" | "ghost";
  isLoading?: boolean;
}

export function Button({
  children,
  className = "",
  variant = "primary",
  isLoading = false,
  disabled,
  ...props
}: ButtonProps) {
  const baseStyles =
    "inline-flex items-center justify-center rounded-2xl font-medium transition focus:outline-none disabled:cursor-not-allowed disabled:opacity-50";
  
  const variants = {
    primary:
      "bg-indigo-600 text-white hover:bg-indigo-500 focus-visible:ring-2 focus-visible:ring-indigo-400 disabled:bg-indigo-400",
    outline:
      "border border-white/10 bg-white/5 text-slate-300 hover:border-indigo-400 hover:bg-indigo-500/10 hover:text-white",
    ghost: "text-indigo-500 hover:text-indigo-600",
  };

  const sizes = "px-4 py-2 text-sm sm:py-3 sm:text-base";

  return (
    <button
      className={`${baseStyles} ${variants[variant]} ${sizes} ${className}`}
      disabled={isLoading || disabled}
      {...props}
    >
      {isLoading && (
        <svg
          className="mr-2 h-4 w-4 animate-spin"
          fill="none"
          stroke="currentColor"
          viewBox="0 0 24 24"
        >
          <path
            strokeLinecap="round"
            strokeLinejoin="round"
            strokeWidth={2}
            d="M4 4v5h.582m15.356 2A8.001 8.001 0 004.582 9m0 0H9m11 11v-5h-.581m0 0a8.003 8.003 0 01-15.357-2m15.357 2H15"
          />
        </svg>
      )}
      {children}
    </button>
  );
}
