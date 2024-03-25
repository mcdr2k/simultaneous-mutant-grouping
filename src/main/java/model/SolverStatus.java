package model;

import com.google.ortools.linearsolver.MPSolver;
import com.google.ortools.sat.CpSolverStatus;

import java.util.List;

public enum SolverStatus {
    /**
     * Indicates that the solver was able to find an optimal solution, no other solution can improve the objective score.
     */
    OPTIMAL,
    /**
     * Indicates that the solver was able to find a, possibly non-optimal, solution.
     */
    FEASIBLE,
    /**
     * Indicates that the solver was unable to find a solution yet but that there are still paths to consider.
     */
    UNKNOWN,
    /**
     * Indicates that the solver was unable to find any solution to the problem where it can satisfy
     * all constraints.
     */
    INFEASIBLE,
    /**
     * Indicates that the solver or the model to solve was configured improperly.
     */
    INVALID;

    /**
     * Indicates whether the solver was able to find any result (optimal or feasible solution)
     */
    public boolean hasSolution() {
        return this == FEASIBLE || this == OPTIMAL;
    }

    public boolean isValid() {
        return this != INVALID;
    }

    public static SolverStatus aggregate(List<SolverStatus> statuses) {
        var result = OPTIMAL;

        for (var status : statuses) {
            if (status.ordinal() > result.ordinal())
                result = status;
        }

        return result;
    }

    public static SolverStatus convert(CpSolverStatus status) {
        switch (status) {
            case UNKNOWN -> {
                return UNKNOWN;
            }
            case MODEL_INVALID, UNRECOGNIZED -> {
                return INVALID;
            }
            case FEASIBLE -> {
                return FEASIBLE;
            }
            case INFEASIBLE -> {
                return INFEASIBLE;
            }
            case OPTIMAL -> {
                return OPTIMAL;
            }
        }
        throw new RuntimeException("Unrecognized status: " + status);
    }

    public static SolverStatus convert(MPSolver.ResultStatus status) {
        switch (status) {
            case OPTIMAL -> {
                return OPTIMAL;
            }
            case FEASIBLE -> {
                return FEASIBLE;
            }
            case INFEASIBLE -> {
                return INFEASIBLE;
            }
            case UNBOUNDED -> {
                throw new RuntimeException("Cannot convert UNBOUNDED, unclear whether it has a solution");
            }
            case ABNORMAL, MODEL_INVALID -> {
                return INVALID;
            }
            case NOT_SOLVED -> {
                return UNKNOWN;
            }
        }
        throw new RuntimeException("Unrecognized status: " + status);
    }
}
