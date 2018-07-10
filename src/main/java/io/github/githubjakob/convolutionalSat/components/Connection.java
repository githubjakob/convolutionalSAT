package io.github.githubjakob.convolutionalSat.components;


import io.github.githubjakob.convolutionalSat.logic.*;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class Connection implements Component {

    static int idCounter = 0;

    int id;

    private OutputPin from;

    private InputPin to;

    public Connection(OutputPin from, InputPin to) {
        this.id = idCounter++;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "C" + id;
    }

    public List<Clause> convertToCnfAtTick(int numberOfGates) {
        List<Clause> clausesForAllTicks = new ArrayList<>();
        ConnectionVariable connectionNotSet = new ConnectionVariable(false, this);

        for (BitStream bitStream : this.getModule().getBitstreams()) {
            int bitstreamId = bitStream.getId();
            int bits = bitStream.getLength();
            for (int tick = 0; tick < bits; tick++) {

                BitAtComponentVariable inputTrue = new BitAtComponentVariable(tick, bitstreamId, true, from);
                BitAtComponentVariable inputFalse = new BitAtComponentVariable(tick, bitstreamId, false, from);

                BitAtComponentVariable outputTrue = new BitAtComponentVariable(tick, bitstreamId, true, to);
                BitAtComponentVariable outputFalse = new BitAtComponentVariable(tick, bitstreamId, false, to);

                Clause clause1 = new Clause(inputFalse, outputTrue, connectionNotSet);
                Clause clause2 = new Clause(inputTrue, outputFalse, connectionNotSet);
                clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));
            }
        }

        Gate fromGate = from.getGate();
        Gate toGate = to.getGate();
        if (toGate.getType().equals("input") || toGate.getType().equals("register")) {
            return clausesForAllTicks;
        }

        // wenn die verbindung gestzt ist muss der Microtick von "from" kleiner als von "to" sein
        // für irgendeine Stelligkeit
        Clause biggerOrEqual = new Clause();
        clausesForAllTicks.add(biggerOrEqual);
        biggerOrEqual.addVariable(connectionNotSet);
        for (int i = 0; i < numberOfGates; i++) {
            ConnectionVariable fromVariableTrue = new MicrotickVariable(i, true, fromGate);
            ConnectionVariable fromVariableFalse = new MicrotickVariable(i, false, fromGate);
            ConnectionVariable toVariableTrue = new MicrotickVariable(i, true,to.getGate());
            ConnectionVariable toVariableFalse = new MicrotickVariable(i, false,to.getGate());

            BiggerOrEqualVariable biggerOrEqualVariableTrue = new BiggerOrEqualVariable(i, true, this);
            BiggerOrEqualVariable biggerOrEqualVariableFalse = new BiggerOrEqualVariable(i, false, this);
            biggerOrEqual.addVariable(biggerOrEqualVariableTrue);

            clausesForAllTicks.add(new Clause(biggerOrEqualVariableFalse, fromVariableFalse));
            clausesForAllTicks.add(new Clause(biggerOrEqualVariableFalse, toVariableTrue));
            //clausesForAllTicks.add(new Clause(biggerOrEqualVariableTrue, fromVariableTrue));
            //clausesForAllTicks.add(new Clause(biggerOrEqualVariableTrue, toVariableFalse));

        }


        return clausesForAllTicks;

    }

    public InputPin getTo() {
        return to;
    }

    public OutputPin getFrom() {
        return from;
    }

    @Override
    public String getType() {
        return "connection";
    }

    @Override
    public Module getModule() {
        return this.from.getModule();
    }
}
