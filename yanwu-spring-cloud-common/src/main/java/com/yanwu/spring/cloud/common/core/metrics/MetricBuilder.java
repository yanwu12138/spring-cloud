package com.yanwu.spring.cloud.common.core.metrics;

/**
 * The metric name builder and a lot of handy metric name part constants.
 */
public final class MetricBuilder {

    private static final char NAME_DELIMITER = '.';

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Common used metric name parts - Components
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public static final String SYSTEM = "system";
    public static final String ENGINE = "engine";
    public static final String OVERALL = "overall";
    public static final String DB = "db";
    public static final String WEB = "web";
    public static final String REDIS = "redis";
    public static final String MQ = "mq";
    public static final String RABBITMQ = "rabbitmq";
    public static final String DISPATCHER = "dispatcher";
    public static final String DEVICE = "device";
    public static final String HTTP = "http";
    public static final String USER = "user";
    public static final String MESSAGE = "message";
    public static final String EVENT = "event";
    public static final String TASK = "task";
    public static final String JOB = "job";
    public static final String THREAD = "thread";
    public static final String THREAD_POOL = "thread_pool";

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Common used metric name parts - Actions
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public static final String RUN = "run";
    public static final String CALL = "call";
    public static final String EXECUTE = "execute";
    public static final String PROCESSING = "processing";
    public static final String SCHEDULE = "schedule";
    public static final String DISPATCH = "dispatch";
    public static final String LOGIN = "login";
    public static final String LOGOUT = "logout";
    public static final String REQUEST = "request";
    public static final String RESPONSE = "response";

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Common used metric name parts - Functions
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public static final String TOTAL = "total";
    public static final String MEAN = "mean";
    public static final String AVG = "avg";
    public static final String MAX = "max";
    public static final String MIN = "min";
    public static final String CURRENT = "current";
    public static final String FIRST = "first";
    public static final String LAST = "last";

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Common used metric name parts - Directions
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public static final String RX = "rx";
    public static final String TX = "tx";
    public static final String IN = "in";
    public static final String OUT = "out";
    public static final String FROM = "from";
    public static final String TO = "to";

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Common used metric name parts - Units
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public static final String SIZE = "size";
    public static final String COUNT = "count";
    public static final String BYTES = "bytes";
    public static final String PACKAGES = "packages";
    public static final String CALLS = "calls";
    public static final String HITS = "hits";
    public static final String USERS = "users";
    public static final String MESSAGES = "messages";
    public static final String EVENTS = "events";
    public static final String TASKS = "tasks";
    public static final String REQUESTS = "requests";
    public static final String RESPONSES = "responses";
    public static final String ERRORS = "errors";
    public static final String THREADS = "threads";

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Common used metric name parts - Status
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public static final String RESULT = "result";
    public static final String SUCCESS = "success";
    public static final String FAILURE = "failure";
    public static final String ERROR = "error";
    public static final String COMPLETED = "completed";
    public static final String SCHEDULED = "scheduled";
    public static final String ACTIVE = "active";
    public static final String INACTIVE = "inactive";

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Common used metric name parts - Metrics
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    public static final String TIME = "time";
    public static final String UPTIME = "uptime";
    public static final String LATENCY = "latency";
    public static final String PROCESSING_TIME = name(PROCESSING, TIME);
    public static final String RESPONSE_TIME = name(RESPONSE, TIME);
    public static final String THROUGHPUT = "throughput";
    public static final String LOAD = "load";

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * Common used metric name parts - Composed Metrics
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    // system
    public static final String SYSTEM_UPTIME = name(SYSTEM, UPTIME);
    public static final String SYSTEM_PROCESSING_TIME = name(SYSTEM, PROCESSING_TIME);

    // thread pool
    public static final String THREAD_POOL_THREADS_ACTIVE = name(THREAD_POOL, THREADS, ACTIVE);
    public static final String THREAD_POOL_SIZE_CURRENT = name(THREAD_POOL, SIZE, CURRENT);
    public static final String THREAD_POOL_SIZE_LARGEST = name(THREAD_POOL, SIZE, "largest");
    public static final String THREAD_POOL_SIZE_CORE = name(THREAD_POOL, SIZE, "core");
    public static final String THREAD_POOL_SIZE_MAX = name(THREAD_POOL, SIZE, MAX);
    public static final String THREAD_POOL_TASKS_SCHEDULED = name(THREAD_POOL, TASKS, SCHEDULED);
    public static final String THREAD_POOL_TASKS_COMPLETED = name(THREAD_POOL, TASKS, COMPLETED);

    /*
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     *
     * END of name part constants
     *
     * ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
     */

    private MetricBuilder() {
    }

    /**
     * Concatenates elements to form a dotted name, eliding any null values or empty strings.
     *
     * @param name  the first element of the name
     * @param names the remaining elements of the name
     * @return {@code name} and {@code names} concatenated by periods
     */
    public static String name(final String name, final String... names) {
        final StringBuilder builder = new StringBuilder();
        append(builder, name);
        if (names != null) {
            for (String s : names) {
                append(builder, s);
            }
        }
        return builder.toString();
    }

    /**
     * Concatenates a class name and elements to form a dotted name, eliding any null values or
     * empty strings.
     *
     * @param clazz the first element of the name
     * @param names the remaining elements of the name
     * @return {@code clazz} and {@code names} concatenated by periods
     */
    public static String name(final Class<?> clazz, final String... names) {
        return name(clazz.getName(), names);
    }

    private static void append(final StringBuilder builder, final String part) {
        if (part == null || part.isEmpty()) {
            return;
        }
        if (builder.length() > 0) {
            builder.append(NAME_DELIMITER);
        }
        builder.append(part);
    }

}
