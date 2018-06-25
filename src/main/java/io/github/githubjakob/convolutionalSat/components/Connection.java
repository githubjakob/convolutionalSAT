package io.github.githubjakob.convolutionalSat.components;


import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;

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

    public List<Clause> convertToCnfAtTick(BitStream bitStream) {
        int bitstreamId = bitStream.getId();
        List<Clause> clausesForAllTicks = new ArrayList<>();

        int bits = bitStream.getLength();
        for (int tick = 0; tick < bits; tick++) {

            Variable connectionNotSet = new Variable(false, this);

            TimeDependentVariable inputTrue = new TimeDependentVariable(tick, bitstreamId, true, from);
            TimeDependentVariable inputFalse = new TimeDependentVariable(tick, bitstreamId, false, from);

            TimeDependentVariable outputTrue = new TimeDependentVariable(tick, bitstreamId, true, to);
            TimeDependentVariable outputFalse = new TimeDependentVariable(tick, bitstreamId, false, to);

            Clause clause1 = new Clause(inputFalse, outputTrue, connectionNotSet);
            Clause clause2 = new Clause(inputTrue, outputFalse, connectionNotSet);
            clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));
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
}
