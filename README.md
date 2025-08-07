## Bubbles

### Language learning bubbles tailored to your topic and level

**Bubbles** lets learners request a topic (a "bubble") such as "French for a concert I will visit" and instantly get 20–30 level-appropriate sentences, a complete topic vocabulary set, and interactive practice: vocabulary drills, sentence learning, sentence completion, and a conversational chat mode with voiceover. All generated content is stored for later review.

### Key features
- **Bubble generation**: Topic-focused content matching levels (A1–C2 or beginner/advanced).
- **Vocabulary and sentences**: 20–30 sentences and comprehensive vocab, with translations and audio.
- **Practice modes**: Flashcards with spaced repetition, sentence dictation, cloze (fill-in-the-blank), listening practice, and stepwise chat conversations with TTS.
- **Persistence**: All generated content is saved for reuse and progress tracking.
- **Spring Boot backend**: REST + SSE/WebSocket for real-time status, integrated with OpenAI APIs.

### Tech overview
- **Backend**: Spring Boot (Java 21+), PostgreSQL, Flyway, Spring Security, OpenAI API, optional Redis for jobs/caching.
- **Frontend**: React/Next.js (TypeScript), Tailwind or Chakra/Mantine, Web Audio, PWA.
- **Infra**: Docker, containerized services, optional Kubernetes, Cloud storage for audio (S3-compatible).

### Documentation
See `docs/` for detailed plans:
- `docs/overview.md`
- `docs/requirements.md`
- `docs/architecture.md`
- `docs/data-model.md`
- `docs/api.md`
- `docs/openai.md`
- `docs/exercises.md`
- `docs/ux.md`
- `docs/voice.md`
- `docs/security.md`
- `docs/deployment.md`
- `docs/testing.md`
- `docs/roadmap.md`

### Getting started

#### Prerequisites
- Java 17+
- Node 18+
- Docker (for Postgres + Redis)
- OpenAI API key (`OPENAI_API_KEY`) for generation and TTS

#### 1) Start infrastructure
```bash
docker compose up -d
```

This starts PostgreSQL (localhost:5432, db/user/pass: `bubbles`) and Redis (localhost:6379).

#### 2) Configure environment
Copy `.env.example` to `.env` and set:
- `OPENAI_API_KEY`: required
- Optional: `OPENAI_MODEL`, `OPENAI_TTS_MODEL` (default `tts-1`), `OPENAI_TTS_VOICE` (default `alloy`)
- Optional: `AUDIO_STORAGE_DIR` to change where synthesized audio files are stored
- Email: configure Spring Mail for verification codes
  - `spring.mail.host`, `spring.mail.port`, `spring.mail.username`, `spring.mail.password`
  - `spring.mail.properties.mail.smtp.auth=true`
  - `spring.mail.properties.mail.smtp.starttls.enable=true`
  - `MAIL_FROM=noreply@yourdomain`
- Deep links: `FRONTEND_BASE_URL` (default `http://localhost:3000`) used to build verification links in emails
- Google OAuth (NextAuth + backend exchange)
  - Frontend: `GOOGLE_CLIENT_ID`, `GOOGLE_CLIENT_SECRET`
  - Backend/Frontend: `GOOGLE_EXCHANGE_SECRET` (shared secret for `/api/auth/google-exchange`)
  - Frontend: `NEXT_PUBLIC_API_BASE_URL` (default `http://localhost:8080`)

Backend reads env via `application.yml`. Frontend reads `NEXT_PUBLIC_API_BASE_URL`.

#### 3) Run backend (Spring Boot)
```bash
cd backend
./gradlew bootRun
```
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Health: `http://localhost:8080/actuator/health`

#### 4) Run frontend (Next.js)
```bash
cd frontend
npm run dev
```
Open `http://localhost:3000`.

#### 5) Try it out
1. Register demo user
2. Enter a topic, language (e.g., `fr`) and level (e.g., `A2`)
3. Create a bubble → watch status via SSE
4. Explore tabs: Sentences, Vocabulary, Conversation, Exercises
5. In Conversation, click Play to synthesize TTS

### Configuration
- `SPRING_DATASOURCE_*`: database connection for Postgres
- `SPRING_REDIS_*`: Redis connection
- `JWT_SECRET`: dev secret used for JWT tokens
- `OPENAI_API_KEY`: used by Spring AI chat and direct TTS calls
- `OPENAI_MODEL`: chat model (default `gpt-4o-mini`)
- `OPENAI_TTS_MODEL`: TTS model (default `tts-1`)
- `OPENAI_TTS_VOICE`: voice (default `alloy`)
- `AUDIO_STORAGE_DIR`: server path where TTS audio files are stored (default: system temp dir + `/bubbles-audio`)

### Text-to-Speech (TTS) persistence
- When you click Play in the Conversation tab, the backend synthesizes audio via OpenAI’s Audio Speech API.
- The raw audio bytes are saved to disk under `AUDIO_STORAGE_DIR` using a SHA-256 checksum filename (`<checksum>.mp3` or `.wav`).
- A corresponding `audio_assets` DB row is created with metadata and a stable URL path: `/api/tts/assets/{checksum}`.
- Subsequent requests reuse the saved file via the checksum URL; no need to resynthesize.

### Notes
- The generation pipeline currently produces sentences, vocabulary, a two-person conversation, and cloze exercises. It is ready to be further tuned.
- Admin retry endpoint: `POST /api/admin/bubbles/{id}/retry`.
- Email verification
  - On register, an email with a verification link and code is sent. The link opens `/auth/verify?token=...`.
  - Alternatively, visit `/auth/verify`, paste your code, and click Verify. On success, your JWT is stored locally.
  - Expired tokens return HTTP 410. You can request a new one via `POST /api/auth/resend` with `{ "email": "you@example.com" }`.
- Google login
  - Sign in with Google via NextAuth. The frontend automatically exchanges the Google session for a backend JWT by calling `/api/google/callback?mode=json` and stores it.
  - The backend mints JWTs via `/api/auth/google-exchange`, guarded by `GOOGLE_EXCHANGE_SECRET`.

### Progress (SRS)
- Backend endpoints
  - `GET /api/progress/due` returns due vocabulary and sentences for the authenticated user.
  - `POST /api/progress/review` records a review: `{ type: "vocab"|"sentence", itemId: number, correct: boolean }`.
- Frontend
  - Visit `/progress` to review due items with Again/Good buttons and immediate recording.

### Exercise attempts
- Attempts are associated with the authenticated user when present.
- `GET /api/exercises/bubbles/{bubbleId}/attempts` returns only attempts for the current user within that bubble.



