<!-- Your weekly grade is read AUTOMATICALLY from this file:
   03-week/hu-status/README.md  (inside YOUR fork). English. -->
# Weekly Status - Week 03

FULL_NAME: Daniel Stiven Poveda
GITHUB_USER: Stiven700
TEAM: Pms_Property
SPRINT_GOAL: Establish a consistent domain model (DDD) for the booking platform and
align it with service design / data ownership before writing any code.

## 1. User stories worked this week

> Still no formal backlog / HU IDs — using a temporary `DOC-<area>-00N` scheme to track
> documentation deliverables until the team defines real user stories.

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-DOMAIN-001 | Review `domain-map.md`, `domain-events.md`, `entities-and-rules.md` for cross-document consistency | done | *(pending commit — see note below)* |
| DOC-DOMAIN-002 | Correct the 7 inconsistencies found (event naming EN/ES, non-existent `DateLocked/Released` event, state naming mismatch, missing `moneda` field, asymmetric notification consumers, incorrect `causationId`, unspecified `UserRegistered` event) | done | *(pending commit — see note below)* |
| DOC-DOMAIN-003 | Write `explicacion-dominio.md` — consolidated domain summary for team presentations | done | *(pending commit — see note below)* |

> **Evidence note:** this documentation was produced and iterated in working sessions,
> not yet pushed to the repository as commits/PRs. Real evidence links will replace this
> note once the files are committed — see `00-governance/git-conventions.md` for the
> branch/commit format to use (`feat/domain-...` or `docs/domain-...`).

## 2. My individual contribution

- Reviewed the three initial domain documents and found they were **not congruent with
  each other**: event names and states were mixed between English and Spanish, one
  documented event (`DateLocked/Released`) did not actually exist in the event catalog,
  the required `moneda` field was missing from all monetary event payloads, notification
  consumers were inconsistent between similar terminal events, `ReservaExpirada`'s
  `causationId` incorrectly pointed to an unrelated event, and the `UserRegistered`
  contract was referenced but never specified.
- Implemented the agreed corrections directly in `domain-map.md` and `domain-events.md`
  so all three domain documents now use the same Ubiquitous Language and event catalog.
- Wrote `explicacion-dominio.md`, a Spanish-language walkthrough of the domain model
  (Bounded Contexts, Context Map, the booking Saga, tactical DDD patterns) to support
  presenting the work to the team/professor.
- Produced two infographics for this week's sessions: `week3_Sesion1` (Domain-Driven
  Design & Hexagonal Architecture) and `week3_sesion2` (Planning — service design, data
  ownership and contracts).

## 3. Blockers and risks

- Still no formal backlog with real HU IDs — tracking documentation work with the
  temporary `DOC-` scheme described above.
- Documentation has not yet been committed to the repository; this is a risk if grading
  or peer review expects it to exist as tracked history, not just as delivered files.

## 4. Plan for next week

- Fill out `05-architecture/overview.md`, `hexagonal-architecture.md`, and
  `pattern-guide.md` using the now-corrected domain model as the foundation.
- Reconcile `01-context/scope.md` and `01-context/glossary.md` against the domain
  documents.
- Produce infographics for Week 4's sessions (service structure/walking skeleton, and
  MVP 1 sprint planning).

## 5. Compliance self-check

> Documentation-only week, pre-implementation — the checklist below is code-oriented
> and largely not yet applicable. Left unchecked rather than falsely marked.

- [ ] Conventional Commits - type(scope): summary
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...)
- [x] Testable acceptance criteria — *(the domain invariants documented, e.g. INV-001..005 in `entities-and-rules.md`, are written to be directly testable, even though no test code exists yet)*
- [ ] Tests added/updated (unit / integration)
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) — *(no code yet to check this against; the domain model itself was reviewed for DDD correctness — see item above)*
- [ ] No secrets; config via environment variables

## 6. Evidence links

- Infographic: `week3_Sesion1` — Domain-Driven Design & Hexagonal Architecture
- Infographic: `week3_sesion2` — Planning: service design, data ownership and contracts
- `02-domain/domain-map.md`, `domain-events.md`, `entities-and-rules.md` — corrected
- `explicacion-dominio.md` — domain summary document
