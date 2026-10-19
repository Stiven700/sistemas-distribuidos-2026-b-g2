# CP4 · Golden signals and metric catalog

Journey: Saga `reservar` (CP1). Names follow Prometheus conventions: `snake_case`,
service prefix, unit suffix (`_seconds`, `_bytes`), `_total` for counters. Every series
also gets `service`, `environment` (`dev`/`qa`/`main`) and `version` from the scrape
config, not from the code.

## api-gateway (NGINX, nginx-prometheus-exporter + log-based metrics)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | `gateway_request_duration_seconds` | histogram | `route`, `method` | `histogram_quantile(0.95, sum by (le, route) (rate(gateway_request_duration_seconds_bucket[5m])))` |
| Traffic | `gateway_requests_total` | counter | `route`, `method`, `status_class` | `sum by (route) (rate(gateway_requests_total[5m]))` |
| Errors | `gateway_requests_total` | counter | `status_class="5xx"` | `sum(rate(gateway_requests_total{status_class="5xx"}[5m])) / sum(rate(gateway_requests_total[5m]))` |
| Saturation | `nginx_connections_active` | gauge | — | `max(nginx_connections_active)` |

## property-workflow (Saga `reservar`)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | `saga_reservar_duration_seconds` | histogram | — | `histogram_quantile(0.95, sum by (le) (rate(saga_reservar_duration_seconds_bucket[5m])))` |
| Traffic | `saga_reservar_requests_total` | counter | `route`, `outcome` | `sum(rate(saga_reservar_requests_total[5m]))` |
| Errors | `saga_reservar_requests_total` | counter | `outcome="failed"` or `"error"` | `sum(rate(saga_reservar_requests_total{outcome=~"failed\|error"}[5m])) / sum(rate(saga_reservar_requests_total[5m]))` |
| Saturation | `saga_instances_running` | gauge | — | `max(saga_instances_running)` (single instance, ADR-010) |

Extra: `saga_step_duration_seconds` (histogram, label `step`) to see which of the three
steps is slow.

## booking-service

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | `booking_http_request_duration_seconds` | histogram | `route`, `method` | `histogram_quantile(0.95, sum by (le, route) (rate(booking_http_request_duration_seconds_bucket[5m])))` |
| Traffic | `booking_reservas_total` | counter | `estado` (`PENDIENTE`, `CONFIRMADA`, `CANCELADA`, `EXPIRADA`) | `sum by (estado) (rate(booking_reservas_total[5m]))` |
| Errors | `booking_http_requests_total` | counter | `route`, `error_code` | `sum by (error_code) (rate(booking_http_requests_total{error_code!="none"}[5m]))` |
| Saturation | `booking_outbox_pending_events` | gauge | — | `max(booking_outbox_pending_events)` (rows of `outbox_event` not yet published) |

`error_code="BUSINESS_RULE_VIOLATION"` on `POST /api/v1/reservas` is mostly INV-001
(overlapping dates). It is counted but it is **not** an availability error (CP6).

## catalog-service (Python, FastAPI)

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | `catalog_http_request_duration_seconds` | histogram | `route` | `histogram_quantile(0.95, sum by (le) (rate(catalog_http_request_duration_seconds_bucket{route="/api/v1/propiedades/{id}"}[5m])))` |
| Traffic | `catalog_http_requests_total` | counter | `route`, `status_class` | `sum by (route) (rate(catalog_http_requests_total[5m]))` |
| Errors | `catalog_http_requests_total` | counter | `status_class="5xx"` | `sum(rate(catalog_http_requests_total{status_class="5xx"}[5m]))` |
| Saturation | `catalog_events_lag_seconds` | gauge | `event` | `max by (event) (catalog_events_lag_seconds)` (age of the oldest unconsumed `ReservaConfirmada`) |

## payment-service

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | `payment_gateway_call_duration_seconds` | histogram | `operation` (`charge`, `refund`) | `histogram_quantile(0.95, sum by (le) (rate(payment_gateway_call_duration_seconds_bucket{operation="charge"}[5m])))` |
| Traffic | `payment_pagos_total` | counter | `estado` (`APROBADO`, `RECHAZADO`), `motivo_rechazo` | `sum by (estado) (rate(payment_pagos_total[5m]))` |
| Errors | `payment_http_requests_total` | counter | `route`, `status_class` | `sum(rate(payment_http_requests_total{route="/api/v1/pagos",status_class="5xx"}[5m]))` |
| Saturation | `payment_circuit_breaker_state` | gauge | — (0 closed, 1 half-open, 2 open) | `max(payment_circuit_breaker_state)` (ADR-003) |

## notification-service

| Signal | Metric | Type | Labels | PromQL |
|---|---|---|---|---|
| Latency | `notification_delivery_duration_seconds` | histogram | `channel` | `histogram_quantile(0.95, sum by (le) (rate(notification_delivery_duration_seconds_bucket[5m])))` |
| Traffic | `notification_events_consumed_total` | counter | `event` | `sum by (event) (rate(notification_events_consumed_total[5m]))` |
| Errors | `notification_deliveries_total` | counter | `channel`, `outcome` | `sum(rate(notification_deliveries_total{outcome="failed"}[5m]))` |
| Saturation | `rabbitmq_queue_messages_ready` | gauge (RabbitMQ exporter) | `queue` | `max by (queue) (rabbitmq_queue_messages_ready{queue=~".*notification.*\|.*dlq.*"})` |

## Label cardinality

| Label | Values | Count |
|---|---|---|
| `route` | route **template** from the gateway table (`/api/v1/reservas/{id}/confirmar`, not the raw URL) | ~15 |
| `method` | GET, POST | 2 |
| `status_class` | 2xx, 4xx, 5xx | 3 |
| `outcome` (Saga) | completed, compensated, failed, error | 4 |
| `step` | crear-reserva, cobrar, confirmar-reserva (+ `:compensar`) | 5 |
| `estado` | states of `Reserva` (4) or `Pago` (2) | ≤ 4 |
| `motivo_rechazo` | closed list of Payment | 4 |
| `error_code` | 6 codes of the norm + `none` | 7 |
| `channel`, `event`, `operation`, `queue` | closed lists | < 10 each |

Worst series per histogram: `route` 15 × `method` 2 × 9 buckets = 270. Fine.

**Forbidden labels** and what replaces them:

- `saga_id`, `reserva_id`, `pago_id`, `usuario_id`, `propiedad_id` → unbounded; they go in
  the logs (CP2) and the trace, never in a metric.
- Raw URL (`/api/v1/reservas/9b3e…`) → route template.
- E-mail, `Idempotency-Key`, `trace_id` → logs only (and e-mail masked).

## Histogram buckets

`saga_reservar_duration_seconds`: `0.1, 0.25, 0.5, 1, 2, 3, 5, 10`.
The guest waits on "Confirmar y pagar" until the Saga ends, and the Saga calls Booking,
Catalog, Payment and Stripe in a row. A good Saga ends under **2 s**; **3 s** is our
latency SLO edge (CP6) and the Stripe timeout; **10 s** is near the gateway time-out,
so anything above it is a failure anyway. Buckets are dense around 1–3 s, where the
SLO decision is taken.

`booking_http_request_duration_seconds` and `catalog_…`: `0.025, 0.05, 0.1, 0.25, 0.5, 1, 2`
— one hop of a REST call with one database query should answer in tens of milliseconds.
