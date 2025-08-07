"use client";
import clsx from "clsx";

export function Tabs({ tabs, active, onChange }: { tabs: { id: string; label: string }[]; active: string; onChange: (id: string) => void }) {
  return (
    <div className="inline-flex rounded-md border bg-white overflow-hidden">
      {tabs.map(t => (
        <button key={t.id} onClick={() => onChange(t.id)} className={clsx("px-3 py-1.5 text-sm border-r last:border-r-0", active===t.id?"bg-blue-600 text-white":"hover:bg-gray-50")}>{t.label}</button>
      ))}
    </div>
  );
}


