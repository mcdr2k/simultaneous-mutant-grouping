package model;

public class ProgramInput {
    public Mutant[] mutants;
    public boolean smartBail;
    public int fixedSize;
    public int maximumSize;

    public ProgramInput() {

    }

    public ProgramInput(ProgramInput input) {
        this.mutants = input.mutants;
        this.smartBail = input.smartBail;
        this.fixedSize = input.fixedSize;
        this.maximumSize = input.maximumSize;
    }
}
