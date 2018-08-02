package io.github.githubjakob.convolutionalSat.components.connections;


import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class NoiseFreeConnection extends AbstractConnection {

    public NoiseFreeConnection(OutputPin from, InputPin to) {
        this.id = idCounter++;
        this.from = from;
        this.to = to;
    }

    @Override
    public String toString() {
        return "C" + id;
    }

    public List<Clause> convertToCnfAtTick(BitStream bitStream, int numberOfGates) {
        List<Clause> clausesForAllTicks = new ArrayList<>();
        ConnectionVariable connectionNotSet = new ConnectionVariable(false, this);

        int bitstreamId = bitStream.getId();
        int bits = bitStream.getLengthWithDelay();
        for (int tick = 0; tick < bits; tick++) {
            BitAtComponentVariable inputTrue = new BitAtComponentVariable(tick, bitstreamId, true, from);
            BitAtComponentVariable inputFalse = new BitAtComponentVariable(tick, bitstreamId, false, from);

            BitAtComponentVariable outputTrue = new BitAtComponentVariable(tick, bitstreamId, true, to);
            BitAtComponentVariable outputFalse = new BitAtComponentVariable(tick, bitstreamId, false, to);

            Clause clause1 = new Clause(inputFalse, outputTrue, connectionNotSet);
            Clause clause2 = new Clause(inputTrue, outputFalse, connectionNotSet);
            clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));
        }

        return clausesForAllTicks;

    }
}
