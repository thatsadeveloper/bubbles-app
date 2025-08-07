import { ReactNode } from "react";
import clsx from "clsx";

export function Card({ children, className }: { children: ReactNode; className?: string }) {
  return <div className={clsx("border rounded-lg bg-white shadow-sm", className)}>{children}</div>;
}

export function CardHeader({ children, className }: { children: ReactNode; className?: string }) {
  return <div className={clsx("px-4 py-3 border-b font-medium", className)}>{children}</div>;
}

export function CardBody({ children, className }: { children: ReactNode; className?: string }) {
  return <div className={clsx("p-4", className)}>{children}</div>;
}


