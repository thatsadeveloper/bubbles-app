# AI Handoff Guide

This repository is structured to allow another AI assistant to continue coding safely and effectively.

## Quick context
- Backend: Spring Boot 3 (Java 17), PostgreSQL + Flyway, Spring Security (JWT), Spring AI (OpenAI), WebFlux (TTS), Mail (verification)
- Frontend: Next.js 15 (App Router), TypeScript, Tailwind, NextAuth (Google), custom JWT via backend exchange
- Infra: docker-compose for Postgres/Redis

## Start here
- Read `README.md` (setup, env, Google OAuth) and `CHANGELOG.md` (latest changes)
- Explore code under `backend/src/main/java/com/bubbles` and `frontend/src`
- Run locally:
  - `docker compose up -d`
  - `cd backend && ./gradlew bootRun`
  - `cd frontend && npm run dev`

## Coding guidelines
- Follow the code style in `README.md` and existing patterns
- Keep functions readable and small; prefer early returns
- Avoid introducing TypeScript `any`; when needed, annotate explicitly and consider shared types
- For backend: prefer constructor injection and Spring Data JPA
- For migrations: use Flyway `db/migration/V*.sql`
- Do not commit secrets; `.gitignore` covers build outputs and env files

## Where help is needed next
- Frontend
  - Route guards and auth context: surface JWT status, logout, and protect pages
  - Attempt history per exercise and bubble; richer feedback UI and toasts
  - Profile/settings page: TTS voice/rate, language defaults
- Backend
  - Harden generation: JSON schema validation, retries/backoff, moderation
  - Rate limiting for TTS/generation; simple admin dashboard endpoints
  - Object storage (S3) for audio with signed URLs
  - Expand SRS: surface due selection logic and record richer metadata
- Testing & CI/CD
  - Unit and integration tests for services and controllers
  - E2E tests (Playwright/Cypress) for main flows
  - Containerization and GitHub Actions workflows

## PR checklist (AI)
- Update `CHANGELOG.md` for significant changes
- Update `README.md` when adding env vars or changing flows
- Keep builds green: `npm run build` and `./gradlew build -x test` locally
- Add Flyway migrations for DB schema changes

## Contacts
- None yet. Leave clear commit messages and doc strings for human reviewers.
