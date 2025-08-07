"use client";
import { ButtonHTMLAttributes } from "react";
import clsx from "clsx";

type ButtonProps = ButtonHTMLAttributes<HTMLButtonElement> & {
  variant?: "primary" | "secondary" | "outline" | "ghost";
  size?: "sm" | "md" | "lg";
};

export default function Button({ variant = "primary", size = "md", className, ...props }: ButtonProps) {
  const base = "inline-flex items-center justify-center rounded transition-colors focus:outline-none focus:ring-2 focus:ring-offset-2 disabled:opacity-50 disabled:cursor-not-allowed";
  const variants = {
    primary: "bg-blue-600 text-white hover:bg-blue-700 focus:ring-blue-600",
    secondary: "bg-emerald-600 text-white hover:bg-emerald-700 focus:ring-emerald-600",
    outline: "border border-gray-300 text-gray-900 hover:bg-gray-50 focus:ring-gray-400",
    ghost: "text-gray-900 hover:bg-gray-100",
  } as const;
  const sizes = {
    sm: "px-2.5 py-1.5 text-sm",
    md: "px-3.5 py-2",
    lg: "px-4.5 py-2.5 text-lg",
  } as const;

  return (
    <button className={clsx(base, variants[variant], sizes[size], className)} {...props} />
  );
}


