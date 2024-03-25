import com.google.gson.Gson;
import model.Converter;
import model.ProgramOutput;
import model.SolverInput;
import model.TimedSolverResult;
import solver.CpModelSolver;
import solver.SplitSolver;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.util.List;

public class Main {
    private static final int SPLIT_SIZE = 150;
    private static final boolean EXECUTE_SAMPLE = false;

    public static void main(String[] args) throws IOException {
        if (EXECUTE_SAMPLE) {
            sample();
            return;
        }

        var inputFile = getInputFile(args);
        var outputFile = getOutputFile(args);

        var splitSize = getSplitSize(args);
        var solverInput = Converter.toSolverInput(inputFile, splitSize);
        var result = solve(solverInput);
        ProgramOutput output = new ProgramOutput(result.getSolverResult(), result.getTimer(), splitSize, solverInput.size());

        Gson gson = new Gson();
        String jsonOutput = gson.toJson(output);

        var writer = new FileWriter(outputFile);
        try (writer) {
            writer.write(jsonOutput);
            writer.flush();
        }
    }

    private static final int TEST_CAP = 10;
    private static void sample() {
        solve(new boolean[][]{
                bools(0, 1, 2),
                bools(3, 4, 5),
                bools(6, 7, 8),
                bools(9, 0, 1),
                bools(2, 4, 6),
                bools(3, 5, 7),
                bools(7, 8, 9),
                bools(1, 2, 9),
                bools(9),
                bools(4, 7),
                bools(5, 7),
                bools(1, 2, 4, 7, 9),
                bools(2, 3),
                bools(0),
        });
    }

    private static File getInputFile(String[] args) throws FileNotFoundException {
        var file = argumentToFile(args, 0);
        if (!file.exists()) {
            throw new FileNotFoundException(args[0]);
        }
        if (file.isDirectory()) {
            throw new RuntimeException(String.format("Path '%s' points to a directory but a file is required", args[0]));
        }
        return file;
    }

    private static File getOutputFile(String[] args) throws FileNotFoundException {
        try {
            var file = argumentToFile(args, 1);
            if (file.exists()) {
                //throw new FileAlreadyExistsException(String.format("File '%s' already exists", args[1]));
            }
            return file;
        } catch (RuntimeException e) {
            // allow output file to not exist, then default to the same directory as the input
            var inputFile = getInputFile(args);
            var outputDirectory = inputFile.getParentFile();
            return new File(outputDirectory, "grouped-mutants.json");
        }
    }

    private static File argumentToFile(String[] args, int fileIndex) {
        if (args == null || args.length < fileIndex + 1 || args[fileIndex] == null || args[fileIndex].trim().isEmpty()) {
            throw new RuntimeException("No path found in arguments");
        }
        return new File(args[fileIndex]);
    }

    private static int getSplitSize(String[] args) {
        if (args == null || args.length < 3 || args[2] == null || args[2].trim().isEmpty()) {
            return SPLIT_SIZE;
        }
        return Integer.parseInt(args[2]);
    }

    private static boolean[] bools(int... reachableTests) {
        boolean[] result = new boolean[TEST_CAP];
        for (int i : reachableTests)
            result[i] = true;
        return result;
    }

    private static TimedSolverResult solve(List<SolverInput> solverInput) {
        if (solverInput.isEmpty()) {
            throw new RuntimeException("No input provided");
        }

        return new SplitSolver(new CpModelSolver()).solve(solverInput);
    }

    private static void solve(boolean[][] input) {
        solve(List.of(new SolverInput(input)));
    }
}