package model;

public class Mutant {
    public String id;
    public String[] tests;

    public Mutant(String id, String... tests) {
        this.id = id;
        this.tests = tests;
    }
}
