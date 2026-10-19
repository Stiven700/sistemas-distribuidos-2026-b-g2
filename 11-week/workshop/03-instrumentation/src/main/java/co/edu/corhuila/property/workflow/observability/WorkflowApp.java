package co.edu.corhuila.property.workflow.observability;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.UUID;

/** Minimal property-workflow: POST /api/v1/sagas/reservar runs step crear-reserva. */
public class WorkflowApp {

    private final HttpServer server;
    private final Metrics metrics = new Metrics();

    public WorkflowApp(int port, String bookingUrl) throws IOException {
        BookingClient booking = new BookingClient(bookingUrl);
        server = HttpServer.create(new InetSocketAddress(port), 0);
        server.createContext("/api/v1/sagas/reservar", ex -> reservar(ex, booking))
                .getFilters().add(new TraceFilter());
        server.createContext("/metrics", ex -> send(ex, 200, metrics.render()));
    }

    private void reservar(HttpExchange ex, BookingClient booking) throws IOException {
        long start = System.nanoTime();
        String sagaId = UUID.randomUUID().toString();
        String body = new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
        JsonLogger.info("saga.step.started", "saga reservar started",
                Map.of("saga_id", sagaId, "saga_step", "crear-reserva"));
        String outcome;
        int status;
        try {
            int bookingStatus = booking.crearReserva(sagaId, body);
            outcome = bookingStatus == 201 ? "completed" : "compensated";
            status = bookingStatus == 201 ? 201 : 422;
        } catch (IOException | InterruptedException e) {
            JsonLogger.error("booking.request.failed", "booking-service did not answer",
                    Map.of("saga_id", sagaId, "saga_step", "crear-reserva"));
            outcome = "failed";
            status = 500;
        }
        metrics.record(outcome, (System.nanoTime() - start) / 1e9);
        send(ex, status, "{\"sagaId\":\"" + sagaId + "\",\"status\":\"" + outcome.toUpperCase() + "\"}");
    }

    private static void send(HttpExchange ex, int status, String body) throws IOException {
        byte[] bytes = body.getBytes(StandardCharsets.UTF_8);
        ex.sendResponseHeaders(status, bytes.length);
        ex.getResponseBody().write(bytes);
        ex.close();
    }

    public void start() { server.start(); }
    public void stop() { server.stop(0); }
    public int port() { return server.getAddress().getPort(); }

    public static void main(String[] args) throws IOException {
        String bookingUrl = System.getenv().getOrDefault("BOOKING_URL", "http://localhost:3001");
        WorkflowApp app = new WorkflowApp(3006, bookingUrl);
        app.start();
        System.out.println("property-workflow listening on :3006, booking at " + bookingUrl);
    }
}
