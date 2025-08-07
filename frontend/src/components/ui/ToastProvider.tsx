"use client";
import { createContext, useCallback, useContext, useMemo, useState } from "react";
import clsx from "clsx";

type ToastType = "success" | "error" | "info";
type ToastItem = { id: number; message: string; type: ToastType };

type ToastAPI = {
  success: (message: string) => void;
  error: (message: string) => void;
  info: (message: string) => void;
};

const ToastContext = createContext<ToastAPI | null>(null);

export function useToast() {
  const ctx = useContext(ToastContext);
  if (!ctx) throw new Error("useToast must be used within ToastProvider");
  return ctx;
}

export default function ToastProvider({ children }: { children: React.ReactNode }) {
  const [toasts, setToasts] = useState<ToastItem[]>([]);
  const remove = useCallback((id: number) => setToasts(t => t.filter(x => x.id !== id)), []);

  const push = useCallback((message: string, type: ToastType) => {
    const id = Date.now() + Math.floor(Math.random() * 1000);
    setToasts(t => [...t, { id, message, type }]);
    setTimeout(() => remove(id), 3000);
  }, [remove]);

  const api = useMemo<ToastAPI>(() => ({
    success: (m) => push(m, "success"),
    error: (m) => push(m, "error"),
    info: (m) => push(m, "info"),
  }), [push]);

  return (
    <ToastContext.Provider value={api}>
      {children}
      <div className="fixed top-4 right-4 z-50 space-y-2">
        {toasts.map(t => (
          <div key={t.id} className={clsx(
            "min-w-[220px] max-w-sm rounded-md px-4 py-2 shadow-lg text-white",
            t.type === "success" && "bg-emerald-600",
            t.type === "error" && "bg-rose-600",
            t.type === "info" && "bg-slate-800",
          )}>{t.message}</div>
        ))}
      </div>
    </ToastContext.Provider>
  );
}


