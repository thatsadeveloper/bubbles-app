## API Design (Spring Boot)

### Auth
- **POST** `/api/auth/register` → { email, password, level?, target_language? }
- **POST** `/api/auth/login` → JWT access + refresh
- **POST** `/api/auth/refresh`

### Bubbles
- **POST** `/api/bubbles` → create and enqueue generation
  - Body: { topic, targetLanguage, level, nativeLanguage? }
  - Returns: 202 + { bubbleId, status }
- **GET** `/api/bubbles/:id` → bubble summary and status
- **GET** `/api/bubbles/:id/sentences`
- **GET** `/api/bubbles/:id/vocabulary`
- **GET** `/api/bubbles/:id/conversation`
- **GET** `/api/bubbles/:id/exercises`
- **DELETE** `/api/bubbles/:id`
- **GET** `/api/bubbles/:id/events` (SSE) → generation progress

### Exercises
- **POST** `/api/exercises/:id/attempts` → create attempt and return scoring
- **GET** `/api/users/me/due` → due items for SRS across vocab/sentences

### Audio/TTS
- **POST** `/api/tts` → on-demand TTS for text; returns signed URL and stores `audio_assets`

### Admin
- **POST** `/api/admin/bubbles/:id/retry`
- **GET** `/api/admin/generations` → filters by status

### Error format
```json
{
  "error": "BadRequest",
  "message": "Level must be one of A1..C2",
  "details": { "field": "level" }
}
```

### Versioning and pagination
- Prefix APIs with `/api` and add semantic versioning when needed (e.g., `/api/v1`).
- Cursor-based pagination for lists: `?cursor=...&limit=...`.


