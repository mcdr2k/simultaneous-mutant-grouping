package model;

import java.time.Duration;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Timer {
    private final long setupAxiomsDurationNanos;
    private final long setupVariablesDurationNanos;
    private final long setupConstraintsDurationNanos;
    private final long createObjectiveDurationNanos;
    private final long solveDurationNanos;
    private final long totalDurationNanos;

    public Timer(long setupAxiomsDurationNanos, long setupVariablesDurationNanos, long setupConstraintsDurationNanos, long createObjectiveDurationNanos, long solveDurationNanos) {
        this.setupAxiomsDurationNanos = setupAxiomsDurationNanos;
        this.setupVariablesDurationNanos = setupVariablesDurationNanos;
        this.setupConstraintsDurationNanos = setupConstraintsDurationNanos;
        this.createObjectiveDurationNanos = createObjectiveDurationNanos;
        this.solveDurationNanos = solveDurationNanos;
        this.totalDurationNanos = setupAxiomsDurationNanos + setupVariablesDurationNanos + setupConstraintsDurationNanos + createObjectiveDurationNanos + solveDurationNanos;
    }

    public static long time(Runnable runnable) {
        long startTime = System.nanoTime();
        runnable.run();
        long endTime = System.nanoTime();

        return (endTime - startTime);
    }

    public static Timer aggregate(List<Timer> timers) {
        return new Timer(
                timers.stream().map(t -> t.setupAxiomsDurationNanos).reduce(0L, Long::sum),
                timers.stream().map(t -> t.setupVariablesDurationNanos).reduce(0L, Long::sum),
                timers.stream().map(t -> t.setupConstraintsDurationNanos).reduce(0L, Long::sum),
                timers.stream().map(t -> t.createObjectiveDurationNanos).reduce(0L, Long::sum),
                timers.stream().map(t -> t.solveDurationNanos).reduce(0L, Long::sum)
        );
    }

    public long setupAxiomsDuration(TimeUnit tu) {
        return tu.convert(Duration.ofNanos(setupAxiomsDurationNanos));
    }

    public long setupVariablesDuration(TimeUnit tu) {
        return tu.convert(Duration.ofNanos(setupVariablesDurationNanos));
    }
    public long setupConstraintsDuration(TimeUnit tu) {
        return tu.convert(Duration.ofNanos(setupConstraintsDurationNanos));
    }
    public long createObjectiveDuration(TimeUnit tu) {
        return tu.convert(Duration.ofNanos(createObjectiveDurationNanos));
    }
    public long solveDuration(TimeUnit tu) {
        return tu.convert(Duration.ofNanos(solveDurationNanos));
    }

    public long totalDuration(TimeUnit tu) {
        return tu.convert(Duration.ofNanos(totalDurationNanos));
    }

    @Override
    public String toString() {
        return String.join(
                System.lineSeparator(),
                "Timed durations:",
                toString("Setup axioms", humanReadableFormat(setupAxiomsDurationNanos)),
                toString("Setup variables", humanReadableFormat(setupVariablesDurationNanos)),
                toString("Setup constraints", humanReadableFormat(setupConstraintsDurationNanos)),
                toString("Create objective", humanReadableFormat(createObjectiveDurationNanos)),
                toString("Solving", humanReadableFormat(solveDurationNanos)),
                toString("Total", humanReadableFormat(totalDurationNanos))
        );
    }

    private static String toString(String variable, String duration) {
        return variable + ": " + duration;
    }

    public static String humanReadableFormat(long durationNanos) {
        long convertToSeconds = (long) Math.pow(10, 9);
        long convertToMillis = (long) Math.pow(10, 6);

        if (convertToSeconds <= durationNanos) {
            double durationSeconds = (double) durationNanos / convertToSeconds;
            return durationSeconds + " seconds";
        } else {
            return durationNanos / convertToMillis + " milliseconds";
        }
    }

    public long getSetupAxiomsDurationNanos() {
        return setupAxiomsDurationNanos;
    }

    public long getSetupVariablesDurationNanos() {
        return setupVariablesDurationNanos;
    }

    public long getSetupConstraintsDurationNanos() {
        return setupConstraintsDurationNanos;
    }

    public long getCreateObjectiveDurationNanos() {
        return createObjectiveDurationNanos;
    }

    public long getSolveDurationNanos() {
        return solveDurationNanos;
    }

    public long getTotalDurationNanos() {
        return totalDurationNanos;
    }
}
