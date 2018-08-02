package io.github.githubjakob.convolutionalSat.components.bitstream;

import io.github.githubjakob.convolutionalSat.components.gates.Input;
import io.github.githubjakob.convolutionalSat.components.gates.Output;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import lombok.Getter;

import java.util.*;

/**
 * Created by jakob on 22.06.18.
 */
public class BitStream {

    @Getter
    int[] bits;

    @Getter
    private final Input input;

    @Getter
    private final Output output;

    @Getter
    int id;

    @Getter
    private int delay;

    private static int idCounter = 0;

    private BitStream(int id, int[] bits, int delay, Input input, Output output) {
        this.id = id;
        this.bits = bits;
        this.delay = delay;
        this.input = input;
        this.output = output;
    }

    public BitStream(int[] bits, int delay, Input input, Output output) {
        this.id = idCounter++;
        this.bits = bits;
        this.delay = delay;
        this.input = input;
        this.output = output;
    }

    public static BitStream noIdAndRandomBits(int blockLength, int delay) {
        return new BitStream(-1, createRandomBits(blockLength), delay, null, null);
    }

    private static int[] createRandomBits(int length) {
        Random rnd = new Random();
        int[] randomBooleans = new int[length];
        for (int i = 0; i < randomBooleans.length; i++) {
            randomBooleans[i] = rnd.nextBoolean() ? 1 : 0;
        }
        return randomBooleans;
    }

    public int getLengthWithDelay() {
        return this.bits.length + delay;
    }

    public int getLength() {
        return this.bits.length;
    }

    public List<Clause> toCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        if (output != null) {
            clausesForTick.addAll(bitStreamAtOutput());
        }


       if (input != null) {
            clausesForTick.addAll(bitStreamAtInput());
       }

       return clausesForTick;

    }

    private List<Clause> bitStreamAtOutput() {
        List<Clause> clausesForTick = new ArrayList<>();

        for (int tick = 0; tick < getLengthWithDelay(); tick++) {
            if (tick < delay) {
                for (InputPin inputPin : output.getInputPins()) {
                    Clause outputClause = new Clause(
                            new BitAtComponentVariable(tick, this.getId(), true, inputPin),
                            new BitAtComponentVariable(tick, this.getId(), false, inputPin));
                    clausesForTick.add(outputClause);
                }
            } else {
                boolean bitSet = isBitSetAt(tick-delay);
                for (InputPin inputPin : output.getInputPins()) {
                    Clause outputClause = new Clause(
                            new BitAtComponentVariable(tick, this.getId(), bitSet, inputPin));
                    clausesForTick.add(outputClause);
                }
            }
        }
        return clausesForTick;

    }

    public boolean isBitSetAt(int tick) {
        return bits[tick] == 1;
    }

    public int getBitAt(int tick) {
        return bits[tick];
    }

    private List<Clause> bitStreamAtInput() {
        List<Clause> clausesForTick = new ArrayList<>();
        for (int tick = 0; tick < getLengthWithDelay(); tick++) {
            if (tick >= getLengthWithDelay() - delay) {
                /**
                 * zuerst wird der bitstream am input angelegt, dann delay-bits lang 0's
                 *
                 */
                Clause inputClause = new Clause(
                        //new BitAtComponentVariable(tick, this.getId(), true, gate.getOutputPin()),
                        new BitAtComponentVariable(tick, this.getId(), false, input.getOutputPin()));
                clausesForTick.add(inputClause);
            } else {
                boolean bitSet = isBitSetAt(tick);
                Clause inputClause = new Clause(
                        new BitAtComponentVariable(tick, this.getId(), bitSet, input.getOutputPin()));
                clausesForTick.add(inputClause);
            }

        }

        return clausesForTick;
    }

        @Override
    public String toString() {
        return Arrays.toString(bits);
    }
}
