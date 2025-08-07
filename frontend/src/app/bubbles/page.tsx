"use client";
import { useEffect, useState } from "react";
import { authFetch } from "@/lib/api";
import { ExerciseRunner, Exercise as RunnerExercise } from "@/app/exercise/runner";
import { Card, CardBody, CardHeader } from "@/components/ui/Card";
import { Tabs } from "@/components/ui/Tabs";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

type Bubble = { id: number; status: string; topic: string; level: string; targetLanguage: string };
type Sentence = { id: number; text: string; translation?: string };
type Vocab = { id: number; lemma: string; translation?: string; pos?: string };
type Turn = { id: number; speaker: string; text: string; translation?: string };
type JsonValue = string | number | boolean | null | JsonValue[] | { [key: string]: JsonValue };
type Exercise = RunnerExercise;

export default function BubblesPage() {
  const [bubbles, setBubbles] = useState<Bubble[]>([]);
  const [selected, setSelected] = useState<Bubble | null>(null);
  const [tab, setTab] = useState<"sent"|"vocab"|"conv"|"ex">("sent");
  const [sentences, setSentences] = useState<Sentence[]>([]);
  const [vocab, setVocab] = useState<Vocab[]>([]);
  const [conversation, setConversation] = useState<Turn[]>([]);
  const [exercises, setExercises] = useState<Exercise[]>([]);

  useEffect(() => {
    authFetch(`${API_BASE}/api/bubbles`).then(r => r.json()).then(setBubbles).catch(()=>{});
  }, []);

  useEffect(() => {
    if (!selected) return;
    fetch(`${API_BASE}/api/bubbles/${selected.id}/sentences`).then(r => r.json()).then(setSentences).catch(()=>{});
    fetch(`${API_BASE}/api/bubbles/${selected.id}/vocabulary`).then(r => r.json()).then(setVocab).catch(()=>{});
    fetch(`${API_BASE}/api/bubbles/${selected.id}/conversation`).then(r => r.json()).then(setConversation).catch(()=>{});
    fetch(`${API_BASE}/api/bubbles/${selected.id}/exercises`).then(r => r.json()).then(setExercises).catch(()=>{});
  }, [selected]);

  return (
    <main className="mx-auto max-w-5xl p-6 grid grid-cols-1 md:grid-cols-3 gap-6">
      <Card className="md:col-span-1">
        <CardHeader>Bubbles</CardHeader>
        <CardBody>
        <ul className="space-y-2">
          {bubbles.map((b) => (
            <li key={b.id} className={`border rounded p-2 cursor-pointer ${selected?.id===b.id? 'bg-blue-50':''}`} onClick={()=>setSelected(b)}>
              <div className="font-medium">{b.topic}</div>
              <div className="text-xs text-gray-600">{b.targetLanguage} {b.level} · {b.status}</div>
            </li>
          ))}
        </ul>
        </CardBody>
      </Card>
      <Card className="md:col-span-2">
        <CardBody>
        {!selected ? (
          <div className="text-gray-600">Select a bubble to view details.</div>
        ) : (
          <div>
            <div className="flex items-center justify-between">
              <h2 className="font-semibold">{selected.topic}</h2>
              {/* eslint-disable-next-line @typescript-eslint/no-explicit-any */}
              <Tabs tabs={[{id:"sent",label:"Sentences"},{id:"vocab",label:"Vocabulary"},{id:"conv",label:"Conversation"},{id:"ex",label:"Exercises"}]} active={tab} onChange={(t)=>setTab(t as unknown as typeof tab)} />
            </div>
            <div className="mt-4">
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
                    <li key={t.id} className="border rounded p-2"><div><span className="font-semibold mr-2">{t.speaker}:</span>{t.text}</div><div className="text-sm text-gray-600">{t.translation}</div></li>
                  ))}
                </ul>
              )}
              {tab === "ex" && (
                <ul className="space-y-3">
                  {exercises.map((ex: Exercise) => (
                    <li key={ex.id} className="border rounded p-3 space-y-2">
                      <div className="text-sm text-gray-600">{ex.type}</div>
                      <ExerciseRunner exercise={ex} token={typeof window !== 'undefined' ? (localStorage.getItem('token') || '') : ''} />
                    </li>
                  ))}
                </ul>
              )}
            </div>
          </div>
        )}
        </CardBody>
      </Card>
    </main>
  );
}


