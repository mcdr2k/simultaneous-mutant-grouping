package model;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.*;

public class Converter {
    private Converter() {

    }

    public static List<SolverInput> toSolverInput(File inputFile, int splitSize) throws FileNotFoundException {
        if (inputFile == null || !inputFile.exists() || inputFile.isDirectory()) {
            throw new RuntimeException("Invalid file provided");
        }
        Gson gson = new Gson();
        ProgramInput input = gson.fromJson(new FileReader(inputFile), ProgramInput.class);
        return toSplitSolverInput(input, splitSize);
    }

    public static List<SolverInput> toSplitSolverInput(ProgramInput input, int splitSize) {
        System.out.println("Total mutant input: " + input.mutants.length);
        splitSize = splitSize <= 0 ? Integer.MAX_VALUE : splitSize;
        var result = new ArrayList<SolverInput>();
        final Mutant[] mutants = input.mutants;

        int i = 0;
        while (i < mutants.length) {
            final int M = Math.min(mutants.length - i, splitSize);
            var splitInput = new ProgramInput(input);
            splitInput.mutants = Arrays.copyOfRange(mutants, i, i + M, Mutant[].class);
            result.add(toSolverInput(splitInput));
            i += M;
        }

        return result;
    }

    public static SolverInput toSolverInput(ProgramInput input) {
        Objects.requireNonNull(input);
        var matrix = toReachabilityMatrix(input.mutants);
        return new SolverInput(
                matrix,
                Arrays.stream(input.mutants).map(mutant -> mutant.id).toArray(String[]::new),
                input.smartBail,
                input.fixedSize,
                input.maximumSize,
                matrix.length != 0 ? matrix[0].length : 0
        );
    }

    public static boolean[][] toReachabilityMatrix(Mutant[] mutants) {
        Objects.requireNonNull(mutants);
        final int M = mutants.length;

        if (M == 0) return new boolean[0][];

        Set<String> uniqueTests = new HashSet<>();
        List<String> orderedUniqueTests = new ArrayList<>();
        for (Mutant mutant : mutants) {
            for (String test : mutant.tests) {
                if (uniqueTests.add(test)) {
                    orderedUniqueTests.add(test);
                }
            }
            //uniqueTests.addAll(Arrays.asList(mutant.tests));
        }

        final int T = uniqueTests.size();
        final String[] tests = orderedUniqueTests.toArray(String[]::new);
        boolean[][] reachabilityMatrix = new boolean[M][];
        for (int i = 0; i < M; i++) {
            reachabilityMatrix[i] = bools(tests, mutants[i].tests, T);
        }

        return reachabilityMatrix;
    }

    private static boolean[] bools(String[] tests, String[] reachableTests, int testCap) {
        boolean[] result = new boolean[testCap];
        for (String test : reachableTests)
            result[indexOf(tests, test)] = true;
        return result;
    }

    private static int indexOf(String[] array, String value) {
        for (int i = 0; i < array.length; i++) {
            if (Objects.equals(array[i], value)) return i;
        }
        return -1;
    }
}
