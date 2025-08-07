## Security and Privacy

### Authentication and authorization
- Spring Security with JWT access/refresh tokens.
- Role-based access (USER, ADMIN).

### Input validation and sanitization
- Validate topic/level/lang; limit lengths; reject HTML/JS.
- Escape outputs; use parameterized SQL with JPA.

### Rate limiting and abuse prevention
- Per-IP and per-user limits for bubble generation and TTS.
- Backpressure on queues; quotas by plan.

### Secrets and configuration
- Store secrets in env or secret manager; never in repo.
- Rotate keys; least-privilege IAM for storage and DB.

### Privacy
- Minimize PII; allow account deletion and data export.
- Retain only necessary prompts/responses; redact PII in logs.


