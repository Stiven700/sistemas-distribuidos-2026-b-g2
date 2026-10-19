package co.edu.corhuila.property.workflow.observability;

/** Holds the X-Correlation-Id of the request that started the Saga. */
public final class Correlation {

    private static final ThreadLocal<String> CURRENT = new ThreadLocal<>();

    private Correlation() { }

    public static void set(String id) { CURRENT.set(id); }
    public static String current() { return CURRENT.get(); }
    public static void clear() { CURRENT.remove(); }
}
