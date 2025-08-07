"use client";
import { useEffect } from "react";
import { useSession } from "next-auth/react";

export default function AuthBootstrap() {
  const { data: session } = useSession();
  useEffect(() => {
    const token = typeof window !== 'undefined' ? localStorage.getItem('token') : null;
    const email = session?.user?.email;
    if (!token && email) {
      const url = `/api/google/callback?email=${encodeURIComponent(email)}&mode=json`;
      fetch(url, { headers: { Accept: "application/json" } })
        .then(r => r.ok ? r.json() : Promise.reject())
        .then(d => { if (d?.token) localStorage.setItem('token', d.token); })
        .catch(() => {});
    }
  }, [session]);
  return null;
}


