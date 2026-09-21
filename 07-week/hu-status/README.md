<!-- Your weekly grade is read AUTOMATICALLY from this file:
   07-week/hu-status/README.md  (inside YOUR fork). English. -->
# Weekly Status - Week 07

FULL_NAME: Daniel Stiven Poveda
GITHUB_USER: Stiven700
TEAM: Pms_Property
SPRINT_GOAL: Formalize how services actually talk to each other — justify sync vs.
async per interaction, publish the first real API contract, and define versioning/
compatibility rules for both REST and event contracts.

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-ARCH-005 | Write ADR-006 — REST for synchronous communication (vs. gRPC) | done | *(see repo — Week 2-6 backlog already committed, this week's PR to follow the same flow)* |
| DOC-ARCH-006 | Write `05-architecture/inter-service-communication.md` — sync/async matrix for all 8 real interactions + idempotent consumer pattern | done | *(see repo)* |
| DOC-API-001 | Write `07-api/contracts/versioning-and-compatibility.md` — REST and event schema compatibility rules | done | *(see repo)* |
| DOC-API-002 | Publish `07-api/contracts/openapi/booking-service.yaml` — first real OpenAPI contract (`POST /reservas`, `GET /reservas/{id}`, `POST /reservas/{id}/cancelar`) | done | *(see repo)* |
| DOC-DOMAIN-004 | Add `schemaVersion` field to every event in `02-domain/domain-events.md` | done | *(see repo)* |
| MVP1-API-001, MVP2-API-001, MVP2-API-002 | Slice 3 contract/communication stories (OpenAPI publication, Pact contract test, idempotent-consumer requirement) | done | *(see repo)* |

## 2. My individual contribution

- Realized this week's topic (sync vs. async, REST vs. gRPC, delivery semantics,
  versioned contracts) mapped onto three things the project had left **implicit**:
  which style (REST) was chosen for internal synchronous calls, what actually
  guarantees correctness under at-least-once delivery, and how contracts are supposed
  to evolve without breaking a consumer.
- Wrote **ADR-006**, formalizing REST (not gRPC) for every synchronous interaction —
  reusing the reasoning already implicit in `overview.md`'s service catalog and
  Principle P1, rather than introducing a new unjustified preference.
- Built the full **sync/async interaction matrix** (`inter-service-communication.md`):
  listed all 8 real interactions in the system with a justification for each — this had
  never been written down explicitly, even though the individual decisions (Saga = async,
  client-facing = sync) already existed piecemeal across ADR-001/ADR-004/overview.md.
- Documented the actual **idempotent consumer pattern** the project already committed to
  in principle (`processed_event_ids`, `01-context/glossary.md`) but never showed in
  code — a check→apply→mark example against `payment-service` consuming `ReservaCreada`.
- Published the project's **first real OpenAPI contract**
  (`booking-service.yaml`), covering the create/read/cancel Reserva endpoints — until
  now every microservices-documentation reference to `07-api/contracts/openapi/` pointed
  at a path with nothing in it.
- Wrote the **versioning and compatibility rules** for both REST and event schemas, and
  applied the event-schema rule immediately by adding a `schemaVersion` field to every
  event already in `domain-events.md` (defaulted to `1`, purely additive — no existing
  payload's meaning changed).
- Produced two infographics: `week7_sesion1` (Inter-service communication — REST, gRPC
  and messaging) and `week7_sesion2` (Planning — versioned contracts and contract
  testing).

## 3. Blockers and risks

- Consumer-driven contract testing (Pact) is documented as a rule and sliced as a story
  (`MVP2-API-001`), but not yet runnable — there is no CI pipeline or service code yet
  for it to run against. This is expected at this stage, not a gap in this week's work.
- `booking-service.yaml` currently documents only 3 endpoints (create/read/cancel) —
  the rest of the services' contracts (`catalog-service`, `payment-service`,
  `identity-service`, `notification-service`) still need their own OpenAPI files;
  planned incrementally as each service's implementation starts.

## 4. Plan for next week

- Start `booking-service`'s walking skeleton implementation, now that both its
  hexagonal structure (`hexagonal-architecture.md`) and its REST contract
  (`booking-service.yaml`) exist.
- Publish OpenAPI contracts for `catalog-service` and `identity-service`.
- Wire the `docker-compose.yml` drafted in Week 6 against real (even if minimal)
  service code, to validate the health-check gating actually works.

## 5. Compliance self-check

- [x] Conventional Commits - type(scope): summary — *(Weeks 2-6 backlog is now committed per the corrected process; this week's work follows the same convention, e.g. `docs(api): publish booking-service OpenAPI contract`)*
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) — *(not applicable — this repo uses a single `main` branch, per the Week 6 correction; work still goes through a `docs/`-scoped branch + PR to `main`, per `00-governance/git-conventions.md`)*
- [x] Testable acceptance criteria — *(the 3 contract/communication stories sliced this week have explicit Given/When/Then criteria; `booking-service.yaml` itself is testable by OpenAPI validation)*
- [ ] Tests added/updated (unit / integration) — *(no service code exists yet to test against)*
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) — *(no code yet to check)*
- [x] No secrets; config via environment variables — *(the OpenAPI contract's `bearerAuth` scheme references JWTs by convention only, no key material in the file; consistent with `security-policy.md`)*

## 6. Evidence links

- Infographic: `week7_sesion1` — Inter-service communication: REST, gRPC and messaging
- Infographic: `week7_sesion2` — Planning: versioned contracts and contract testing
- `05-architecture/decisions/records/ADR-006-rest-for-synchronous-communication.md`
- `05-architecture/inter-service-communication.md`
- `07-api/contracts/versioning-and-compatibility.md`
- `07-api/contracts/openapi/booking-service.yaml`
- `02-domain/domain-events.md` — `schemaVersion` field added
