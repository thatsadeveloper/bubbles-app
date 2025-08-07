"use client";
import { useState } from "react";
import { useToast } from "@/components/ui/ToastProvider";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export type JsonValue = string | number | boolean | null | JsonValue[] | { [key: string]: JsonValue };
export type Exercise = { id: number; type: string; prompt: { [key: string]: JsonValue }; solution: { [key: string]: JsonValue } };

export function ExerciseRunner({ exercise, token }: { exercise: Exercise; token: string }) {
  if (exercise.type === "CLOZE") return <Cloze ex={exercise} token={token} />;
  if (exercise.type === "DICTATION") return <Dictation ex={exercise} token={token} />;
  return <div>Unsupported exercise type</div>;
}

function Cloze({ ex, token }: { ex: Exercise; token: string }) {
  const [answer, setAnswer] = useState("");
  const [result, setResult] = useState<string>("");
  const toast = useToast();
  async function submit() {
    try {
      const res = await fetch(`${API_BASE}/api/exercises/${ex.id}/attempts`, { method: "POST", headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }, body: JSON.stringify({ response: { answer }, timeSpentMs: 0 }) });
      if (!res.ok) throw new Error();
      const data = await res.json();
      setResult(data.correct ? "Correct" : "Incorrect");
      toast[data.correct ? 'success' : 'info'](data.correct ? 'Correct!' : 'Try again');
    } catch {
      toast.error('Submission failed');
    }
  }
  return (
    <div className="space-y-2">
      <div>{String(ex.prompt.sentence ?? "")}</div>
      <input className="border p-2 rounded" placeholder="Missing word" value={answer} onChange={(e)=>setAnswer(e.target.value)} />
      <button className="px-3 py-2 rounded bg-emerald-600 text-white" onClick={submit} disabled={!answer}>Check</button>
      {result && <div className="text-sm">{result}</div>}
    </div>
  );
}

function Dictation({ ex, token }: { ex: Exercise; token: string }) {
  const [answer, setAnswer] = useState("");
  const [result, setResult] = useState<string>("");
  const toast = useToast();
  const translation = String(ex.prompt.translation ?? "");
  async function submit() {
    try {
      const res = await fetch(`${API_BASE}/api/exercises/${ex.id}/attempts`, { method: "POST", headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` }, body: JSON.stringify({ response: { answer }, timeSpentMs: 0 }) });
      if (!res.ok) throw new Error();
      const data = await res.json();
      setResult(data.correct ? "Correct" : "Incorrect");
      toast[data.correct ? 'success' : 'info'](data.correct ? 'Correct!' : 'Keep practicing');
    } catch {
      toast.error('Submission failed');
    }
  }
  return (
    <div className="space-y-2">
      <div className="text-sm text-gray-600">{translation}</div>
      <input className="border p-2 rounded w-full" placeholder="Type what you hear" value={answer} onChange={(e)=>setAnswer(e.target.value)} />
      <button className="px-3 py-2 rounded bg-emerald-600 text-white" onClick={submit} disabled={!answer}>Check</button>
      {result && <div className="text-sm">{result}</div>}
    </div>
  );
}


