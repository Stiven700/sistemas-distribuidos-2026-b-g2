<!-- Your weekly grade is read AUTOMATICALLY from this file:
   05-week/hu-status/README.md  (inside YOUR fork). English. -->
# Weekly Status - Week 05

FULL_NAME: Daniel Stiven Poveda
GITHUB_USER: Stiven700
TEAM: Pms_Property
SPRINT_GOAL: Close the remaining documentation gaps for MVP 1 — formalize architecture
decisions as ADRs, complete the UX/UI documentation from the Figma mockup, and define
the project's security and governance rules.

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-ARCH-004 | Write ADR-001 through ADR-005 (architectural style, database per service, circuit breaker, choreographed Saga, outbox pattern) | done | *(pending commit — see note below)* |
| DOC-UX-001 | Fill `12-ux-ui/design-system.md` from the Figma mockup (tokens, components, UX patterns) | done | *(pending commit — see note below)* |
| DOC-UX-002 | Fill `12-ux-ui/navigation-map.md` from the Figma mockup (routes, screen map, user flows) | done | *(pending commit — see note below)* |
| DOC-UX-003 | Write `12-ux-ui/mockup/README.md` with the interactive Figma link and screen-to-route mapping | done | *(pending commit — see note below)* |
| DOC-SEC-001 | Fill `00-governance/security-rules.md` (OWASP Top 10 controls, resolved all placeholder values) | done | *(pending commit — see note below)* |
| DOC-SEC-002 | Fill `00-governance/security-policy.md` (RBAC with real GUEST/HOST/ADMIN roles, JWT policy) | done | *(pending commit — see note below)* |
| DOC-GOV-001 | Fill `agile-conventions.md`, `definition-of-done.md`, `definition-of-ready.md`, `documentation-rules.md`, `git-conventions.md`, `microservices-documentation.md` | done | *(pending commit — see note below)* |

> **Evidence note:** as with the previous two weeks, this documentation has not yet been
> committed to the repository. This is now 3 weeks of accumulated, uncommitted
> documentation — strongly recommend pushing everything as tracked commits/PRs before
> Week 6, since ongoing grading and the DoD both depend on it being in the repo, not
> just delivered as files.

## 2. My individual contribution

- Formalized the 5 architecture decisions that had been described as "adopted" without
  a written ADR: microservices + event-driven style, database per service, circuit
  breaker around the payment gateway, choreographed Saga, and the outbox pattern —
  written in MADR format (context, decision, considered options, consequences), each
  reusing the justification already established in `overview.md`/`pattern-guide.md`
  rather than introducing new reasoning.
- Extracted real design tokens (colors, typography, components) and the full screen/
  route map from the team's Figma mockup and used them to fill `design-system.md` and
  `navigation-map.md`. In the process, found and documented 2 real coherence gaps
  between the mockup and the already-documented architecture: messaging has no assigned
  Bounded Context, and the "Reserva confirmada" screen implies a synchronous
  confirmation that contradicts the asynchronous Saga.
- Wrote the mockup folder's `README.md` with the interactive Figma link, a screen-to-
  route-to-service table, and the open coherence notes, so anyone opening the Figma file
  has the context without digging through the other documents first.
- Rewrote `security-rules.md` and `security-policy.md`: replaced every unresolved
  placeholder (`[N]`, `[7 days / 30 days]`, etc.) with concrete values, replaced the
  generic `SUPER_ADMIN/ADMIN/OPERATOR/VIEWER` RBAC table with this project's real
  `GUEST/HOST/ADMIN` roles, fixed a contradiction where one document allowed HS256 JWTs
  and the other forbade them in production (standardized on RS256 only), and corrected
  `auth-service` references to the real `identity-service`.
- Adapted the remaining governance documents (`agile-conventions.md`,
  `definition-of-done.md`, `definition-of-ready.md`, `documentation-rules.md`,
  `git-conventions.md`, `microservices-documentation.md`) to the team's actual
  4-person, rotating/informal role structure, and fixed a direct contradiction in
  `documentation-rules.md` that mandated English-only docs despite the project's own
  established Spanish Ubiquitous Language convention.
- Produced the infographic `week5_sesion1` (Containerization with Docker).

## 3. Blockers and risks

- **Highest priority risk:** 3 consecutive weeks of documentation work exist only as
  delivered files, with no git history behind them. This needs to be committed as
  properly scoped PRs (per `00-governance/git-conventions.md`) before it can count as
  DoD-compliant work or show up as real evidence in these weekly reports.
- No formal backlog with real HU IDs still exists — recommend setting this up in
  GitHub Projects (per `agile-conventions.md`) before Week 6, since it is now blocking
  every week's evidence table from having a real link.
- No code has been written yet — Week 6 is the natural point to start implementation
  given the architecture, domain, and governance groundwork is now complete.

## 4. Plan for next week

- Commit all Week 2–5 documentation to the repository as scoped PRs, following
  `git-conventions.md`.
- Set up the GitHub Projects backlog with real HU IDs, replacing the temporary `DOC-`
  scheme used in these reports.
- Begin implementation of `booking-service`'s walking skeleton (per the structure
  already defined in `hexagonal-architecture.md`).

## 5. Compliance self-check

> Documentation-only week, pre-implementation — the checklist below is code-oriented
> and largely not yet applicable. Left unchecked rather than falsely marked.

- [ ] Conventional Commits - type(scope): summary — *(commit format is defined in `git-conventions.md`; not yet applied since nothing has been committed)*
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria — *(the ADRs' consequences and the security rules' concrete thresholds — e.g. 5 failed attempts, 20 records per page — are directly testable once implemented)*
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) — *(no code yet to check this against)*
- [x] No secrets; config via environment variables — *(security-policy.md explicitly forbids secrets in source/logs/committed .env and mandates environment variables or Vault; this is the documented rule, correctly reflecting how the team intends to work — no code exists yet to violate or confirm it)*

## 6. Evidence links

- Infographic: `week5_sesion1` — Containerization with Docker
- `05-architecture/decisions/records/ADR-001` through `ADR-005`
- `12-ux-ui/design-system.md`, `navigation-map.md`, `mockup/README.md`
- `00-governance/security-rules.md`, `security-policy.md`
- `00-governance/agile-conventions.md`, `definition-of-done.md`, `definition-of-ready.md`, `documentation-rules.md`, `git-conventions.md`, `microservices-documentation.md`
