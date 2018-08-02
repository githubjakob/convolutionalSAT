package io.github.githubjakob.convolutionalSat.components.connections;


import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.*;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class NoisyChannelConnection extends AbstractConnection {

    private static int channelIdCounter = 0;

    @Getter
    private int channelId;

    public NoisyChannelConnection(OutputPin from, InputPin to, Requirements requirements) {
        super(from, to, requirements);
        this.channelId = channelIdCounter++;
    }

    @Override
    public String toString() {
        return "C" + id;
    }

    public List<Clause> convertToCnf() {
        List<Clause> clausesForAllTicks = new ArrayList<>();

        for (BitStream bitStream : requirements.getBitStreams()) {
            clausesForAllTicks.addAll(convertToCnfAtTick(bitStream));
        }

        clausesForAllTicks.addAll(convertMicroticksRequirement());

        return clausesForAllTicks;
    }

    private List<Clause> convertToCnfAtTick(BitStream bitStream) {
        List<Clause> clausesForAllTicks = new ArrayList<>();
        ConnectionVariable connectionNotSet = new ConnectionVariable(false, this);
        //System.out.println("Flipped Bits (Gate: " + from.getGate() + ", Channel: " + channelId + " ): " + Arrays.toString(flippedBits));

            int bitstreamId = bitStream.getId();
            int bits = bitStream.getLengthWithDelay();

            for (int tick = 0; tick < bits; tick++) {
                BitAtComponentVariable inputTrue = new BitAtComponentVariable(tick, bitstreamId, true, from);
                BitAtComponentVariable inputFalse = new BitAtComponentVariable(tick, bitstreamId, false, from);

                BitAtComponentVariable outputTrue = new BitAtComponentVariable(tick, bitstreamId, true, to);
                BitAtComponentVariable outputFalse = new BitAtComponentVariable(tick, bitstreamId, false, to);


                if (bitStream.isBitFlippedAt(tick, channelId)) {
                    /**
                     *
                     * Flip Bit
                     *
                     */
                    Clause clause1 = new Clause(outputFalse, inputFalse, connectionNotSet);
                    Clause clause2 = new Clause(outputTrue, inputTrue, connectionNotSet);
                    clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));

                    /**
                     *
                     * Channel und es sollen einige Bits fest auf 1 gesetzt werden
                     * A => C
                     * CNF ~A || C
                     *
                    Clause clause1 = new Clause(outputTrue, connectionNotSet);
                    clausesForAllTicks.addAll(Arrays.asList(clause1));*/

                } else {
                    Clause clause1 = new Clause(inputFalse, outputTrue, connectionNotSet);
                    Clause clause2 = new Clause(inputTrue, outputFalse, connectionNotSet);
                    clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));
                }


        }

        return clausesForAllTicks;

    }
}
