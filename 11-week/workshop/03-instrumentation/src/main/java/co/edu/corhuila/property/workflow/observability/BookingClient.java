package co.edu.corhuila.property.workflow.observability;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

/** Step crear-reserva: POST /api/v1/reservas of booking-service, with the trace context. */
public class BookingClient {

    private final HttpClient http = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(2)).build();
    private final String baseUrl;

    public BookingClient(String baseUrl) {
        this.baseUrl = baseUrl;
    }

    public int crearReserva(String sagaId, String body) throws IOException, InterruptedException {
        TraceContext parent = TraceContext.current();
        TraceContext child = parent.child();
        HttpRequest request = HttpRequest.newBuilder(URI.create(baseUrl + "/api/v1/reservas"))
                .timeout(Duration.ofSeconds(3))
                .header("Content-Type", "application/json")
                .header("traceparent", child.toHeader())
                .header("X-Correlation-Id", Correlation.current())
                .header("Idempotency-Key", sagaId + ":crear-reserva")
                .POST(HttpRequest.BodyPublishers.ofString(body))
                .build();
        long start = System.nanoTime();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        long durationMs = (System.nanoTime() - start) / 1_000_000;
        JsonLogger.info("saga.step.completed", "booking-service answered step crear-reserva",
                Map.of("saga_id", sagaId, "saga_step", "crear-reserva",
                        "status", response.statusCode(), "duration_ms", durationMs));
        return response.statusCode();
    }
}
