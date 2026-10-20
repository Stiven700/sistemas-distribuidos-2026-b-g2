<!-- HU-STATUS TEMPLATE - do NOT remove the <!-- ... --> markers or the table headers.
     Your weekly grade is read AUTOMATICALLY from this file:
       11-week/hu-status/README.md  (inside YOUR fork). English. -->

# Weekly Status - Week 11

<!-- CONFIG-START - must match your profile repo (username/username) CONFIG -->
- FULL_NAME: Daniel Stiven Poveda Cante
- GITHUB_USER: Stiven700
- TEAM: Pms_Property
- SPRINT_GOAL: Make the Saga `reservar` observable end to end (trace map, log contract, instrumentation, metrics, SLOs and incident response) and start corte 3 replacing the mocks with the real services under the same contracts.
<!-- CONFIG-END -->

## 1. User stories worked this week
| HU ID | Title | Status (todo/doing/done) | Evidence (PR or commit URL) |
|---|---|---|---|
| W11-CP1 | Trace map of the Saga `reservar` across gateway, workflow, booking, catalog, payment and RabbitMQ | done | https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/blob/main/11-week/workshop/01-journey.md |
| W11-CP2 | Structured JSON log contract with Saga domain fields and a never-log list | done | https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/blob/main/11-week/workshop/02-log-contract.md |
| W11-CP3 | Trace-context filter, JSON logger, propagation to booking-service and `/metrics` in `property-workflow` | done | https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/tree/main/11-week/workshop/03-instrumentation |
| W11-CP4 | Golden signals and metric catalog per service | done | https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/blob/main/11-week/workshop/04-metrics.md |
| W11-CP6 | Three SLOs of the Saga `reservar` (availability, latency, integrity) with error budget and policy | done | https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/blob/main/11-week/workshop/05-slo.md |
| W11-CP7 | Multi-window burn-rate alerts in Prometheus and alert policy | done | https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/blob/main/11-week/workshop/06-alerts.yml |
| W11-CP8 | Runbook of the fast-burn availability alert | done | https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/blob/main/11-week/workshop/07-runbook.md |
| W11-CP9 | Incident drill on the `cobrar` hop and blameless postmortem with three user stories | done | https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/blob/main/11-week/workshop/08-incident.md |

## 2. My individual contribution
- Mapped the critical journey (Saga `reservar`) hop by hop and how `traceparent` crosses the outbox into RabbitMQ.
- Defined the log contract shared by the six services, with `saga_id`, `saga_step`, `reserva_id`, `pago_id` and `monto_cents`.
- Wrote and tested a minimal instrumentation module for `property-workflow` in Java: the test proves the `trace_id` received is the one sent to `booking-service`.
- Wrote the metric catalog with the four golden signals, label cardinality and histogram buckets.
- Defined three SLOs for the Saga with their error budget, the burn-rate alerts that protect them and the runbook of the page.
- Ran an incident drill on the `cobrar` hop (slow `payment-service`) and wrote a blameless postmortem; its action items are ready as user stories for the next sprint.

## 3. Blockers and risks
- The backend services are not built yet; the instrumentation is a standalone module and the SLO numbers are estimates until there is real traffic.
- `booking.outbox_event` needs a `traceparent` column so the trace survives the async boundary; it is not in `06-data/models.md` yet.

## 4. Plan for next week
- Corte 3: start the real services behind the same OpenAPI contracts, beginning with the JSON logger and the trace filter in `property-workflow`.
- Add the `traceparent` column to `booking.outbox_event` and the three postmortem stories to the backlog.

## 5. Compliance self-check
- [x] Conventional Commits - `type(scope): summary` — *(e.g. `feat(w11): add trace-context middleware and structured logger`)*
- [ ] Per-environment HU branch + PR to that environment (hu-xxx-dev -> develop, ...) — *(individual workshop in my fork)*
- [x] Testable acceptance criteria — *(each checkpoint has its "done when" check)*
- [x] Tests added/updated (unit / integration) — *(`TracePropagationTest`)*
- [ ] DDD / hexagonal boundaries respected (domain has no I/O) — *(no domain code this week)*
- [x] No secrets; config via environment variables — *(`SERVICE_NAME`, `APP_ENV`, `BOOKING_URL`; tokens are in the never-log list)*

## 6. Evidence links
- Workshop: https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/tree/main/11-week/workshop
- Commits: https://github.com/Stiven700/sistemas-distribuidos-2026-b-g2/commits/main
- property-docs: https://github.com/code-corhuila/property-docs
