package io.github.githubjakob.convolutionalSat.components;


import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.logic.*;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class NoisyConnection extends Connection {

    private static int channelIdCounter = 0;

    private int channelId;

    private Requirements requirements;

    public NoisyConnection(OutputPin from, InputPin to, Requirements requirements) {
        super(from, to);
        this.requirements = requirements;
        this.channelId = channelIdCounter++;
    }

    @Override
    public String toString() {
        return "C" + id;
    }

    public List<Clause> convertToCnfAtTick(int numberOfGates) {
        List<Clause> clausesForAllTicks = new ArrayList<>();
        ConnectionVariable connectionNotSet = new ConnectionVariable(false, this);
        int[] flippedBits = requirements.getFlippedBits(channelId);
        System.out.println("Flipped Bits (Gate: " + from.getGate() + ", Channel: " + channelId + " ): " + Arrays.toString(flippedBits));

        for (BitStream bitStream : this.getModule().getBitstreams()) {
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
        }

        Gate fromGate = from.getGate();
        Gate toGate = to.getGate();
        if (toGate.getType().equals("input") || toGate.getType().equals("register")) {
            return clausesForAllTicks;
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
        clausesForAllTicks.add(biggerOrEqual);
        biggerOrEqual.addVariable(connectionNotSet);
        for (int i = 0; i < numberOfGates; i++) {
            Variable fromVariableTrue = new MicrotickVariable(i, true, fromGate);
            Variable fromVariableFalse = new MicrotickVariable(i, false, fromGate);
            Variable toVariableTrue = new MicrotickVariable(i, true,to.getGate());
            Variable toVariableFalse = new MicrotickVariable(i, false,to.getGate());

            MicrotickGreaterVariable microtickGreaterVariableTrue = new MicrotickGreaterVariable(i, true, this);
            MicrotickGreaterVariable microtickGreaterVariableFalse = new MicrotickGreaterVariable(i, false, this);
            biggerOrEqual.addVariable(microtickGreaterVariableTrue);

            clausesForAllTicks.add(new Clause(microtickGreaterVariableFalse, fromVariableFalse));
            clausesForAllTicks.add(new Clause(microtickGreaterVariableFalse, toVariableTrue));
            clausesForAllTicks.add(new Clause(microtickGreaterVariableTrue, fromVariableTrue, toVariableFalse));

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
        return "connections";
    }

    @Override
    public Module getModule() {
        return this.from.getModule();
    }
}
