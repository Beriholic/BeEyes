import React from "react";

interface ModalProps {
  isOpen: boolean;
  onClose: () => void;
  title: string;
  children: React.ReactNode;
}

export function Modal({ isOpen, onClose, title, children }: ModalProps) {
  if (!isOpen) return null;

  return (
    <div className="fixed inset-0 z-50 flex items-center justify-center px-4 py-10">
      <div
        className="absolute inset-0 bg-black/70 backdrop-blur-sm"
        onClick={onClose}
      />
      <div className="relative z-10 w-full max-w-3xl rounded-3xl border border-white/10 bg-slate-950 p-8 text-white shadow-2xl">
        <div className="flex items-start justify-between gap-4">
          <div>
            <p className="text-xs uppercase tracking-[0.4em] text-slate-400">
              详情
            </p>
            <h3 className="mt-2 text-2xl font-semibold">{title}</h3>
          </div>
          <button
            type="button"
            onClick={onClose}
            className="rounded-full border border-white/10 px-4 py-2 text-sm text-slate-300 transition hover:border-indigo-400 hover:text-white"
          >
            关闭
          </button>
        </div>

        <div className="mt-6 max-h-[60vh] overflow-y-auto pr-2">
          {children}
        </div>
      </div>
    </div>
  );
}
