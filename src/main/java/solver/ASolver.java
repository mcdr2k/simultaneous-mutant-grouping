package solver;

import model.Timer;
import model.SolverInput;
import model.TimedSolverResult;
import model.SolverResult;

public abstract class ASolver implements Solver {
    protected SolverInput input;

    @Override
    public TimedSolverResult solve(SolverInput input) {
        this.input = input;
        Timer timer = new Timer(
                Timer.time(this::setupAxioms),
                Timer.time(this::setupVariables),
                Timer.time(this::setupConstraints),
                Timer.time(this::createObjective),
                Timer.time(this::solve)
                );
        var result = formulateResult();
        return new TimedSolverResult(result, timer);
    }

    protected abstract void setupAxioms();

    protected abstract void setupVariables();

    protected abstract void setupConstraints();

    protected abstract void createObjective();

    protected abstract void solve();

    protected abstract SolverResult formulateResult();
}
