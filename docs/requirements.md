## Requirements

### Functional requirements
- **Create bubble**: User specifies topic, target language, proficiency (A1–C2). System generates 20–30 sentences, vocabulary, conversation, and exercises.
- **View bubble**: Retrieve all content; show generation status updates (SSE/WebSocket) if in progress.
- **Vocabulary practice**: Flashcards with spaced repetition and pronunciation audio.
- **Sentence practice**: Reading, dictation, and cloze (fill-in-the-blank) exercises.
- **Conversation mode**: Two-person scripted dialogue; stepwise reveal with TTS and text.
- **Progress tracking**: Store attempts, correctness, timings; show spaced repetition due items.
- **User profile**: Level, target languages, preferred voices, accessibility settings.
- **Authentication**: Email/password or OAuth2; session/refresh tokens.
- **Admin**: Moderate content, manage models, retry generations.

### Non-functional requirements
- **Performance**: P95 API < 300 ms for cached reads; generation is async.
- **Scalability**: Horizontal scaling for web/API; job queue for generation.
- **Reliability**: Idempotent generation; retry-safe; at-least-once job semantics.
- **Security**: OWASP Top 10 mitigations, RBAC, rate limiting, input validation.
- **Privacy**: Minimize PII; encrypt secrets; configurable data retention.
- **Accessibility**: WCAG AA; keyboard-first; captions and transcripts.
- **Internationalization**: UI i18n; support for multiple target languages.
- **Cost control**: Caching, content reuse, token/latency budgets, guardrails.

### Assumptions
- OpenAI models provide JSON outputs for structured content.
- TTS is provided by OpenAI or a compatible provider.
- PostgreSQL is the primary data store. Redis optional for caching/jobs.


