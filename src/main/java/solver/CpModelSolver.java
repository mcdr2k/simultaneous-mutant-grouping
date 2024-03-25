package solver;

import com.google.ortools.Loader;
import com.google.ortools.sat.*;
import model.FeasibleSolution;
import model.SolverResult;
import model.SolverStatus;

import java.util.ArrayList;
import java.util.List;

public class CpModelSolver extends ASolver {
    private int M;
    private int T;
    private CpModel model;

    private Literal[][] overlaps;

    static {
        Loader.loadNativeLibraries();
    }

    @Override
    protected void setupAxioms() {
        var R = input.reachabilityMatrix;
        M = R.length;
        T = M > 0 ? R[0].length : 0;

        model = new CpModel();
        overlaps = new Literal[M][M];
        for (int m1 = 0; m1 < M; m1++) {
            // mutant always overlaps with itself
            overlaps[m1][m1] = model.trueLiteral();
            for (int m2 = m1 + 1; m2 < M; m2++) {
                for (int t = 0; t < T; t++) {
                    if (R[m1][t] && R[m2][t]) {
                        // overlaps(m1m2) -> overlaps(m2m1)
                        overlaps[m1][m2] = model.trueLiteral();
                        overlaps[m2][m1] = overlaps[m1][m2];
                        break;
                    }
                    // !overlaps(m1m2) -> !overlaps(m2m1)
                    overlaps[m1][m2] = model.falseLiteral();
                    overlaps[m2][m1] = overlaps[m1][m2];
                }
            }
        }
    }

    private BoolVar[][] groupMatrix;
    @Override
    protected void setupVariables() {
        groupMatrix = new BoolVar[M][M];
        // init variables
        for (int m = 0; m < M; m++) {
            for (int g = 0; g < M; g++) {
                groupMatrix[g][m] = model.newBoolVar("G" + g + "M" + m);
            }
        }
    }

    @Override
    protected void setupConstraints() {
        // each mutant belongs to exactly 1 group
        for (int m = 0; m < M; m++) {
            List<Literal> mutant = new ArrayList<>(M);
            for (int g = 0; g < M; g++) {
                mutant.add(groupMatrix[g][m]);
            }
            model.addExactlyOne(mutant);
        }

        // imposes a constraint on the maximum number of mutant within a group
        if (input.maxGroupSize > 0) {
            for (int g = 0; g < M; g++) {
                model.addLessOrEqual(LinearExpr.sum(groupMatrix[g]), input.maxGroupSize);
            }
        }

        if (input.fixedGroupSize == 1) {
            // this is useless, will yield identity matrix
            for (int g = 0; g < M; g++) {
                model.addEquality(LinearExpr.sum(groupMatrix[g]), 1);
            }
        } else if (input.fixedGroupSize > 1) {
            for (int g = 0; g < M; g++) {
                // sum = fixedGroupSize || sum = 1
                var sum = LinearExpr.sum(groupMatrix[g]);
                var reify = model.newBoolVar("reifyFixedGroup" + g);
                model.addLessOrEqual(sum, 1).onlyEnforceIf(reify);
                model.addEquality(sum, input.fixedGroupSize).onlyEnforceIf(reify.not());
            }
        }

        // every mutant that belongs to the same group should not have overlapping tests (unless it is the same mutant)
        for (int g = 0; g < M; g++) {
            for (int m1 = 0; m1 < M; m1++) {
                var l1 = groupMatrix[g][m1];
                for (int m2 = 0; m2 < M; m2++) {
                    if (m1 == m2) continue;
                    var l2 = groupMatrix[g][m2];
                    var hasOverlap = overlaps[m1][m2];
                    // if two mutants have overlapping tests, then at most 1 of them can be true
                    model.addLessOrEqual(
                                    LinearExpr.sum(new LinearArgument[]{l1, l2}), 1)
                            .onlyEnforceIf(hasOverlap);
                }
            }
        }
    }

    @Override
    protected void createObjective() {
        LinearExprBuilder obj = LinearExpr.newBuilder();
        for (int g = 0; g < M; g++) {
            var gHasMutants = model.newBoolVar("G" + g + "+");
            var gHasNoMutants = gHasMutants.not();
            var sum = LinearExpr.sum(groupMatrix[g]);
            model.addEquality(sum, 0).onlyEnforceIf(gHasNoMutants);
            model.addGreaterThan(sum, 0).onlyEnforceIf(gHasMutants);
            obj.addTerm(gHasMutants, 1);
        }

        model.minimize(obj);
    }

    private void addHints() {
        for (int g = 0; g < groupMatrix.length; g++) {
            for (int m = 0; m < groupMatrix.length; m++) {
                model.addHint(groupMatrix[g][m], g == m ? 1 : 0);
            }
        }
    }

    private CpSolver solver;
    private CpSolverStatus status;
    @Override
    protected void solve() {
        //this.addHints();
        solver = new CpSolver();
        solver.getParameters().setMaxTimeInSeconds(90);
        status = solver.solve(model);
    }

    @Override
    protected SolverResult formulateResult() {
        if (status == CpSolverStatus.OPTIMAL || status == CpSolverStatus.FEASIBLE) {
            System.out.println("Type: " + status + ", Objective function value: " + solver.objectiveValue());
            var groups = new ArrayList<List<String>>();
            for (int g = 0; g < M; g++) {
                var group = new ArrayList<String>();
                StringBuilder groupBitString = new StringBuilder("g" + g + ": ");
                for (int m = 0; m < M; m++) {
                    if (solver.booleanValue(groupMatrix[g][m])) {
                        group.add(input.getMutantId(m));
                        groupBitString.append('1');
                    } else {
                        groupBitString.append('0');
                    }
                }
                if (!group.isEmpty()) {
                    //System.out.println(groupBitString);
                    groups.add(group);
                }
            }
            return new SolverResult(SolverStatus.convert(status), new FeasibleSolution(groups));
        } else {
            System.err.println("No solution found.");
            return new SolverResult(SolverStatus.convert(status), null);
        }
    }
}
