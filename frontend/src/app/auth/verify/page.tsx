"use client";
import { useEffect, useState } from "react";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export const dynamic = "force-dynamic";

export default function VerifyPage() {
  const [token, setToken] = useState("");
  const [email, setEmail] = useState("");
  const [status, setStatus] = useState<"idle"|"verifying"|"verified"|"expired"|"error">("idle");
  const [message, setMessage] = useState("");
  const [cooldown, setCooldown] = useState<number>(0);

  useEffect(() => {
    try {
      const url = new URL(window.location.href);
      const t = url.searchParams.get("token");
      if (t) { setToken(t); autoVerify(t); }
    } catch {}
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, []);

  async function autoVerify(t: string) {
    setStatus("verifying");
    try {
      const res = await fetch(`${API_BASE}/api/auth/verify?token=${encodeURIComponent(t)}`, { method: "POST" });
      if (res.status === 410) { setStatus("expired"); setMessage("Code expired. Request a new one."); return; }
      if (!res.ok) throw new Error();
      const data = await res.json();
      localStorage.setItem("token", data.token);
      setStatus("verified");
    } catch {
      setStatus("error");
      setMessage("Invalid code. Please try again.");
    }
  }

  async function verifyManual() { if (token) await autoVerify(token); }

  async function resend() {
    setMessage("");
    if (!email) { setMessage("Enter your email to resend."); return; }
    if (cooldown > 0) return;
    const res = await fetch(`${API_BASE}/api/auth/resend`, { method: "POST", headers: { "Content-Type": "application/json" }, body: JSON.stringify({ email }) });
    if (res.ok) {
      setMessage("Verification email sent.");
      setCooldown(30);
      const timer = setInterval(() => {
        setCooldown((c) => {
          if (c <= 1) { clearInterval(timer); return 0; }
          return c - 1;
        });
        return undefined as unknown as number; // satisfy TS narrowing
      }, 1000);
    } else {
      setMessage("Failed to resend. Check email.");
    }
  }

  return (
    <main className="mx-auto max-w-md p-6 space-y-4">
      <h1 className="text-2xl font-semibold">Verify your email</h1>
      <div className="space-y-2">
        <label className="text-sm">Enter verification code</label>
        <input className="w-full border p-2 rounded" placeholder="Paste code" value={token} onChange={(e)=>setToken(e.target.value)} />
        <button className="px-3 py-2 rounded bg-blue-600 text-white" onClick={verifyManual} disabled={status==="verifying"}>Verify</button>
      </div>
      <div className="space-y-2">
        <label className="text-sm">Resend code</label>
        <input className="w-full border p-2 rounded" placeholder="Email" value={email} onChange={(e)=>setEmail(e.target.value)} />
        <button className="px-3 py-2 rounded border disabled:opacity-50" onClick={resend} disabled={cooldown>0}>
          {cooldown>0 ? `Resend in ${cooldown}s` : "Resend email"}
        </button>
      </div>
      {status === "verified" && <div className="text-emerald-700 text-sm">Verified! You can now continue.</div>}
      {status === "expired" && <div className="text-amber-700 text-sm">Code expired. Please resend.</div>}
      {message && <div className="text-red-600 text-sm">{message}</div>}
    </main>
  );
}


