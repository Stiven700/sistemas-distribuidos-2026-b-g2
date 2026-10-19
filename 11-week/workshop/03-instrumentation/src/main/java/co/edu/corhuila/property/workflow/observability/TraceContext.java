package co.edu.corhuila.property.workflow.observability;

import java.security.SecureRandom;
import java.util.HexFormat;
import java.util.regex.Pattern;

/** W3C trace context of one hop of the Saga reservar: 00-<trace_id>-<span_id>-01. */
public record TraceContext(String traceId, String spanId) {

    private static final Pattern TRACEPARENT =
            Pattern.compile("^00-([0-9a-f]{32})-([0-9a-f]{16})-[0-9a-f]{2}$");
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final ThreadLocal<TraceContext> CURRENT = new ThreadLocal<>();

    /** Continues the caller's trace with a new span, or starts a new trace. */
    public static TraceContext fromHeader(String traceparent) {
        if (traceparent != null) {
            var m = TRACEPARENT.matcher(traceparent.trim());
            if (m.matches()) {
                return new TraceContext(m.group(1), randomHex(8));
            }
        }
        return new TraceContext(randomHex(16), randomHex(8));
    }

    /** Context for an outbound call: same trace_id, new span_id. */
    public TraceContext child() {
        return new TraceContext(traceId, randomHex(8));
    }

    public String toHeader() {
        return "00-" + traceId + "-" + spanId + "-01";
    }

    public static void set(TraceContext ctx) { CURRENT.set(ctx); }
    public static TraceContext current() { return CURRENT.get(); }
    public static void clear() { CURRENT.remove(); }

    private static String randomHex(int bytes) {
        byte[] b = new byte[bytes];
        RANDOM.nextBytes(b);
        return HexFormat.of().formatHex(b);
    }
}
