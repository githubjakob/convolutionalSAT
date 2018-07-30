package io.github.githubjakob.convolutionalSat.components.connection;


import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.BitStream;
import io.github.githubjakob.convolutionalSat.components.InputPin;
import io.github.githubjakob.convolutionalSat.components.OutputPin;
import io.github.githubjakob.convolutionalSat.components.connection.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.logic.*;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class NoisyConnection extends AbstractConnection {

    private static int channelIdCounter = 0;

    private int channelId;

    private Requirements requirements;

    public NoisyConnection(OutputPin from, InputPin to, Requirements requirements) {
        this.id = idCounter++;
        this.from = from;
        this.to = to;
        this.requirements = requirements;
        this.channelId = channelIdCounter++;
    }

    @Override
    public String toString() {
        return "C" + id;
    }

    public List<Clause> convertToCnfAtTick(BitStream bitStream, int numberOfGates) {
        List<Clause> clausesForAllTicks = new ArrayList<>();
        ConnectionVariable connectionNotSet = new ConnectionVariable(false, this);
        int[] flippedBits = requirements.getFlippedBits(channelId);
        System.out.println("Flipped Bits (Gate: " + from.getGate() + ", Channel: " + channelId + " ): " + Arrays.toString(flippedBits));

            int bitstreamId = bitStream.getId();
            int bits = bitStream.getLength();

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
