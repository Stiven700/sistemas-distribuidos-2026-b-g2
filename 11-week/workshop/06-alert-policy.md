# CP7 · Alert policy — Saga `reservar`

Rules in `06-alerts.yml`. We alert on **how fast the error budget of CP6 is spent**,
not on CPU or queue sizes. Each alert needs a long window (is it real?) **and** a short
window (is it still happening?), so it fires fast and also stops fast after a fix.

Numbers from CP6: 6,000 Sagas per 30 days; availability budget 30 requests / 216 min;
latency budget 300 slow Sagas / 2,160 min.

## The three alerts

| Alert | Condition | Severity | Budget spent before it fires |
|---|---|---|---|
| `SagaReservarAvailabilityFastBurn` | error ratio > 7.2 % (14.4 × 0.5 %) over 1 h **and** 5 m, for 2 m | page | 14.4 × 1 h / 720 h = **2 %** of the month: 0.6 failed requests, **4.3 min** of outage |
| `SagaReservarAvailabilitySlowBurn` | error ratio > 3 % (6 × 0.5 %) over 6 h **and** 30 m, for 5 m | ticket | 6 × 6 h / 720 h = **5 %**: 1.5 failed requests, **10.8 min** |
| `SagaReservarLatencyBurn` | slow ratio (> 3 s) > 20 % (4 × 5 %) over 30 m **and** 5 m | ticket | 4 × 0.5 h / 720 h = **0.28 %** of the latency budget: about 0.8 slow Sagas, **6 min** |

If nobody reacts: at burn rate 14.4 the whole availability budget is gone in
720 h / 14.4 = **50 hours**; at 6, in **5 days**. That is why the fast one wakes
someone up and the slow one can wait until the next working day.

**Low traffic warning.** With about 8 Sagas per hour, one `5xx` in an hour is already
12.5 % and fires the fast alert. On a normal day that is acceptable for us: a failing
`POST /api/v1/sagas/reservar` means a guest who could not book. If it becomes noisy, we
will add a minimum of 5 requests in the hour to the condition.

The latency alert is only a **ticket**: a slow Saga still books the property. It exists
because a slow `cobrar` step makes guests press "Confirmar y pagar" twice.

## Cause-based alerts we will NOT create

1. **"CPU of `booking-service` above 80 %".** High CPU does not mean guests are failing;
   the JVM can sit at 85 % and still answer every Saga in time. If CPU does hurt, the
   availability or latency burn alert fires anyway. It would page people for nothing.
2. **"Payment circuit breaker OPEN" (ADR-003).** When the circuit opens, Payment answers
   `201 RECHAZADO` with `PASARELA_NO_DISPONIBLE` and the Saga ends `COMPENSATED`: the guest
   gets a clear answer and no `5xx`. A short opening during a Stripe hiccup is the design
   working. We watch it in the dashboard (`payment_circuit_breaker_state`, CP4) and in
   the runbook, not as a page.

Also not alerts: the depth of the RabbitMQ queues of Catalog and Notification. They are
eventually consistent by design; a backlog of a few minutes does not break a booking.
