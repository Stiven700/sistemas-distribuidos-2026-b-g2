# CP6 · SLIs, SLOs and error budget — Saga `reservar`

Journey from CP1: the guest presses "Confirmar y pagar" and `POST /api/v1/sagas/reservar`
runs `crear-reserva` → `cobrar` → `confirmar-reserva` in `property-workflow`. Metrics
come from CP4: `gateway_requests_total` (api-gateway) and `saga_reservar_requests_total`,
`saga_reservar_duration_seconds` (property-workflow, CP3).

## Traffic estimate

Property starts in Neiva and Huila. The catalog seed has around 100 properties, and a
guest usually tries once or twice before a booking goes through (dates taken, card
declined). We expect **about 200 Saga requests per day**, more on weekends and
holidays (San Pedro in June).

- Per 30 days: 200 × 30 = **6,000 requests**.
- Window: **30 days = 43,200 minutes**.

These numbers are an estimate for the first months; we will replace them with the real
`increase(saga_reservar_requests_total[30d])` after corte 3.

## SLO 1 · Availability

- **SLI:** requests to `POST /api/v1/sagas/reservar` that did not get a `5xx`, over all
  requests. A `4xx` is the guest's request (for example `422 BUSINESS_RULE_VIOLATION`
  when the dates overlap, INV-001), so it counts as good. A Saga that ends
  `COMPENSATED` because the card was declined also answers correctly: good.
- **PromQL:**
  ```promql
  sum(rate(gateway_requests_total{route="/api/v1/sagas/reservar",status_class!="5xx"}[30d]))
  /
  sum(rate(gateway_requests_total{route="/api/v1/sagas/reservar"}[30d]))
  ```
- **Target:** 99.5 % · **Window:** 30 days, rolling.
- **Budget:** 0.5 % of 6,000 = **30 failed requests** per month, or 0.5 % of 43,200 =
  **216 minutes** of full outage.

## SLO 2 · Latency

- **SLI:** Sagas that finish (any outcome) in 3 seconds or less, over all Sagas. 3 s is
  the Stripe timeout and the point where the guest starts to press the button again.
- **PromQL** (the Java client writes the bucket as `3.0`):
  ```promql
  sum(rate(saga_reservar_duration_seconds_bucket{le="3.0"}[30d]))
  /
  sum(rate(saga_reservar_duration_seconds_count[30d]))
  ```
- **Target:** 95 % · **Window:** 30 days.
- **Budget:** 5 % of 6,000 = **300 slow Sagas** per month. In minutes: 5 % of 43,200 =
  **2,160 minutes** in which every Saga could be slow before the SLO breaks.

## SLO 3 · Saga integrity (no `FAILED` Sagas)

A Saga ends `FAILED` when a compensation failed or the result of a charge is unknown.
That is the worst case for a guest: money may have moved and the `Reserva` is not
confirmed; a person has to fix it by hand.

- **SLI:** Sagas that end `COMPLETED` or `COMPENSATED`, over all Sagas that ended.
- **PromQL:**
  ```promql
  1 - (
    sum(increase(saga_reservar_requests_total{outcome="failed"}[30d]))
    /
    sum(increase(saga_reservar_requests_total[30d]))
  )
  ```
- **Target:** 99.9 % · **Window:** 30 days.
- **Budget:** 0.1 % of 6,000 = **6 `FAILED` Sagas** per month (43.2 minutes).

| SLO | Target | Budget (requests / month) | Budget (minutes / 30 days) |
|---|---|---|---|
| Availability | 99.5 % | 30 | 216 |
| Latency ≤ 3 s | 95 % | 300 | 2,160 |
| Integrity | 99.9 % | 6 | 43.2 |

## Error budget policy

| Budget used | What the team does |
|---|---|
| **50 %** | Warning in the team chat. Review the last deploys of `property-workflow`, `booking-service` and `payment-service`. Normal work goes on. |
| **75 %** | Only low-risk changes go to `main`. Every release PR of these services needs a rollback plan. One person checks the burn rate daily. |
| **100 %** | Feature freeze for the journey: only fixes for reliability reach `main` until the 30-day window recovers. The next sprint starts with the action items of the postmortem (CP9). For the Integrity SLO, every `FAILED` Saga is reviewed one by one with Payment. |
