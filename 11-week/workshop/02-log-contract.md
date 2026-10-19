# CP2 · Structured log contract

Every service of the journey (`api-gateway`, `property-workflow`, `booking-service`,
`catalog-service`, `payment-service`, `notification-service`) writes **one JSON object
per line** to stdout, with the same field names. One query by `trace_id` returns the
whole Saga `reservar`.

## Mandatory fields

| Field | Type | Required | Example | Why it exists |
|---|---|---|---|---|
| `timestamp` | string, ISO-8601 UTC | yes | `2026-10-19T14:20:03.118Z` | Order lines from six services on one timeline |
| `level` | enum `ERROR` `WARN` `INFO` `DEBUG` | yes | `INFO` | Filter by severity |
| `service` | string | yes | `booking-service` | Know which container wrote it |
| `version` | string (semver) | yes | `2.0.0` | Tie a failure to a release tag |
| `environment` | enum `dev` `qa` `main` | yes | `qa` | Same names as our branches and `env/*.env` files |
| `trace_id` | 32 hex | yes | `7c1e9a04b2d54f3e8a61d0c9f2b3a4e5` | Join every hop of one request (from `traceparent`) |
| `span_id` | 16 hex | yes | `a3f09c2e71b4d856` | Know which hop inside the trace |
| `correlation_id` | string | yes | `corr-5d21` | Our existing `X-Correlation-Id`; it survives retries of the same Saga |
| `event` | string, `noun.verb` | yes | `reserva.created` | Stable name to count and alert on; never free text |
| `message` | string | yes | `reservation created as PENDIENTE` | Human-readable sentence |
| `duration_ms` | integer | optional (required on request end and outbound calls) | `412` | Latency per hop without a tracing backend |

## Domain fields

| Field | Type | Required | Example | Why it exists |
|---|---|---|---|---|
| `saga_id` | uuid | when inside a Saga | `5d21f0aa-…` | Find every step and compensation of one `saga_instance` |
| `saga_step` | enum `crear-reserva` `cobrar` `confirmar-reserva` (+ `:compensar`) | when inside a Saga | `cobrar` | Know which step failed; matches the Idempotency-Key suffix |
| `reserva_id` | uuid | when known | `9b3e…` | Follow one `Reserva` through `PENDIENTE` → `CONFIRMADA`/`CANCELADA`/`EXPIRADA` |
| `propiedad_id` | string | when known | `prop-118` | See if failures cluster on one `Propiedad` (INV-001 overlaps) |
| `pago_id` | uuid | when known | `e41c…` | Link the log to `payment.payment` |
| `monto_cents` | integer | on payment lines | `48000000` | Amount in integer cents, as the norm requires; never decimals |
| `error_code` | enum of the norm (`VALIDATION_ERROR`, `BUSINESS_RULE_VIOLATION`, …) | on failures | `BUSINESS_RULE_VIOLATION` | Same 6 codes as the error envelope |

## Never log

| Data | Where it appears | How it is masked |
|---|---|---|
| `tokenPago` (card token) | body of `POST /api/v1/pagos` | Never written; the logger drops the field by name |
| Card number, CVV, expiry | Stripe response | Never requested; only `motivoRechazo` is logged |
| `Authorization` header, JWT, `refreshToken`, `SERVICE_TOKEN` | every request | Header removed; log `auth=present` / `auth=absent` |
| Password | `POST /api/v1/register`, `/login` | Body of identity endpoints is never logged |
| Guest e-mail, name, phone | Identity, Notification | E-mail as `d***@gmail.com`; name and phone not logged |
| `usuario_id` | every service | Logged only as a SHA-256 hash prefix (`usr_9f2c…`) |
| Full `Idempotency-Key` from the browser | E-23 | Only the Saga-generated keys (`<sagaId>:<step>`) are logged |

## Example: one failed Saga, one `trace_id`

The card charge times out once at Stripe, the retry with the same key comes back
declined, and the workflow compensates.

```json
{"timestamp":"2026-10-19T14:20:03.118Z","level":"INFO","service":"api-gateway","version":"2.0.0","environment":"qa","trace_id":"7c1e9a04b2d54f3e8a61d0c9f2b3a4e5","span_id":"1a2b3c4d5e6f7081","correlation_id":"corr-5d21","event":"http.request.received","message":"POST /api/v1/sagas/reservar routed to workflow:3006"}
{"timestamp":"2026-10-19T14:20:03.140Z","level":"INFO","service":"property-workflow","version":"2.0.0","environment":"qa","trace_id":"7c1e9a04b2d54f3e8a61d0c9f2b3a4e5","span_id":"b7c8d9e0f1a2b3c4","correlation_id":"corr-5d21","event":"saga.step.started","message":"saga reservar started","saga_id":"5d21f0aa-3c41-4e8b-9d1a-77e2b0c4f911","saga_step":"crear-reserva","propiedad_id":"prop-118"}
{"timestamp":"2026-10-19T14:20:03.552Z","level":"INFO","service":"booking-service","version":"2.0.0","environment":"qa","trace_id":"7c1e9a04b2d54f3e8a61d0c9f2b3a4e5","span_id":"c4d5e6f7a8b9c0d1","correlation_id":"corr-5d21","event":"reserva.created","message":"reservation created as PENDIENTE","duration_ms":412,"saga_id":"5d21f0aa-3c41-4e8b-9d1a-77e2b0c4f911","reserva_id":"9b3e6a12-0f4d-4c7e-a5b8-2d1e9f0c3b7a","propiedad_id":"prop-118","monto_cents":48000000}
{"timestamp":"2026-10-19T14:20:06.601Z","level":"ERROR","service":"payment-service","version":"2.0.0","environment":"qa","trace_id":"7c1e9a04b2d54f3e8a61d0c9f2b3a4e5","span_id":"d1e2f3a4b5c6d7e8","correlation_id":"corr-5d21","event":"stripe.charge.timeout","message":"Stripe did not answer, asking again with the same idempotency key","duration_ms":3001,"saga_id":"5d21f0aa-3c41-4e8b-9d1a-77e2b0c4f911","saga_step":"cobrar","reserva_id":"9b3e6a12-0f4d-4c7e-a5b8-2d1e9f0c3b7a","monto_cents":48000000}
{"timestamp":"2026-10-19T14:20:07.034Z","level":"WARN","service":"payment-service","version":"2.0.0","environment":"qa","trace_id":"7c1e9a04b2d54f3e8a61d0c9f2b3a4e5","span_id":"d1e2f3a4b5c6d7e8","correlation_id":"corr-5d21","event":"pago.rejected","message":"charge declined: TARJETA_RECHAZADA, Pago recorded as RECHAZADO","duration_ms":3433,"saga_id":"5d21f0aa-3c41-4e8b-9d1a-77e2b0c4f911","saga_step":"cobrar","reserva_id":"9b3e6a12-0f4d-4c7e-a5b8-2d1e9f0c3b7a","pago_id":"e41c7b20-8a5f-4d3e-b6c9-0f1a2e3d4c5b","monto_cents":48000000,"error_code":"BUSINESS_RULE_VIOLATION"}
{"timestamp":"2026-10-19T14:20:07.210Z","level":"INFO","service":"booking-service","version":"2.0.0","environment":"qa","trace_id":"7c1e9a04b2d54f3e8a61d0c9f2b3a4e5","span_id":"e8f9a0b1c2d3e4f5","correlation_id":"corr-5d21","event":"reserva.cancelled","message":"reservation cancelled, reason SAGA_COMPENSADA","duration_ms":95,"saga_id":"5d21f0aa-3c41-4e8b-9d1a-77e2b0c4f911","saga_step":"crear-reserva:compensar","reserva_id":"9b3e6a12-0f4d-4c7e-a5b8-2d1e9f0c3b7a"}
{"timestamp":"2026-10-19T14:20:07.236Z","level":"WARN","service":"property-workflow","version":"2.0.0","environment":"qa","trace_id":"7c1e9a04b2d54f3e8a61d0c9f2b3a4e5","span_id":"b7c8d9e0f1a2b3c4","correlation_id":"corr-5d21","event":"saga.finished","message":"saga reservar ended COMPENSATED, failed step cobrar","duration_ms":4096,"saga_id":"5d21f0aa-3c41-4e8b-9d1a-77e2b0c4f911","saga_step":"cobrar","reserva_id":"9b3e6a12-0f4d-4c7e-a5b8-2d1e9f0c3b7a"}
```

## Log levels in this team

| Level | When | Example in Property |
|---|---|---|
| `ERROR` | A call failed and the system cannot finish its job without a retry or a person | `stripe.charge.timeout`; a Saga ending `FAILED`; the outbox relay cannot reach RabbitMQ |
| `WARN` | An expected failure that the system handled by itself | `pago.rejected` (card declined); Saga `COMPENSATED`; a duplicate event ignored by `processed_events` |
| `INFO` | A business fact or the start/end of a request | `reserva.created`, `reserva.confirmed`, `saga.step.started` |
| `DEBUG` | Detail for local work only, off in `qa` and `main` | Value of the Idempotency-Key lookup, the computed `daterange` of a `Reserva` |
