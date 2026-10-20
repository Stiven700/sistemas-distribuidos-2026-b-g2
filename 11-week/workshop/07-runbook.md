# CP8 · Runbook — `SagaReservarAvailabilityFastBurn`

**What it means:** more than 7.2 % of `POST /api/v1/sagas/reservar` answered `5xx` in
the last hour and in the last 5 minutes. Guests cannot book. At this speed the monthly
availability budget (216 min) is gone in about 50 hours.

**Severity:** page. **Owner of the journey:** `property-workflow` (Saga `reservar`).

## 1. Symptoms

- Booking portal (`:4202`) shows the error screen after "Confirmar y pagar".
- Sagas end with `status: FAILED`, or the gateway answers `500 INTERNAL_ERROR` / `504`.
- The alert has label `slo="availability"`, `service="property-workflow"`.

## 2. Confirm (5 minutes)

Is it real and how big is it?

```promql
saga_reservar:error_ratio:rate5m
sum by (outcome) (rate(saga_reservar_requests_total[5m]))
```

Which step is failing?

```promql
histogram_quantile(0.95, sum by (le, step) (rate(saga_step_duration_seconds_bucket[5m])))
sum by (status_class) (rate(payment_http_requests_total{route="/api/v1/pagos"}[5m]))
sum by (error_code) (rate(booking_http_requests_total[5m]))
max(payment_circuit_breaker_state)
```

Take one failing request and read its whole story (CP2 contract). Get a `trace_id` from
the error logs of the workflow, then filter every service by it:

```bash
docker compose logs --since 15m property-workflow | grep '"level":"ERROR"' | grep '"event":"saga.finished"'
docker compose logs --since 15m | grep '"trace_id":"<trace_id>"'
```

Read the lines in order of `timestamp`: the last service with an `ERROR` before
`saga.finished` is where it broke (`booking-service`, `payment-service`, `catalog-service`
or Stripe behind Payment).

## 3. Mitigate — in order of risk (lowest first)

| # | Action | When | How |
|---|---|---|---|
| 1 | **Open the Payment circuit breaker** (ADR-003) | Stripe is slow or failing and Payment's calls hang | Set Payment's breaker to forced-open through its config and restart it. Sagas then end `COMPENSATED` with `PASARELA_NO_DISPONIBLE` in under a second: guests get a clear "try later", no `5xx`, no unknown charges |
| 2 | **Roll back** the last release | The burn started after a deploy (check the tags) | Point the service in `property-infra` `docker-compose` to the previous image tag (`v2.0.0`), `docker compose up -d <service>`. One service at a time |
| 3 | **Scale out** `booking-service` or `payment-service` | Saturation: high latency and pending requests, no errors in logs | `docker compose up -d --scale payment-service=2`. **Not** `property-workflow`: it must run as a single instance (ADR-010) |

Never: restart `property-workflow` in the middle of the incident without reason. On
start it settles `RUNNING` Sagas (compensates, never replays), which adds load to
Booking and Payment while they are already failing.

## 4. Escalate

| After | Who | Role |
|---|---|---|
| 0 min | On-call of the week (rotates in the team) | Confirms, runs step 2 and mitigation 1 |
| 15 min without improvement | Owner of the failing service (Booking, Payment or Catalog, from the trace) | Decides rollback or scale |
| 30 min, or any Saga `FAILED` with a charge | Whole team (4) + teacher as product owner | `FAILED` Sagas may have charged a guest without a booking: list them with `GET /api/v1/pagos/por-reserva/{reservaId}` and refund by hand |

## 5. Verify recovery

- `saga_reservar:error_ratio:rate5m` below 0.5 % for 15 minutes, and the alert resolves
  (the 5 m window drops first).
- `sum by (outcome) (rate(saga_reservar_requests_total[5m]))` shows `completed` again.
- Make one test booking in `qa` and follow its `trace_id` end to end, including the
  `ReservaConfirmada` consumed by Catalog and Notification.
- No `PENDIENTE` reservation older than 15 minutes stays behind (the worker expires them).
- Write the incident in `08-incident.md` format and open the postmortem.
