## Deployment

### Environments
- **Local**: Docker compose for DB/Redis, local dev servers.
- **Staging**: Preview builds, feature flags, sandbox OpenAI keys.
- **Production**: Multi-AZ DB, autoscaling workers, observability.

### Packaging
- Backend: Spring Boot fat JAR in container.
- Frontend: Static Next.js build served behind CDN.

### Infrastructure
- Postgres (managed if possible), Redis, S3-compatible storage, CDN.
- CI/CD pipelines for build, test, migrate, deploy.

### Observability
- Metrics (HTTP, queue, jobs), logs with correlation IDs, tracing.


