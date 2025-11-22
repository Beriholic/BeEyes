import React from "react";

interface InputProps extends React.InputHTMLAttributes<HTMLInputElement> {
  label?: string;
  error?: string;
  rightElement?: React.ReactNode;
}

export function Input({
  label,
  error,
  rightElement,
  className = "",
  id,
  ...props
}: InputProps) {
  return (
    <div className={className}>
      {label && (
        <label
          htmlFor={id}
          className="mb-2 block text-sm font-medium text-slate-600"
        >
          {label}
        </label>
      )}
      <div className="relative rounded-2xl border border-slate-200 bg-white px-4 py-3 focus-within:border-indigo-500 focus-within:ring-2 focus-within:ring-indigo-100">
        <div className="flex items-center">
          <input
            id={id}
            className="w-full border-none bg-transparent text-base text-slate-900 placeholder:text-slate-400 focus:outline-none"
            {...props}
          />
          {rightElement && <div className="ml-2">{rightElement}</div>}
        </div>
      </div>
      {error && <p className="mt-1 text-sm text-rose-500">{error}</p>}
    </div>
  );
}
