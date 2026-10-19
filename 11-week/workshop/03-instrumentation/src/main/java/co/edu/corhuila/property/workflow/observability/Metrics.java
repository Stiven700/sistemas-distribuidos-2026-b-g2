package co.edu.corhuila.property.workflow.observability;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.DoubleAdder;

/** The two CP3 metrics of the Saga reservar, in Prometheus text format. */
public final class Metrics {

    /** Seconds. The guest waits for the whole Saga, so the buckets go up to 10 s. */
    static final double[] BUCKETS = {0.1, 0.25, 0.5, 1, 2, 3, 5, 10};

    private final Map<String, AtomicLong> requestsByOutcome = new ConcurrentHashMap<>();
    private final AtomicLong[] bucketCounts = new AtomicLong[BUCKETS.length];
    private final AtomicLong count = new AtomicLong();
    private final DoubleAdder sum = new DoubleAdder();

    public Metrics() {
        for (int i = 0; i < BUCKETS.length; i++) bucketCounts[i] = new AtomicLong();
    }

    /** outcome: completed, compensated, failed, error. Fixed list: low cardinality. */
    public void record(String outcome, double seconds) {
        requestsByOutcome.computeIfAbsent(outcome, k -> new AtomicLong()).incrementAndGet();
        for (int i = 0; i < BUCKETS.length; i++) {
            if (seconds <= BUCKETS[i]) bucketCounts[i].incrementAndGet();
        }
        count.incrementAndGet();
        sum.add(seconds);
    }

    public String render() {
        StringBuilder sb = new StringBuilder();
        sb.append("# HELP saga_reservar_requests_total Saga reservar requests by outcome\n");
        sb.append("# TYPE saga_reservar_requests_total counter\n");
        requestsByOutcome.forEach((outcome, n) -> sb.append("saga_reservar_requests_total{route=\"/api/v1/sagas/reservar\",outcome=\"")
                .append(outcome).append("\"} ").append(n.get()).append('\n'));
        sb.append("# HELP saga_reservar_duration_seconds Saga reservar latency\n");
        sb.append("# TYPE saga_reservar_duration_seconds histogram\n");
        for (int i = 0; i < BUCKETS.length; i++) {
            sb.append("saga_reservar_duration_seconds_bucket{le=\"").append(BUCKETS[i]).append("\"} ")
                    .append(bucketCounts[i].get()).append('\n');
        }
        sb.append("saga_reservar_duration_seconds_bucket{le=\"+Inf\"} ").append(count.get()).append('\n');
        sb.append("saga_reservar_duration_seconds_sum ").append(sum.sum()).append('\n');
        sb.append("saga_reservar_duration_seconds_count ").append(count.get()).append('\n');
        return sb.toString();
    }
}
