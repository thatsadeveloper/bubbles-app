"use client";
import { useState } from "react";
import Link from "next/link";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export default function AuthPage() {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [level, setLevel] = useState("A2");
  const [targetLanguage, setTargetLanguage] = useState("fr");
  const [mode, setMode] = useState<"login" | "register">("register");
  const [msg, setMsg] = useState<string>("");

  async function submit() {
    setMsg("");
    const url = mode === "register" ? `${API_BASE}/api/auth/register` : `${API_BASE}/api/auth/login`;
    const body = mode === "register"
      ? { email, password, level, targetLanguage }
      : { email, password };
    const res = await fetch(url, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify(body) });
    if (!res.ok) {
      setMsg("Auth failed");
      return;
    }
    const data = await res.json();
    if (data.token === "VERIFY_EMAIL") {
      window.location.href = "/auth/verify";
      return;
    }
    localStorage.setItem("token", data.token);
    window.location.href = "/";
  }

  return (
    <main className="mx-auto max-w-md p-6 space-y-4">
      <h1 className="text-2xl font-semibold">{mode === "register" ? "Register" : "Login"}</h1>
      <div className="space-y-2">
        <input className="w-full border p-2 rounded" placeholder="Email" value={email} onChange={(e)=>setEmail(e.target.value)} />
        <input className="w-full border p-2 rounded" type="password" placeholder="Password" value={password} onChange={(e)=>setPassword(e.target.value)} />
        {mode === "register" && (
          <div className="flex gap-2">
            <input className="border p-2 rounded w-24" placeholder="Level" value={level} onChange={(e)=>setLevel(e.target.value)} />
            <input className="border p-2 rounded w-24" placeholder="Lang" value={targetLanguage} onChange={(e)=>setTargetLanguage(e.target.value)} />
          </div>
        )}
        <button className="px-3 py-2 rounded bg-blue-600 text-white" onClick={submit}>{mode === "register" ? "Create account" : "Login"}</button>
        {mode === "register" && (
          <p className="text-xs text-gray-600">After registering, check your email for a verification link or enter your code on the <Link className="underline" href="/auth/verify">verification page</Link>.</p>
        )}
        <GoogleLogin />
        <button className="px-3 py-2 rounded border" onClick={()=>setMode(mode === "register" ? "login" : "register")}>{mode === "register" ? "Have an account? Login" : "Need an account? Register"}</button>
        {msg && <div className="text-red-600 text-sm">{msg}</div>}
      </div>
    </main>
  );
}

function GoogleLogin() {
  const [error, setError] = useState<string>("");
  async function start() {
    setError("");
    try {
      window.location.href = "/api/auth/signin/google";
    } catch (e) {
      setError("Google login failed");
    }
  }
  return (
    <div className="space-y-2">
      <button className="px-3 py-2 rounded border w-full" onClick={start}>Continue with Google</button>
      {error && <div className="text-red-600 text-sm">{error}</div>}
    </div>
  );
}


