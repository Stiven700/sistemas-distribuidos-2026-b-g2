package co.edu.corhuila.property.workflow.observability;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sun.net.httpserver.HttpServer;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.atomic.AtomicReference;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class TracePropagationTest {

    private static final String TRACE_ID = "7c1e9a04b2d54f3e8a61d0c9f2b3a4e5";
    private static final String INCOMING = "00-" + TRACE_ID + "-1a2b3c4d5e6f7081-01";

    private final AtomicReference<String> seenByBooking = new AtomicReference<>();
    private final AtomicReference<String> keySeenByBooking = new AtomicReference<>();
    private HttpServer fakeBooking;
    private WorkflowApp workflow;

    @BeforeEach
    void start() throws Exception {
        fakeBooking = HttpServer.create(new InetSocketAddress(0), 0);
        fakeBooking.createContext("/api/v1/reservas", ex -> {
            seenByBooking.set(ex.getRequestHeaders().getFirst("traceparent"));
            keySeenByBooking.set(ex.getRequestHeaders().getFirst("Idempotency-Key"));
            ex.sendResponseHeaders(201, -1);
            ex.close();
        });
        fakeBooking.start();
        workflow = new WorkflowApp(0, "http://localhost:" + fakeBooking.getAddress().getPort());
        workflow.start();
    }

    @AfterEach
    void stop() {
        workflow.stop();
        fakeBooking.stop(0);
    }

    @Test
    void traceIdReceivedIsTheTraceIdSentToBooking() throws Exception {
        HttpResponse<String> response = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + workflow.port() + "/api/v1/sagas/reservar"))
                        .header("traceparent", INCOMING)
                        .POST(HttpRequest.BodyPublishers.ofString("{\"propiedadId\":\"prop-118\"}"))
                        .build(),
                HttpResponse.BodyHandlers.ofString());

        assertEquals(201, response.statusCode());
        String downstream = seenByBooking.get();
        assertEquals(TRACE_ID, downstream.split("-")[1], "booking-service must get the same trace_id");
        assertNotEquals("1a2b3c4d5e6f7081", downstream.split("-")[2], "the outbound call needs its own span_id");
        assertEquals(TRACE_ID, response.headers().firstValue("traceparent").orElseThrow().split("-")[1]);
        assertTrue(keySeenByBooking.get().endsWith(":crear-reserva"));

        String metrics = HttpClient.newHttpClient().send(
                HttpRequest.newBuilder(URI.create("http://localhost:" + workflow.port() + "/metrics")).build(),
                HttpResponse.BodyHandlers.ofString()).body();
        assertTrue(metrics.contains("saga_reservar_requests_total{route=\"/api/v1/sagas/reservar\",outcome=\"completed\"} 1"));
        assertTrue(metrics.contains("saga_reservar_duration_seconds_count 1"));
    }
}
