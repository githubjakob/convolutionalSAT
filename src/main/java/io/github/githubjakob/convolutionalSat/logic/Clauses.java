package io.github.githubjakob.convolutionalSat.logic;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jakob on 07.06.18.
 */
public class Clauses {

    int tick;

    // CNF, Clauses are connected with "and"

    private Set<Clause> clauses;

    public Clauses(int tick, Clause... clauses) {
        this.tick = tick;
        if (clauses.length == 0) {
            this.clauses = new HashSet<>();
        }
        this.clauses = new HashSet<>(Arrays.asList(clauses));
    }

    public void addClause(Clause clause) {
        this.clauses.add(clause);
    }

    public void addAllClauses(Clauses clauses) {
        this.clauses.addAll(clauses.clauses);
    }

    public Set<Clause> getClauses() {
        return this.clauses;
    }

    public int getTick() {
        return this.tick;
    }
}
