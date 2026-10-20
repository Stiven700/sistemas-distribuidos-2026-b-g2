# CP9 · Incident drill and blameless postmortem

## The drill

Hop 5 of CP1 (`property-workflow` → `payment-service`, `POST /api/v1/pagos`, step
`cobrar`) is the marked hop. Drill: **`payment-service` answers in 3 seconds for 25 % of
the requests during 40 minutes.**

Assumptions (CP6): it happens on a holiday Saturday at the peak, **60 Sagas per hour**
(average day: about 8 per hour). So the 40 minutes carry **40 Sagas**, and **10** of them
are slow. The workflow's limit for `cobrar` is 4 s (Payment's own limit is shorter, as
the workflow fiches require), so a 3 s answer is slow but not a timeout: no `5xx`.

## Trace of one slow request

`trace_id = 3b9f0c2d7e5a41f6a8c1d2e3f4a5b6c7` — Saga `COMPLETED`, total **3,640 ms**.

- `POST /api/v1/sagas/reservar` — api-gateway — **3,640 ms**
  - `saga reservar` — property-workflow — 3,618 ms
    - `step crear-reserva` → `POST /api/v1/reservas` — booking-service — 402 ms
      - `GET /api/v1/propiedades/{id}` — catalog-service — 58 ms
      - `INSERT booking.reservation` — 14 ms
    - `step cobrar` → `POST /api/v1/pagos` — payment-service — **3,031 ms** ← slow hop
      - `wait for Stripe connection` — payment-service — **2,610 ms**
      - `stripe charge` — 395 ms
    - `step confirmar-reserva` → `POST /api/v1/reservas/{id}/confirmar` — 146 ms
      - `INSERT booking.outbox_event` — 9 ms
- `relay publish ReservaConfirmada` — booking-service — 12 ms (async, same trace)

## Four log lines of that trace (CP2 format)

```json
{"timestamp":"2026-10-24T14:31:07.215Z","level":"INFO","service":"property-workflow","version":"2.1.0","environment":"main","trace_id":"3b9f0c2d7e5a41f6a8c1d2e3f4a5b6c7","span_id":"8a1b2c3d4e5f6a7b","correlation_id":"corr-a41f","event":"saga.step.started","message":"step cobrar started","saga_id":"c7d1e2f3-0a4b-4c5d-8e6f-7a8b9c0d1e2f","saga_step":"cobrar","reserva_id":"5e6f7a8b-9c0d-4e1f-a2b3-c4d5e6f7a8b9"}
{"timestamp":"2026-10-24T14:31:09.842Z","level":"WARN","service":"payment-service","version":"2.1.0","environment":"main","trace_id":"3b9f0c2d7e5a41f6a8c1d2e3f4a5b6c7","span_id":"9b2c3d4e5f6a7b8c","correlation_id":"corr-a41f","event":"stripe.pool.wait","message":"waited for a free Stripe connection (pool size 2)","duration_ms":2610,"saga_step":"cobrar","reserva_id":"5e6f7a8b-9c0d-4e1f-a2b3-c4d5e6f7a8b9"}
{"timestamp":"2026-10-24T14:31:10.246Z","level":"INFO","service":"payment-service","version":"2.1.0","environment":"main","trace_id":"3b9f0c2d7e5a41f6a8c1d2e3f4a5b6c7","span_id":"9b2c3d4e5f6a7b8c","correlation_id":"corr-a41f","event":"pago.approved","message":"charge approved, Pago recorded as APROBADO","duration_ms":3031,"saga_step":"cobrar","pago_id":"0f1e2d3c-4b5a-4968-8776-a5b4c3d2e1f0","monto_cents":36000000}
{"timestamp":"2026-10-24T14:31:10.855Z","level":"WARN","service":"property-workflow","version":"2.1.0","environment":"main","trace_id":"3b9f0c2d7e5a41f6a8c1d2e3f4a5b6c7","span_id":"8a1b2c3d4e5f6a7b","correlation_id":"corr-a41f","event":"saga.finished","message":"saga reservar ended COMPLETED above the 3 s latency target","duration_ms":3640,"saga_id":"c7d1e2f3-0a4b-4c5d-8e6f-7a8b9c0d1e2f","reserva_id":"5e6f7a8b-9c0d-4e1f-a2b3-c4d5e6f7a8b9"}
```

## What the SLIs reach (during the 40 minutes)

| SLI | Value in the window | Target |
|---|---|---|
| Availability (non-5xx) | 100 % (no 5xx) | 99.5 % |
| Latency (≤ 3 s) | 75 % | 95 % |
| Integrity (not `FAILED`) | 100 % | 99.9 % |

## Which alert fires first

- `SagaReservarAvailabilityFastBurn` / `SlowBurn`: **do not fire** — there are no `5xx`.
- `SagaReservarLatencyBurn` (> 20 % slow over 30 m **and** 5 m): the 5 m window is at 25 %
  after ~5 minutes; the 30 m window grows as 25 % × t/30 and passes 20 % at
  **t = 24 min**. It fires at **14:34** (incident started 14:10). It is a ticket, so the
  on-call sees it in the team chat, not as a page.

## Which runbook step stops the bleeding

Step 2 "Confirm": the step query shows p95 of `cobrar` at 3 s while `crear-reserva` and
`confirmar-reserva` are normal; the trace shows `wait for Stripe connection`. Mitigation
1 (open the breaker) is rejected: Stripe itself is fine, and opening it would reject every
payment. **Mitigation 2, rollback** of `payment-service` to `v2.0.0` at 14:48 stops it;
the latency is normal at 14:50.

## Blameless postmortem

**Timeline (2026-10-24, holiday Saturday)**

| Time | Event |
|---|---|
| 14:02 | `payment-service` `v2.1.0` deployed to `main` |
| 14:10 | Peak traffic starts; 1 of every 4 charges waits about 2.6 s for a Stripe connection |
| 14:34 | `SagaReservarLatencyBurn` fires (ticket) |
| 14:38 | On-call follows the runbook, finds `cobrar` slow and `stripe.pool.wait` in the logs |
| 14:48 | Rollback of `payment-service` to `v2.0.0` |
| 14:50 | Latency back to normal; alert resolves at 14:53 |

**Impact:** 40 Sagas, **10 slow** (all `COMPLETED`, no money lost, no `FAILED`). That is
10 of the 300 slow Sagas allowed per month = **3.3 % of the latency budget**. In
minutes: 40 min × 25 % = 10 min of full slowness = **0.46 %** of the 2,160 minutes; the
request count is the real number because the peak had 7 times the average traffic.
Availability and integrity budgets: **0 %** used.

**Root cause:** release `v2.1.0` of `payment-service` set the HTTP connection pool to
Stripe to 2 (it was 20). At the peak, more than 2 charges at the same time had to wait
for a free connection.

**Contributing factors**
- The pool size was a constant in the code, not configuration, and nobody reviewed it.
- No load test before the release; in `qa` the traffic is one person clicking.
- There is no alert per Saga step: we only saw the total latency, 24 minutes late.

**Action items (user stories for the next sprint)**

1. **As** on-call, **I want** a ticket alert when p95 of step `cobrar` is above 2 s,
   **so that** a slow Payment is seen before the latency budget burns.
   *Given* `histogram_quantile(0.95, …saga_step_duration_seconds_bucket{step="cobrar"}…)` > 2 s
   for 10 minutes, *when* Prometheus evaluates the rules, *then* `SagaStepCobrarSlow` fires
   with `team="pms-property"` and a `runbook_url`.
2. **As** a Payment developer, **I want** the Stripe pool size in `PAYMENT_STRIPE_POOL_SIZE`
   with default 20, **so that** a release cannot lower it by accident.
   *Given* the variable is not set, *when* `payment-service` starts, *then* it uses 20 and
   logs `config.loaded` with `pool_size=20`. *Given* a value below 5 in `main`, *then* the
   service refuses to start.
3. **As** the team, **I want** a load test of 60 Sagas per hour against `qa` in every
   release PR of `payment-service`, **so that** saturation shows before `main`.
   *Given* a PR to `main`, *when* the load test runs, *then* p95 of
   `saga_reservar_duration_seconds` is under 3 s, or the PR is blocked.
