"use client";
import { useState } from "react";
import Button from "@/components/ui/Button";
import { Card, CardBody, CardHeader } from "@/components/ui/Card";
import { useToast } from "@/components/ui/ToastProvider";
import { authFetch } from "@/lib/api";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

const PRESETS = [
  { topic: "French for a concert", lang: "fr", level: "A2" },
  { topic: "Ordering food in Spanish", lang: "es", level: "A1" },
  { topic: "Business meeting in German", lang: "de", level: "B1" },
  { topic: "Travel in Italian", lang: "it", level: "A2" },
];

export default function NewBubblePage() {
  const toast = useToast();
  const [topic, setTopic] = useState("");
  const [lang, setLang] = useState("fr");
  const [level, setLevel] = useState("A2");
  const [loading, setLoading] = useState(false);

  async function create() {
    setLoading(true);
    try {
      const res = await authFetch(`${API_BASE}/api/bubbles`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ topic, targetLanguage: lang, level }) });
      if (!res.ok) throw new Error();
      const data = await res.json();
      toast.success("Bubble created");
      window.location.href = "/bubbles";
    } catch {
      toast.error("Failed to create bubble");
    } finally {
      setLoading(false);
    }
  }

  function applyPreset(p: { topic: string; lang: string; level: string }) {
    setTopic(p.topic); setLang(p.lang); setLevel(p.level);
  }

  return (
    <main className="mx-auto max-w-3xl p-6 space-y-4">
      <h1 className="text-2xl font-semibold">New Bubble</h1>
      <Card>
        <CardHeader>Choose a preset</CardHeader>
        <CardBody>
          <div className="grid grid-cols-1 sm:grid-cols-2 gap-2">
            {PRESETS.map((p) => (
              <button key={p.topic} className="border rounded p-3 text-left hover:bg-gray-50" onClick={()=>applyPreset(p)}>
                <div className="font-medium">{p.topic}</div>
                <div className="text-xs text-gray-600">{p.lang.toUpperCase()} · {p.level}</div>
              </button>
            ))}
          </div>
        </CardBody>
      </Card>
      <Card>
        <CardHeader>Customize</CardHeader>
        <CardBody>
          <div className="space-y-3">
            <input className="w-full border p-2 rounded" placeholder="Topic" value={topic} onChange={(e)=>setTopic(e.target.value)} />
            <div className="grid grid-cols-2 gap-2">
              <select className="border p-2 rounded" value={lang} onChange={(e)=>setLang(e.target.value)}>
                {['fr','es','de','it','en','pt','ja','zh'].map(l => <option key={l} value={l}>{l.toUpperCase()}</option>)}
              </select>
              <select className="border p-2 rounded" value={level} onChange={(e)=>setLevel(e.target.value)}>
                {['A1','A2','B1','B2','C1'].map(l => <option key={l} value={l}>{l}</option>)}
              </select>
            </div>
            <Button onClick={create} disabled={!topic || loading}>{loading?"Creating...":"Create bubble"}</Button>
          </div>
        </CardBody>
      </Card>
    </main>
  );
}


