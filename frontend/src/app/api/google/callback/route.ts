import { NextRequest, NextResponse } from "next/server";

const API_BASE = process.env.NEXT_PUBLIC_API_BASE_URL || "http://localhost:8080";

export async function GET(req: NextRequest) {
  const email = req.nextUrl.searchParams.get("email");
  if (!email) return NextResponse.redirect(new URL("/auth?error=missing_email", req.url));
  const exchangeSecret = process.env.GOOGLE_EXCHANGE_SECRET || "";
  const res = await fetch(`${API_BASE}/api/auth/google-exchange`, {
    method: "POST",
    headers: { "Content-Type": "application/json" },
    body: JSON.stringify({ email, level: "A2", targetLanguage: "fr", exchangeSecret })
  });
  if (!res.ok) return NextResponse.redirect(new URL("/auth?error=exchange_failed", req.url));
  const data = await res.json();

  const wantsJson = req.nextUrl.searchParams.get("mode") === "json" || (req.headers.get("accept") || "").includes("application/json");
  if (wantsJson) {
    return NextResponse.json({ token: data.token });
  }

  const resp = NextResponse.redirect(new URL("/", req.url));
  resp.cookies.set("jwt", data.token, { httpOnly: false });
  return resp;
}


