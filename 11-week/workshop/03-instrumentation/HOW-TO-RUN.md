# CP3 · How to run

Minimal module of `property-workflow` (Java 17+, no framework): a `TraceFilter` that
reads or creates the W3C `traceparent`, a `JsonLogger` that writes the CP2 contract, the
outbound call of step `crear-reserva` to `booking-service` with the context propagated,
and `/metrics` with `saga_reservar_requests_total` and `saga_reservar_duration_seconds`.

## Requirements

- JDK 17 or newer (tested with Temurin 21).
- Maven 3.9, or Docker if Maven is not installed.

## Test

```bash
cd 11-week/workshop/03-instrumentation
mvn test
```

Without Maven (Windows PowerShell, Docker Desktop running):

```powershell
docker run --rm -v "${PWD}:/app" -w /app maven:3.9-eclipse-temurin-21 mvn test
```

`TracePropagationTest` starts a fake `booking-service`, sends
`POST /api/v1/sagas/reservar` with a known `traceparent`, and checks that booking got the
same `trace_id` with a new `span_id`, that the response returns the trace, that the
`Idempotency-Key` ends in `:crear-reserva`, and that `/metrics` counted the request.

## Run

```bash
mvn -q compile
java -cp target/classes co.edu.corhuila.property.workflow.observability.WorkflowApp
curl -i -X POST http://localhost:3006/api/v1/sagas/reservar -H "traceparent: 00-7c1e9a04b2d54f3e8a61d0c9f2b3a4e5-1a2b3c4d5e6f7081-01" -d "{}"
curl http://localhost:3006/metrics
```

With no `booking-service` on `:3001` the Saga answers `500` and the log shows
`booking.request.failed` with the same `trace_id`.

## Console output on my machine

```text
[INFO]
[INFO] -------------------------------------------------------
[INFO]  T E S T S
[INFO] -------------------------------------------------------
[INFO] Running co.edu.corhuila.property.workflow.observability.TracePropagationTest
{"timestamp":"2026-10-19T21:46:50.027990809Z","level":"INFO","service":"property-workflow","version":"2.0.0","environment":"dev","trace_id":"7c1e9a04b2d54f3e8a61d0c9f2b3a4e5","span_id":"5951bae9f49b88e4","correlation_id":"corr-33822d72-307f-42b3-92a3-9259da99215d","event":"saga.step.started","message":"saga reservar started","saga_id":"1d190d3e-dcb7-4415-91f6-c83a7f61b4dc","saga_step":"crear-reserva"}
{"timestamp":"2026-10-19T21:46:50.073832933Z","level":"INFO","service":"property-workflow","version":"2.0.0","environment":"dev","trace_id":"7c1e9a04b2d54f3e8a61d0c9f2b3a4e5","span_id":"5951bae9f49b88e4","correlation_id":"corr-33822d72-307f-42b3-92a3-9259da99215d","event":"saga.step.completed","message":"booking-service answered step crear-reserva","status":201,"saga_step":"crear-reserva","saga_id":"1d190d3e-dcb7-4415-91f6-c83a7f61b4dc","duration_ms":44}
{"timestamp":"2026-10-19T21:46:50.074979749Z","level":"INFO","service":"property-workflow","version":"2.0.0","environment":"dev","trace_id":"7c1e9a04b2d54f3e8a61d0c9f2b3a4e5","span_id":"5951bae9f49b88e4","correlation_id":"corr-33822d72-307f-42b3-92a3-9259da99215d","event":"http.request.completed","message":"POST /api/v1/sagas/reservar","status":201,"duration_ms":87}
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0, Time elapsed: 0.456 s -- in co.edu.corhuila.property.workflow.observability.TracePropagationTest
[INFO]
[INFO] Results:
[INFO]
[INFO] Tests run: 1, Failures: 0, Errors: 0, Skipped: 0
[INFO]
[INFO] ------------------------------------------------------------------------
[INFO] BUILD SUCCESS
[INFO] ------------------------------------------------------------------------
[INFO] Total time:  26.022 s
[INFO] Finished at: 2026-10-19T21:46:50Z
[INFO] ------------------------------------------------------------------------

```
