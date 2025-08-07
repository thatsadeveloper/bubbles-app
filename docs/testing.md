## Testing Strategy

### Backend
- Unit tests for services, validators, SRS logic.
- Integration tests with Testcontainers (Postgres/Redis).
- Contract tests for REST APIs; JSON schema validation for OpenAI outputs.

### Frontend
- Component tests (React Testing Library), integration tests (Playwright/Cypress).
- Accessibility tests (axe), visual regression for key screens.

### Load and resilience
- Load tests for read-heavy endpoints and background workers.
- Chaos testing for job retries and partial failures.


