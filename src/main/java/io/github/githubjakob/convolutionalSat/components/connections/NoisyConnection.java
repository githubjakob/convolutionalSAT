package io.github.githubjakob.convolutionalSat.components.connections;


import io.github.githubjakob.convolutionalSat.Noise;
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
public class NoisyConnection extends AbstractConnection {

    private static int channelIdCounter = 0;

    private final Noise noise;

    private int channelId;

    public NoisyConnection(OutputPin from, InputPin to, Noise noise) {
        this.id = idCounter++;
        this.from = from;
        this.to = to;
        this.noise = noise;
        this.channelId = channelIdCounter++;
    }

    @Override
    public String toString() {
        return "C" + id;
    }

    public List<Clause> convertToCnfAtTick(BitStream bitStream, int numberOfGates) {
        List<Clause> clausesForAllTicks = new ArrayList<>();
        ConnectionVariable connectionNotSet = new ConnectionVariable(false, this);
        int[] flippedBits = noise.getFlippedBits(channelId);
        //System.out.println("Flipped Bits (Gate: " + from.getGate() + ", Channel: " + channelId + " ): " + Arrays.toString(flippedBits));

            int bitstreamId = bitStream.getId();
            int bits = bitStream.getLengthWithDelay();

            for (int tick = 0; tick < bits; tick++) {
                BitAtComponentVariable inputTrue = new BitAtComponentVariable(tick, bitstreamId, true, from);
                BitAtComponentVariable inputFalse = new BitAtComponentVariable(tick, bitstreamId, false, from);

                BitAtComponentVariable outputTrue = new BitAtComponentVariable(tick, bitstreamId, true, to);
                BitAtComponentVariable outputFalse = new BitAtComponentVariable(tick, bitstreamId, false, to);


                if (flippedBits[tick] == 1) {
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