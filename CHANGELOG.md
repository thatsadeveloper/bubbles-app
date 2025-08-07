## Changelog

### 2025-08-07

#### Planning and docs
- Added comprehensive project documentation in `docs/` and a top-level `README.md` describing Bubbles, including requirements, architecture, data model, API, OpenAI usage, exercises, UX, voice, security, deployment, testing, and roadmap.

#### Repo setup
- Added `.gitignore` for Node and Java/Gradle artifacts.
- Added `docker-compose.yml` with PostgreSQL 16 and Redis 7.
- Added `.env.example` with backend and frontend environment variables.

#### Backend (Spring Boot 3, Java 17)
- Initialized Gradle project in `backend/` with dependencies: Web, Validation, Data JPA, Security, Actuator, Redis, Flyway, PostgreSQL, SpringDoc, Spring AI (OpenAI starter), JWT (jjwt), and WebFlux planned for TTS.
- Configuration in `application.yml` with DB, JPA, Flyway, Redis, SpringDoc, JWT, and Spring AI defaults.
- Flyway migration `V1__init.sql` creating tables: `users`, `bubbles`, `sentences`, `vocabulary`, `conversation_turns`, `exercises`, `exercise_attempts`. Used portable varchar types instead of PostgreSQL enums.
- Security: `SecurityConfig`, `JwtAuthFilter`, `JwtService`.
- Users: `User` (implements `UserDetails`), `UserRepository`, `UserService`.
- Auth: `AuthController` with `/api/auth/register` and `/api/auth/login` returning JWT.
- Bubbles: `Bubble`, `BubbleRepository`, `BubbleController` with `POST /api/bubbles`, `GET /api/bubbles/{id}`, SSE `GET /api/bubbles/{id}/events`, and `GET /api/bubbles` for listing by user.
- Generation (initial -> expanded):
  - Initial: `GenerationService` stub flipping `RUNNING` → `COMPLETED`.
  - Expanded: Calls Spring AI to generate JSON for sentences (20), vocabulary, and a two-person conversation; persists results. Auto-generates simple cloze exercises from sentences. Cleans prior content on retry. Marks `FAILED` on exceptions.
- Content APIs: `ContentController` with `GET /api/bubbles/{id}/sentences|vocabulary|conversation|exercises`.
- Exercise attempts: `ExerciseAttempt`, `ExerciseAttemptRepository`, `ExerciseController` with `POST /api/exercises/{id}/attempts`.
- CORS: `WebConfig` allows `http://localhost:3000`.
- Admin: `BubbleAdminController` with `POST /api/admin/bubbles/{id}/retry` to re-run generation.

#### Frontend (Next.js 15, TypeScript, Tailwind)
- Scaffolded `frontend/` with Next.js app router and Tailwind.
- Initial page to register demo user, create bubble, and stream status via SSE.
- Expanded UI: Tabs for Sentences, Vocabulary, Conversation, and Exercises; fetches and displays content; runs cloze attempts against backend.
- TS/ESLint fixes with stronger types for exercise JSON.

#### Builds
- Backend: `./gradlew clean build -x test` green.
- Frontend: `npm run build` green.

#### TTS and persistence
- Implemented `TtsController` using OpenAI Audio Speech API via WebClient.
- Added local audio persistence to `AUDIO_STORAGE_DIR` with SHA-256 checksum filenames.
- Added `audio_assets` JPA entity and repository; metadata stored with provider/voice/url/checksum.
- Added `GET /api/tts/assets/{checksum}` to serve cached audio.
- Integrated frontend conversation tab with a Play button per turn.

#### Docs
- Expanded README with setup, env variables, how to run, and TTS persistence details.

#### Auth and OAuth
- Email verification added: verification tokens table, mail sender, verify endpoint.
- Google OAuth: NextAuth in frontend with Google provider; backend exchange endpoint to mint JWTs.
 - Verification UI: `/auth/verify` page with auto-verify, manual code entry, resend (30s cooldown), and success/expired handling.
 - HTML verification emails with deep link via `FRONTEND_BASE_URL`.

#### SRS and exercises
- Added SRS tables (`user_vocab_progress`, `user_sentence_progress`) and basic SM-2 style review logic + due endpoint.
- Added dictation exercises and fuzzy scoring.
 - Added `/progress` page to surface due items and record reviews.
 - Exercise attempts now set `userId` and are filtered by bubble+user in `GET /api/exercises/bubbles/{id}/attempts`.


