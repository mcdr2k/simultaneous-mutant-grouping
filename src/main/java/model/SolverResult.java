package model;

import java.util.List;

public class SolverResult {
    public final SolverStatus status;
    public final FeasibleSolution solution;

    public SolverResult(SolverStatus status, FeasibleSolution solution) {
        this.status = status;
        this.solution = solution;
    }

    public static SolverResult aggregate(List<SolverResult> results) {
        return new SolverResult(
                SolverStatus.aggregate(results.stream().map(r -> r.status).toList()),
                FeasibleSolution.aggregate(results.stream().map(r -> r.solution).toList())
        );
    }
}
