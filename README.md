# HMCTS Dev Test Backend

Spring Boot backend for managing example tasks. Use with the related frontend — create/update/delete endpoints redirect to the frontend UI.

## Quick start

1. Start Postgres (DB only):

```bash
docker compose up -d
```

2. Run the backend locally:

```bash
./gradlew bootRun
```

No need to set any environment variables, defaults have been defined.
(Note: in a production environment, any sensitive values wouldn't be committed)

## Endpoints

- `GET /tasks` — list tasks (JSON)
- `GET /tasks/{id}` — get task (JSON)
- `POST /tasks/create` — create (form, redirects)
- `POST /tasks/{id}/update` — update (form, redirects)
- `DELETE /tasks/{id}` — delete (redirects)

Redirect responses use HTTP 303 and the `FRONTEND_BASE_URL` (default `https://localhost:3100`).

Note: the frontend uses plain HTML forms to submit create/update/delete requests. For that reason these endpoints accept form-encoded data (`application/x-www-form-urlencoded`) rather than JSON, and they redirect on success back to the frontend UI.

## Environment defaults (in case you have any issues with conflicting environment variables)

- `DB_HOST=localhost`
- `DB_PORT=5432`
- `DB_NAME=tasks`
- `DB_USER=dev`
- `DB_PASSWORD=dev`
- `DB_OPTIONS` (optional; include leading `?`)
- `FRONTEND_BASE_URL=https://localhost:3100`

## Tests

Run the full test suite with:

```bash
`./gradlew build`
```

## Security

This project is intentionally permissive for local development. For production, apply the following controls:

- Protect endpoints with authentication and authorization (for example, Spring Security with OAuth2/OIDC or JWTs) and enforce least privilege.
- Enable CSRF protection for browser form endpoints and validate redirect targets against the configured `FRONTEND_BASE_URL`.
- Use HTTPS everywhere.
- Enforce network-level restrictions (load balancer / API gateway / firewall) rather than relying on CORS for access control.
- Store secrets (DB credentials, API keys) in a secure secrets manager and rotate them regularly.
