<!-- Your weekly grade is read AUTOMATICALLY from this file:
   04-week/hu-status/README.md  (inside YOUR fork). English. -->
# Weekly Status - Week 04

FULL_NAME: Daniel Stiven Poveda
GITHUB_USER: Stiven700
TEAM: Pms_Property
SPRINT_GOAL: Define the system's architecture (microservices, hexagonal structure,
patterns) and lock down MVP 1 scope, building directly on the corrected domain model.

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-ARCH-001 | Fill `05-architecture/overview.md` — architectural style, C4 diagrams, service catalog, adopted patterns | done | *(pending commit — see note below)* |
| DOC-ARCH-002 | Fill `05-architecture/hexagonal-architecture.md` — adapted to the `Reserva`/`Pago` domain, Java 17 + Spring Boot stack | done | *(pending commit — see note below)* |
| DOC-ARCH-003 | Fill `05-architecture/pattern-guide.md` — design patterns and microservices patterns mapped to the project, adopted-patterns table | done | *(pending commit — see note below)* |
| DOC-SCOPE-001 | Correct `01-context/scope.md` and `01-context/glossary.md` for congruence with the domain model and architecture (missing `EXPIRADA` state, Identity/Notification reconciled into scope, Java/Spring Boot constraint added) | done | *(pending commit — see note below)* |

> **Evidence note:** as with Week 3, this documentation has not yet been committed to
> the repository. Real evidence links will replace this note once pushed.

## 2. My individual contribution

- Wrote the system-level architecture document (`overview.md`): adopted architectural
  style (microservices + event-driven, choreographed Saga) with its justification tied
  directly to the Bounded Contexts, C4 context/container diagrams, the 5-service
  catalog with ports/DBs, architectural principles, and the adopted-patterns table.
- Adapted the Hexagonal Architecture reference document away from its generic `Order`
  example to this project's real domain (`Reserva`, `booking-service`), including
  folder structure, ports/adapters, and TDD examples using our actual invariants.
- Adapted the pattern guide (GoF + microservices patterns) the same way — replaced
  generic examples with `Reserva`/`Pago`/`Propiedad` examples, and filled in the
  "Patterns adopted in this project" table with justifications tied to the Saga.
- Reconciled `scope.md` and `glossary.md` against everything already documented: added
  the missing `EXPIRADA` state, corrected event names to their real Spanish identifiers,
  and moved Identity/Notification from "Out of Scope" into "In Scope" since the
  architecture docs already treat them as full Bounded Contexts.
- Produced two infographics: `week4_sesion1` (Building a service — structure, layers
  and the walking skeleton) and `week4_sesion2` (Planning — the MVP 1 sprint:
  contract-first, estimation, scope).

## 3. Blockers and risks

- Still no formal backlog with real HU IDs.
- Documentation still not committed to the repository — this is now 2 weeks of
  accumulated, uncommitted documentation work; recommend pushing it as a batch of PRs
  before Week 6 to avoid losing traceability.
- No dedicated ADRs existed yet at this point for the architectural decisions already
  being described as "adopted" (API Gateway, Database per Service, Circuit Breaker,
  Saga, Outbox) — flagged as technical debt and resolved the following week (see Week
  05).

## 4. Plan for next week

- Write the 5 pending ADRs formalizing the architecture decisions.
- Fill `12-ux-ui/design-system.md` and `navigation-map.md` from the Figma mockup.
- Fill `00-governance/security-rules.md` and `security-policy.md`.
- Produce the infographic for Week 5's session (Containerization with Docker).

## 5. Compliance self-check

> Documentation-only week, pre-implementation — the checklist below is code-oriented
> and largely not yet applicable. Left unchecked rather than falsely marked.

- [ ] Conventional Commits - type(scope): summary
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria — *(architectural principles and adopted patterns are stated with concrete, checkable criteria — e.g. "no service queries another service's database directly" — even though no code exists yet to verify against)*
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) — *(the Hexagonal Architecture document itself defines this rule for future code; no code exists yet to check)*
- [ ] No secrets; config via environment variables

## 6. Evidence links

- Infographic: `week4_sesion1` — Building a service: structure, layers and the walking skeleton
- Infographic: `week4_sesion2` — Planning: the MVP 1 sprint (contract-first, estimation, scope)
- `05-architecture/overview.md`, `hexagonal-architecture.md`, `pattern-guide.md`
- `01-context/scope.md`, `01-context/glossary.md` — corrected
