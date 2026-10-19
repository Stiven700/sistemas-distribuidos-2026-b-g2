package co.edu.corhuila.property.workflow.observability;

import java.time.Instant;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

/** Writes the CP2 log contract: one JSON object per line on stdout. */
public final class JsonLogger {

    private static final String SERVICE = env("SERVICE_NAME", "property-workflow");
    private static final String VERSION = env("SERVICE_VERSION", "2.0.0");
    private static final String ENVIRONMENT = env("APP_ENV", "dev");
    /** CP2 "never log" list: dropped by name, whatever the caller passes. */
    private static final Set<String> NEVER_LOG =
            Set.of("tokenPago", "authorization", "password", "refreshToken", "email");

    private JsonLogger() { }

    public static void log(String level, String event, String message, Map<String, Object> fields) {
        Map<String, Object> line = new LinkedHashMap<>();
        line.put("timestamp", Instant.now().toString());
        line.put("level", level);
        line.put("service", SERVICE);
        line.put("version", VERSION);
        line.put("environment", ENVIRONMENT);
        TraceContext ctx = TraceContext.current();
        line.put("trace_id", ctx == null ? null : ctx.traceId());
        line.put("span_id", ctx == null ? null : ctx.spanId());
        line.put("correlation_id", Correlation.current());
        line.put("event", event);
        line.put("message", message);
        fields.forEach((k, v) -> { if (!NEVER_LOG.contains(k)) line.put(k, v); });
        System.out.println(toJson(line));
    }

    public static void info(String event, String message, Map<String, Object> fields) {
        log("INFO", event, message, fields);
    }

    public static void error(String event, String message, Map<String, Object> fields) {
        log("ERROR", event, message, fields);
    }

    private static String toJson(Map<String, Object> map) {
        StringBuilder sb = new StringBuilder("{");
        map.forEach((k, v) -> {
            if (sb.length() > 1) sb.append(',');
            sb.append('"').append(k).append("\":");
            if (v == null || v instanceof Number || v instanceof Boolean) sb.append(v);
            else sb.append('"').append(v.toString().replace("\\", "\\\\").replace("\"", "\\\"")).append('"');
        });
        return sb.append('}').toString();
    }

    private static String env(String name, String fallback) {
        String v = System.getenv(name);
        return v == null || v.isBlank() ? fallback : v;
    }
}
