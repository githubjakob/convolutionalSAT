package io.github.githubjakob.convolutionalSat.components;


import com.sun.org.apache.xpath.internal.operations.Mod;
import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.Noise;
import io.github.githubjakob.convolutionalSat.logic.*;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class NoisyConnection extends Connection {

    private static int channelIdCounter = 0;

    private int channelId;

    private Noise noise;

    public NoisyConnection(OutputPin from, InputPin to, Noise noise) {
        super(from, to);
        this.noise = noise;
        this.channelId = channelIdCounter++;
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

                if (noise.isBitFlipped(channelId, bitstreamId, tick)) {
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
        return "connection";
    }

    @Override
    public Module getModule() {
        return this.from.getModule();
    }
}
