package io.github.githubjakob.convolutionalSat.components.connection;

import io.github.githubjakob.convolutionalSat.components.InputPin;
import io.github.githubjakob.convolutionalSat.components.OutputPin;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.logic.*;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakob on 31.07.18.
 */
public abstract class AbstractConnection implements Connection {
    static int idCounter = 0;

    int id;

    @Getter
    OutputPin from;

    @Getter
    InputPin to;

    public List<Clause> convertMicroticksRequirement(int numberOfGates) {
        List<Clause> microtickClauses = new ArrayList<>();
        ConnectionVariable connectionNotSet = new ConnectionVariable(false, this);

        Gate fromGate = from.getGate();
        Gate toGate = to.getGate();

        if (toGate.getType().equals("input") || toGate.getType().equals("register")) {
            return microtickClauses;
        }


        /**
         *
         * A <=> (~B && C)
         * b ist größer gleich c (=A) gdw. microtick von b nicht gesetzt und von c gesetzt an stelle i
         * CNF
         * (~A || B ) && (~A || C) && (A || B || ~C)
         *
         *
         * außerdem:
         * A1 || A2 || .. || An
         *
         */


        // wenn die verbindung gestzt ist muss der Microtick von "from" kleiner als von "to" sein
        // für irgendeine Stelligkeit
        Clause biggerOrEqual = new Clause();
        microtickClauses.add(biggerOrEqual);
        biggerOrEqual.addVariable(connectionNotSet);
        for (int i = 0; i < numberOfGates; i++) {
            Variable fromVariableTrue = new MicrotickVariable(i, true, fromGate);
            Variable fromVariableFalse = new MicrotickVariable(i, false, fromGate);
            Variable toVariableTrue = new MicrotickVariable(i, true,to.getGate());
            Variable toVariableFalse = new MicrotickVariable(i, false,to.getGate());

            MicrotickGreaterVariable microtickGreaterVariableTrue = new MicrotickGreaterVariable(i, true, this);
            MicrotickGreaterVariable microtickGreaterVariableFalse = new MicrotickGreaterVariable(i, false, this);
            biggerOrEqual.addVariable(microtickGreaterVariableTrue);

            microtickClauses.add(new Clause(microtickGreaterVariableFalse, fromVariableFalse));
            microtickClauses.add(new Clause(microtickGreaterVariableFalse, toVariableTrue));
            microtickClauses.add(new Clause(microtickGreaterVariableTrue, fromVariableTrue, toVariableFalse));

        }
        return microtickClauses;
    }

    @Override
    public Module getModule() {
        return this.from.getModule();
    }

    @Override
    public String getType() {
        return "connections";
    }
}
