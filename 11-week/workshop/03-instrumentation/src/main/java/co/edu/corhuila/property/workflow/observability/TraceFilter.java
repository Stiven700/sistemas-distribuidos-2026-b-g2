package co.edu.corhuila.property.workflow.observability;

import com.sun.net.httpserver.Filter;
import com.sun.net.httpserver.HttpExchange;
import java.io.IOException;
import java.util.Map;
import java.util.UUID;

/**
 * Entry filter of property-workflow: reads or creates the trace context, returns it in
 * the response and logs the end of every request with its duration.
 */
public class TraceFilter extends Filter {

    @Override
    public void doFilter(HttpExchange exchange, Chain chain) throws IOException {
        long start = System.nanoTime();
        TraceContext ctx = TraceContext.fromHeader(exchange.getRequestHeaders().getFirst("traceparent"));
        String correlationId = exchange.getRequestHeaders().getFirst("X-Correlation-Id");
        TraceContext.set(ctx);
        Correlation.set(correlationId != null ? correlationId : "corr-" + UUID.randomUUID());
        exchange.getResponseHeaders().set("traceparent", ctx.toHeader());
        exchange.getResponseHeaders().set("X-Correlation-Id", Correlation.current());
        try {
            chain.doFilter(exchange);
        } finally {
            long durationMs = (System.nanoTime() - start) / 1_000_000;
            JsonLogger.info("http.request.completed",
                    exchange.getRequestMethod() + " " + exchange.getRequestURI().getPath(),
                    Map.of("status", exchange.getResponseCode(), "duration_ms", durationMs));
            TraceContext.clear();
            Correlation.clear();
        }
    }

    @Override
    public String description() {
        return "W3C trace context and structured request log";
    }
}
