package model;

import java.beans.Transient;
import java.util.Objects;

public class SolverInput {
    public boolean[][] reachabilityMatrix;
    public String[] mutantIds;
    public boolean smartBail;
    public int fixedGroupSize;
    public int maxGroupSize;
    public int testSuiteSize;

    public SolverInput() {
        this(new boolean[0][]);
    }

    public SolverInput(boolean[][] reachabilityMatrix) {
        this(Objects.requireNonNull(reachabilityMatrix), null, false, -1, -1, 0);
    }

    public SolverInput(boolean[][] reachabilityMatrix, String[] mutantIds, boolean smartBail, int fixedSize, int maximumSize, int testSuiteSize) {
        this.reachabilityMatrix = reachabilityMatrix;
        this.mutantIds = mutantIds;
        this.smartBail = smartBail;
        this.fixedGroupSize = fixedSize;
        this.maxGroupSize = maximumSize;
        this.testSuiteSize = testSuiteSize;
    }

    @Transient
    public String getMutantId(int mutantIndex) {
        if (mutantIds == null) return Integer.toString(mutantIndex);
        return mutantIds[mutantIndex];
    }
}
