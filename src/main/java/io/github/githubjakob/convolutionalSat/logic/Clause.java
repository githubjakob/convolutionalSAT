package io.github.githubjakob.convolutionalSat.logic;

import java.util.*;

/**
 * Created by jakob on 07.06.18.
 */
public class Clause {

    // CNF, Variables are connected with or

    Set<ConnectionVariable> variables;

    public Clause(ConnectionVariable... variables) {
        this.variables = new HashSet<ConnectionVariable>(Arrays.asList(variables));
    }

    @Override
    public String toString() {
        StringBuilder stringBuilder = new StringBuilder();
        int index = 0;
        for (ConnectionVariable variable : variables) {
            index++;
            stringBuilder.append(variable.toString());
            if (index != variables.size()) {
                stringBuilder.append(", ");
            }
        }
        return stringBuilder.toString();
    }

    public Set<ConnectionVariable> getVariables() {
        return this.variables;
    }

    public void addVariable(ConnectionVariable variable) {
        this.variables.add(variable);
    }

    public void addVariables(ConnectionVariable... variables) {
        this.variables.addAll(Arrays.asList(variables));
    }
}
