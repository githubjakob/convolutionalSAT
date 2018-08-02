package io.github.githubjakob.convolutionalSat.components.gates;

import com.google.inject.AbstractModule;
import com.google.inject.Inject;
import io.github.githubjakob.convolutionalSat.components.ComponentFactory;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.MicrotickVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

public abstract class AbstractGate implements Gate {

    private Module module = null;

    @Override
    public void setModule(Module module){
        this.module = module;
    }

    @Override
    public Module getModule() {
        return module;
    }

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
