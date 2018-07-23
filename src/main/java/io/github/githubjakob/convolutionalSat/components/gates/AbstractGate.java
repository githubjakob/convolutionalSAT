package io.github.githubjakob.convolutionalSat.components.gates;

import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.MicrotickVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGate implements Gate {
    public List<Clause> getMicrotickCnf(int numberOfGates) {
        List<Clause> allClauses = new ArrayList<>();

        // wenn sie gesetzt ist, dann müssen alle Stellen davor auch gesezt seind
        for (int i = 1; i < numberOfGates; i++) {

            Variable isFalse = new MicrotickVariable(i, false, this);

            Clause sanitiyReqForUniaryMicrotick = new Clause();
            sanitiyReqForUniaryMicrotick.addVariables(isFalse);
            Variable antecessorIsTrue = new MicrotickVariable(i-1, true, this);
            allClauses.add(new Clause(isFalse, antecessorIsTrue));
        }

        return allClauses;
    }
}
