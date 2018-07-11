package io.github.githubjakob.convolutionalSat.logic;

import java.util.*;

/**
 * Created by jakob on 07.06.18.
 */
public class Clause {

    // CNF, Variables are connected with or

    Set<Variable> variables;

    public Clause(Variable... variables) {
        this.variables = new HashSet<>(Arrays.asList(variables));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        for (Variable variable : variables) {
            index++;
            stringBuilder.append(variable.toString());
            if (index != variables.size()) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public Set<Variable> getVariables() {
        return this.variables;
    }

    public void addVariable(Variable variable) {
        this.variables.add(variable);
    }

    public void addVariables(Variable... variables) {
        this.variables.addAll(Arrays.asList(variables));
    }
}
