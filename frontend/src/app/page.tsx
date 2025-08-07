"use client";
import { useEffect, useState } from "react";
import { authFetch } from "@/lib/api";
import Button from "@/components/ui/Button";
import { Card, CardBody, CardHeader } from "@/components/ui/Card";
import { Tabs } from "@/components/ui/Tabs";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

type Bubble = { id: number; status: string; topic: string } | null;
type Sentence = { id: number; text: string; translation?: string };
type Vocab = { id: number; lemma: string; translation?: string; pos?: string };
type Turn = { id: number; speaker: string; text: string; translation?: string };
type JsonValue = string | number | boolean | null | JsonValue[] | { [key: string]: JsonValue };
type Exercise = { id: number; type: string; prompt: { [key: string]: JsonValue }; solution: { [key: string]: JsonValue } };

export default function Home() {
  const [topic, setTopic] = useState("");
  const [targetLanguage, setTargetLanguage] = useState("fr");
  const [level, setLevel] = useState("A2");
  const [token, setToken] = useState("");
  const [bubble, setBubble] = useState<Bubble>(null);
  const [status, setStatus] = useState<string>("");
  const [tab, setTab] = useState<"sent"|"vocab"|"conv"|"ex">("sent");

  const [sentences, setSentences] = useState<Sentence[]>([]);
  const [vocab, setVocab] = useState<Vocab[]>([]);
  const [conversation, setConversation] = useState<Turn[]>([]);
  const [exercises, setExercises] = useState<Exercise[]>([]);

  async function registerDemo() {
    const existing = typeof window !== 'undefined' ? localStorage.getItem('token') : null;
    if (existing) { setToken(existing); return; }
    const res = await fetch(`${API_BASE}/api/auth/register`, {
      method: "POST",
      headers: { "Content-Type": "application/json" },
      body: JSON.stringify({ email: `demo+${Date.now()}@example.com`, password: "demo-pass", level: "A2", targetLanguage: "fr" })
    });
    if (res.ok) {
      const data = await res.json();
      setToken(data.token);
      localStorage.setItem('token', data.token);
    }
  }

  async function createBubble() {
    const res = await authFetch(`${API_BASE}/api/bubbles`, {
      method: "POST",
      headers: {
        "Content-Type": "application/json"
      },
      body: JSON.stringify({ topic, targetLanguage, level }),
    });
    if (res.ok) {
      const data = await res.json();
      setBubble(data);
      const evt = new EventSource(`${API_BASE}/api/bubbles/${data.id}/events`);
      evt.onmessage = (e) => setStatus(e.data);
      evt.addEventListener("status", (e: MessageEvent) => setStatus(e.data));
    }
  }

  useEffect(() => {
    if (!bubble) return;
    fetch(`${API_BASE}/api/bubbles/${bubble.id}/sentences`).then(r => r.json()).then(setSentences).catch(()=>{});
    fetch(`${API_BASE}/api/bubbles/${bubble.id}/vocabulary`).then(r => r.json()).then(setVocab).catch(()=>{});
    fetch(`${API_BASE}/api/bubbles/${bubble.id}/conversation`).then(r => r.json()).then(setConversation).catch(()=>{});
    fetch(`${API_BASE}/api/bubbles/${bubble.id}/exercises`).then(r => r.json()).then(setExercises).catch(()=>{});
  }, [bubble]);

  async function attempt(ex: Exercise, userAnswer: string) {
    const payload = { response: { answer: userAnswer }, timeSpentMs: 0 };
    const res = await fetch(`${API_BASE}/api/exercises/${ex.id}/attempts`, {
      method: "POST",
      headers: { "Content-Type": "application/json", Authorization: `Bearer ${token}` },
      body: JSON.stringify(payload)
    });
    if (res.ok) {
      const data = await res.json();
      alert(`Correct: ${data.correct ? "Yes" : "No"}`);
    }
  }

  return (
    <main className="mx-auto max-w-3xl p-6">
      <h1 className="text-3xl font-semibold mb-6">Bubbles</h1>
      <div className="space-y-4 mb-6">
        <div className="flex gap-2 items-center">
          <Button onClick={registerDemo}>Register demo user</Button>
          <a className="underline" href="/auth">Login/Register</a>
          {token && <>
            <span className="text-green-700 text-sm">Authenticated</span>
            <Button variant="outline" onClick={() => { localStorage.removeItem('token'); setToken(''); }}>Logout</Button>
          </>}
        </div>
        <Card>
          <CardHeader>Create a new bubble</CardHeader>
          <CardBody>
            <div className="flex flex-col sm:flex-row gap-2">
              <input className="flex-1 border p-2 rounded" placeholder="Topic (e.g., French for a concert)" value={topic} onChange={(e)=>setTopic(e.target.value)} />
              <input className="border p-2 rounded w-28" placeholder="Lang" value={targetLanguage} onChange={(e)=>setTargetLanguage(e.target.value)} />
              <input className="border p-2 rounded w-28" placeholder="Level" value={level} onChange={(e)=>setLevel(e.target.value)} />
              <Button onClick={createBubble} disabled={!token || !topic}>Create</Button>
            </div>
            <div className="text-sm mt-2">
              Prefer a guided form? <a className="underline" href="/bubbles/new">Open bubble creator</a>
            </div>
          </CardBody>
        </Card>
        {bubble && (
          <Card>
            <CardHeader>
              <div className="flex items-center justify-between">
                <div>
                  <div className="font-medium">Topic: {bubble.topic}</div>
                  <div className="text-sm text-gray-600">Status: {status || bubble.status}</div>
                </div>
                {/* eslint-disable-next-line @typescript-eslint/no-explicit-any */}
                <Tabs tabs={[{id:"sent",label:"Sentences"},{id:"vocab",label:"Vocabulary"},{id:"conv",label:"Conversation"},{id:"ex",label:"Exercises"}]} active={tab} onChange={(t)=>setTab(t as unknown as typeof tab)} />
              </div>
            </CardHeader>
            <CardBody>
              {tab === "sent" && (
                <ul className="space-y-2">
                  {sentences.map((s) => (
                    <li key={s.id} className="border rounded p-2"><div>{s.text}</div><div className="text-sm text-gray-600">{s.translation}</div></li>
                  ))}
                </ul>
              )}
              {tab === "vocab" && (
                <ul className="grid grid-cols-1 sm:grid-cols-2 gap-2">
                  {vocab.map((v) => (
                    <li key={v.id} className="border rounded p-2"><div className="font-medium">{v.lemma}</div><div className="text-sm text-gray-600">{v.translation} {v.pos?`(${v.pos})`:''}</div></li>
                  ))}
                </ul>
              )}
              {tab === "conv" && (
                <ul className="space-y-2">
                  {conversation.map((t) => (
                    <li key={t.id} className="border rounded p-2">
                      <div className="flex items-center justify-between">
                        <div><span className="font-semibold mr-2">{t.speaker}:</span>{t.text}</div>
                        <TtsButton text={t.text} bubbleId={bubble?.id ?? 0} itemType="CONVERSATION_TURN" itemId={t.id} />
                      </div>
                      <div className="text-sm text-gray-600">{t.translation}</div>
                    </li>
                  ))}
                </ul>
              )}
              {tab === "ex" && (
                <ul className="space-y-3">
                  {exercises.map((ex) => (
                    <li key={ex.id} className="border rounded p-3 space-y-2">
                      <div className="text-sm text-gray-600">{ex.type}</div>
                      {ex.type === "CLOZE" ? (
                        <ClozeExercise ex={ex} onAttempt={(ans)=>attempt(ex, ans)} />
                      ) : (
                        <div>Unsupported exercise type</div>
                      )}
                    </li>
                  ))}
                </ul>
              )}
            </CardBody>
          </Card>
        )}
      </div>
    </main>
  );
}

function ClozeExercise({ ex, onAttempt }: { ex: Exercise; onAttempt: (answer: string) => void }) {
  const [answer, setAnswer] = useState("");
  const sentence = String(ex.prompt?.sentence ?? "");
  const translation = String(ex.prompt?.translation ?? "");
  return (
    <div className="space-y-2">
      <div>{sentence}</div>
      {translation && <div className="text-sm text-gray-600">{translation}</div>}
      <div className="flex gap-2">
        <input className="border p-2 rounded" placeholder="Missing word" value={answer} onChange={(e)=>setAnswer(e.target.value)} />
        <button className="px-3 py-2 rounded bg-emerald-600 text-white" onClick={()=>onAttempt(answer)} disabled={!answer}>Check</button>
      </div>
    </div>
  );
}

function TtsButton({ text, bubbleId, itemType, itemId }: { text: string; bubbleId: number; itemType: string; itemId: number }) {
  const [loading, setLoading] = useState(false);
  async function play() {
    try {
      setLoading(true);
      const res = await fetch(`${API_BASE}/api/tts`, {
        method: "POST",
        headers: { "Content-Type": "application/json" },
        body: JSON.stringify({ text, format: "mp3", bubbleId, itemType, itemId })
      });
      const blob = await res.blob();
      const url = URL.createObjectURL(blob);
      const audio = new Audio(url);
      audio.onended = () => URL.revokeObjectURL(url);
      await audio.play();
    } finally {
      setLoading(false);
    }
  }
  return (
    <button className="px-2 py-1 text-sm rounded border" onClick={play} disabled={loading}>{loading?"...":"Play"}</button>
  );
}
