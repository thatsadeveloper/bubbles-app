"use client";
import { useEffect, useState } from "react";
import { authFetch } from "@/lib/api";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

type VocabDue = { id: number; lemma: string; translation?: string };
type SentenceDue = { id: number; text: string };

export default function ProgressPage() {
  const [vocab, setVocab] = useState<VocabDue[]>([]);
  const [sentences, setSentences] = useState<SentenceDue[]>([]);
  const [msg, setMsg] = useState("");

  useEffect(() => {
    authFetch(`${API_BASE}/api/progress/due`).then(r => {
      if (!r.ok) throw new Error("Unauthorized");
      return r.json();
    }).then(d => { setVocab(d.vocab || []); setSentences(d.sentences || []); }).catch(()=>setMsg("Login required"));
  }, []);

  async function review(type: "vocab"|"sentence", itemId: number, correct: boolean) {
    const res = await authFetch(`${API_BASE}/api/progress/review`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ type, itemId, correct }) });
    if (res.ok) setMsg("Recorded review");
  }

  return (
    <main className="mx-auto max-w-3xl p-6 grid grid-cols-1 md:grid-cols-2 gap-6">
      <div className="border rounded p-3">
        <h2 className="font-semibold mb-2">Vocab due</h2>
        <ul className="space-y-2">
          {vocab.map(v => (
            <li key={v.id} className="border rounded p-2 flex items-center justify-between">
              <div><span className="font-medium">{v.lemma}</span> <span className="text-sm text-gray-600">{v.translation}</span></div>
              <div className="flex gap-2">
                <button className="px-2 py-1 rounded border" onClick={()=>review("vocab", v.id, false)}>Again</button>
                <button className="px-2 py-1 rounded bg-emerald-600 text-white" onClick={()=>review("vocab", v.id, true)}>Good</button>
              </div>
            </li>
          ))}
        </ul>
      </div>
      <div className="border rounded p-3">
        <h2 className="font-semibold mb-2">Sentences due</h2>
        <ul className="space-y-2">
          {sentences.map(s => (
            <li key={s.id} className="border rounded p-2 flex items-center justify-between">
              <div>{s.text}</div>
              <div className="flex gap-2">
                <button className="px-2 py-1 rounded border" onClick={()=>review("sentence", s.id, false)}>Again</button>
                <button className="px-2 py-1 rounded bg-emerald-600 text-white" onClick={()=>review("sentence", s.id, true)}>Good</button>
              </div>
            </li>
          ))}
        </ul>
      </div>
      {msg && <div className="md:col-span-2 text-sm">{msg}</div>}
    </main>
  );
}


