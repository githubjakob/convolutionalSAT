package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import io.github.githubjakob.convolutionalSat.logic.MicrotickVariable;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakob on 10.07.18.
 */
public abstract class AbstractGate implements Gate {
    public List<Clause> getMicrotickCnf(int numberOfGates) {
        List<Clause> allClauses = new ArrayList<>();

        // wenn sie gesetzt ist, dann müssen alle Stellen davor auch gesezt seind
        for (int i = 1; i < numberOfGates; i++) {

            ConnectionVariable isFalse = new MicrotickVariable(i, false, this);

            Clause sanitiyReqForUniaryMicrotick = new Clause();
            sanitiyReqForUniaryMicrotick.addVariables(isFalse);
            ConnectionVariable antecessorIsTrue = new MicrotickVariable(i-1, true, this);
            allClauses.add(new Clause(isFalse, antecessorIsTrue));
        }

        return allClauses;
    }
}
