## Architecture

### System diagram
```mermaid
graph TD
  U[User (Web/PWA)] -->|HTTPS| FE[Next.js React Frontend]
  FE -->|REST/SSE/WebSocket| BE[Spring Boot API]
  BE -->|SQL| DB[(PostgreSQL)]
  BE -->|Cache/Jobs| RE[(Redis)]
  BE -->|Content Gen| OAI[OpenAI API]
  BE -->|Audio Storage| S3[(Object Storage)]

  subgraph Content Flow
    BE -->|async jobs| GEN[Generation Worker]
    GEN --> OAI
    GEN --> DB
    GEN --> S3
  end
```

### Components
- **Frontend**: Next.js, TypeScript, Tailwind/Chakra, SWR/React Query, Web Audio, PWA.
- **Backend**: Spring Boot (Web, Security, Data JPA), Flyway, OpenAI client, SSE/WebSocket.
- **Data**: PostgreSQL for persistence, Redis for cache/queues.
- **Infra**: Dockerized services, object storage (S3 compatible), CI/CD.

### Runtime flows
- **Bubble creation**: REST `POST /api/bubbles` → enqueue job → generation worker calls OpenAI → persist sentences/vocab/dialogue/exercises → emit status via SSE.
- **Content delivery**: REST `GET /api/bubbles/:id/...` + signed URLs for audio.
- **Practice**: REST for exercises; SSE for real-time feedback if desired.

### Scaling and resilience
- Stateless web tier behind a load balancer.
- Job workers scale horizontally.
- Idempotency keys for generation requests; compensating deletes on failure.


