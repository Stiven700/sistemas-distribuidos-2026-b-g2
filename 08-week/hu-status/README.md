<!-- Your weekly grade is read AUTOMATICALLY from this file:
   08-week/hu-status/README.md  (inside YOUR fork). English. -->
# Weekly Status - Week 08

FULL_NAME: Daniel Stiven Poveda
GITHUB_USER: Stiven700
TEAM: Pms_Property
SPRINT_GOAL: Make the per-service data model visible — publish Entity-Relationship
diagrams for `identity-service` and `booking-service` that match `06-data/models.md`
and respect Database per Service (ADR-002).

## 1. User stories worked this week

| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| DOC-UML-001 | Publish ER diagram for `identity-service` (`identity_db`) — `08-uml/diagrams/source/erd-identity-service.md` | done | branch `docs/add-erd-identity-booking` — commit `a3453ad` `docs(uml): add ER diagrams for identity and booking services` |
| DOC-UML-002 | Publish ER diagram for `booking-service` (`booking_db`) — `08-uml/diagrams/source/erd-booking-service.md` | done | branch `docs/add-erd-identity-booking` — commit `a3453ad` |

## 2. My individual contribution

- Drew the **`identity_db` ER diagram** (Mermaid `erDiagram`): a single `users` table.
  Documented that it is the only place in the whole system holding personal data
  (`email`, `full_name`) — every other service keeps just the bare `userId`, per the
  Shared Kernel relationship in `02-domain/domain-map.md`.
- Flagged the open point that `role` holds one value per user (`GUEST`/`HOST`/`ADMIN`),
  linking it to `06-data/normalization-assessment.md` (P-01) instead of deciding it
  silently.
- Drew the **`booking_db` ER diagram**: `reservations` and `outbox_events` (Outbox
  pattern, ADR-005), related through `aggregate_id`.
- Added the **`processed_events`** table to the booking diagram — it does not exist in
  `models.md` yet, but `05-architecture/inter-service-communication.md` §2 requires every
  consumer of `PagoAprobado`/`PagoRechazado` to deduplicate by `event_id`. Linked it to
  the planned migration `V003__create_processed_events_table.sql`.
- Made cross-service references explicit: `property_id` and `guest_id` carry **no foreign
  key**, since they point to other services' databases (ADR-002).
- Each diagram includes notes and a *Correlations* section pointing to `models.md`,
  `data-dictionary.md`, `migration-strategy.md` and `security-policy.md`, so it stays
  traceable to the written model.

## 3. Blockers and risks

- `processed_events` is designed but not yet part of `06-data/models.md`; until its
  migration is written, the diagram and the model differ on purpose (marked in the
  diagram notes).
- `reservations.total_amount` is still `numeric` because the diagram mirrors
  `06-data/models.md` as it stands today; it will be updated together with `models.md`.

## 4. Plan for next week

- Add `processed_events` to `06-data/models.md` so the booking diagram and the model
  match.
- Keep `08-uml/diagram-index.md` in sync with the new ER diagrams.

## 5. Compliance self-check

- [x] Conventional Commits - type(scope): summary — *(`docs(uml): add ER diagrams for identity and booking services`)*
- [x] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) — *(not applicable to the `-docs` repo: single `main` branch; work goes through a `docs/` branch + PR to `main`)*
- [x] Testable acceptance criteria — *(every table/column in the diagrams can be checked against `06-data/models.md`)*
- [ ] Tests added/updated (unit / integration) — *(no service code yet)*
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) — *(no code yet)*
- [x] No secrets; config via environment variables — *(diagrams only; passwords appear only as `password_hash`, no real data)*

## 6. Evidence links

- `08-uml/diagrams/source/erd-identity-service.md`
- `08-uml/diagrams/source/erd-booking-service.md`
- Repository: https://github.com/code-corhuila/property-docs
