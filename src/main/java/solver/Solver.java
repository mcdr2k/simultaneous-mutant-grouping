package solver;

import model.SolverInput;
import model.TimedSolverResult;

public interface Solver {
    TimedSolverResult solve(SolverInput input);
}
