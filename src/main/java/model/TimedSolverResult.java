package model;

import java.util.List;

public class TimedSolverResult {
    private final SolverResult solverResult;
    private final Timer timer;

    public TimedSolverResult(SolverResult solverResult, Timer timer) {
        this.solverResult = solverResult;
        this.timer = timer;
    }

    public SolverResult getSolverResult() {
        return solverResult;
    }

    public Timer getTimer() {
        return timer;
    }

    public static TimedSolverResult aggregate(List<TimedSolverResult> results) {
        return new TimedSolverResult(
                SolverResult.aggregate(results.stream().map(r -> r.solverResult).toList()),
                Timer.aggregate(results.stream().map(r -> r.timer).toList())
        );
    }
}
