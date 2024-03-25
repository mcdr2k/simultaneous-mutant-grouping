package solver;

import model.SolverInput;
import model.TimedSolverResult;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class SplitSolver {
    private final Solver solver;

    public SplitSolver(Solver solver) {
        this.solver = Objects.requireNonNull(solver);
    }

    public TimedSolverResult solve(List<SolverInput> inputs) {
        if (inputs.size() > 1) {
            System.out.printf("Input was too large, it will be split over %s parts of ~%s mutants%n", inputs.size(), inputs.get(0).mutantIds.length);
        }

        List<TimedSolverResult> results = new ArrayList<>(inputs.size());
        for (var input : inputs) {
            System.out.println("M: " + input.reachabilityMatrix.length);
            System.out.println("T: " + input.reachabilityMatrix[0].length);

            var result = this.solver.solve(input);
            System.out.println(result.getTimer());
            results.add(result);
        }

        var result = TimedSolverResult.aggregate(results);

        if (inputs.size() > 1) {
            System.out.println("Aggregated result:");
            System.out.println("Status " + result.getSolverResult().status);
            System.out.println("Total number of groups formed: " + result.getSolverResult().solution.getGroups().size());
            System.out.println(result.getTimer());
        }
        return result;
    }
}
