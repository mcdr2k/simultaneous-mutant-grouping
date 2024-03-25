package model;

import java.util.List;

public class ProgramOutput {
    public int splitSize;
    public int splits;
    public SolverResult solverResult;
    public Timer timer;

    public ProgramOutput(SolverResult solverResult, Timer timer, int splitSize, int splits) {
        this.solverResult = solverResult;
        this.timer = timer;
        this.splitSize = splitSize;
        this.splits = splits;
    }
}
