<!-- Your weekly grade is read AUTOMATICALLY from this file:
   09-week/hu-status/README.md  (inside YOUR fork). English. -->
# Weekly Status - Week 09

FULL_NAME: Daniel Stiven Poveda
GITHUB_USER: Stiven700
TEAM: Pms_Property
SPRINT_GOAL: Bring the documentation repository in line with the official course norm
and its Annex J — first the API contract layer (guidelines, gap register, endpoint
fiches), then the architecture decisions the norm forces the team to record as ADRs
(single database per engine, message broker, reservation expiry in the worker).

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-API-003 | Rewrite `07-api/guidelines.md` as the single source of API rules (D-C1..D-C13) and create `07-api/gaps.md` as one consolidated `GAP-NNN` register | done | https://github.com/code-corhuila/property-docs/commit/8441160b5e84f3c1bbf0f24bbe576f6b2aad1e8e (branch `docs/api-contract-gaps`) |
| DOC-API-004 | Close fiche gaps: refresh-token policy (GAP-004), catalog date-range semantics (GAP-005) and `07-api/authentication.md` JWT flow (GAP-007) | done | https://github.com/code-corhuila/property-docs/commit/57d17506723ca5a35e46c08bdb3696bd5612b703 (branch `docs/close-remaining-fiche-gaps`) |
| DOC-ARCH-008 | ADR-008 — single database instance per engine, one schema per domain (supersedes ADR-002), including the Annex J naming exception | done | https://github.com/code-corhuila/property-docs/commit/76b61dd7ade350d999d0b37aa42e8ca1b8a8f71c and https://github.com/code-corhuila/property-docs/commit/ba29c4635b725e88d70482f4851f3cd8b87a0155 (branch `docs/adr-single-database`) |
| DOC-ARCH-009 | ADR-013 (RabbitMQ in its own repository, no Redis) and ADR-014 (reservation expiry runs in `property-worker`) | done | https://github.com/code-corhuila/property-docs/commit/8f116415480c742e5c0c50815146a531f5716571 (branch `docs/adr-broker-and-worker`) |
| DOC-ARCH-010 | ADR register in `05-architecture/decisions/README.md` and the norm's "dominant criterion" and "accepted cost" elements in the ADR format | done | https://github.com/code-corhuila/property-docs/commit/2d4dfb27b5480d15d90065ab4e4a035c17d62ccd (branch `docs/adr-index-and-format`) |
| DOC-CTX-001 | Align `01-context` (scope, overview, glossary) with ADR-008 to ADR-014 | done | https://github.com/code-corhuila/property-docs/commit/b6432e80ff47e000e4eb7ccb5cbae1e0ffe8090b (branch `docs/context-align-decisions`) |
| DOC-API-005 | OpenAPI lint / contract-test CI command (GAP-006) | todo | Deferred until service code exists |

## 2. My individual contribution

- **API contract layer.** Rewrote `07-api/guidelines.md` as the single binding source
  for cross-cutting API rules (13 numbered decisions, D-C1..D-C13), replacing three
  documents that contradicted each other and the norm: one error envelope with only 6
  codes, mandatory `Idempotency-Key` on creation `POST`s, money as integer cents, flat
  `/api/v1` resource paths and mandatory pagination. Consolidated every open item into
  `07-api/gaps.md` with a unique `GAP-NNN` sequence (the old lists reused `H-01..H-07`
  for unrelated gaps).
- **Closed three gaps** in a follow-up PR: the refresh-token persistence/revocation
  policy, how catalog's `fechaInicio`/`fechaFin` search interacts with availability, and
  the missing `07-api/authentication.md` narrating the end-to-end JWT flow.
- **ADR-008 — single database per engine.** Annex J of the norm replaces "database per
  service" with one PostgreSQL instance and one MongoDB instance per environment, one
  schema per domain. Recorded that decision, superseding ADR-002 while keeping what it
  protected (each Bounded Context is the only owner of its data), and closed two points
  ADR-002 had left open: Catalog's engine (MongoDB) and Notification's store
  (PostgreSQL). A second commit states explicitly that the `property-infra-postgres` /
  `property-infra-mongo` repository names are an exception authorized by Annex J, not a
  deviation from the naming rule.
- **ADR-013 — messaging infrastructure.** Fixed RabbitMQ as the broker in its own
  repository (`property-infra-rabbitmq`) and removed Redis from the design. Until now
  `scope.md` said RabbitMQ was decided but no ADR recorded it, and other documents still
  showed "Kafka / RabbitMQ" as open.
- **ADR-014 — reservation expiry in the worker.** Moved the 15-minute expiry of a
  `PENDIENTE` `Reserva` out of `booking-service` into a scheduled job of
  `property-worker` (`expire-stale-reservations`), which calls Booking's API instead of
  reading its database, as the norm requires for scheduled work.
- **ADR register and format.** Added the ADR register table to
  `05-architecture/decisions/README.md` (status, superseded/amended links) and brought
  the ADR format in line with the five elements the norm requires (context, options,
  dominant criterion, accepted cost, consequences).
- **Context alignment.** Updated `01-context` (scope, overview and glossary) so it no
  longer contradicts ADR-008 to ADR-014.

## 3. Blockers and risks

- The API rules written at the start of the week are already partly outdated by the
  ADRs accepted at the end of it: the orchestrated Saga changes how idempotency,
  Payment's endpoints and the Saga's transport are described in `guidelines.md`
  (D-C7, D-C12, D-C13). `07-api` needs a second pass.
- GAP-006 (automated OpenAPI lint / contract tests in CI) stays open on purpose — there
  is no service code or pipeline yet to run it against.
- The repositories the norm requires (`property-infra-postgres`, `property-infra-mongo`,
  `property-infra-rabbitmq`, `property-worker`, `property-workflow`) are created by the
  teacher on request; code cannot start until they exist.

## 4. Plan for next week

- Align `07-api` (guidelines, gaps, OpenAPI contracts) with the accepted ADRs.
- Support the team's data-model PRs in `06-data` so the models match ADR-008.
- Request the infrastructure repositories from the teacher through issues.

## 5. Compliance self-check

- [x] Conventional Commits - type(scope): summary — *(e.g. `docs(architecture): add adr-008 single database per engine`)*
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) — *(not applicable to the `-docs` repo: single `main` branch; every change went through its own `docs/` branch + PR to `main`)*
- [x] Testable acceptance criteria — *(every open gap has an explicit exit condition; each ADR lists its consequences and what it supersedes or amends)*
- [ ] Tests added/updated (unit / integration) — *(no service code yet)*
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) — *(no code yet)*
- [x] No secrets; config via environment variables — *(docs only; no credentials or key material committed)*

## 6. Evidence links

- `07-api/guidelines.md`, `07-api/gaps.md`, `07-api/authentication.md`
- `05-architecture/decisions/records/ADR-008-single-database-per-engine.md`
- `05-architecture/decisions/records/ADR-013-messaging-infrastructure.md`
- `05-architecture/decisions/records/ADR-014-reservation-expiry-in-worker.md`
- `05-architecture/decisions/README.md` — ADR register
- `01-context/` — scope, overview, glossary
- Repository: https://github.com/code-corhuila/property-docs
