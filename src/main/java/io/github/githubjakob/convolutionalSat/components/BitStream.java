package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.components.gates.Input;
import io.github.githubjakob.convolutionalSat.components.gates.Output;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import lombok.Getter;

import java.util.*;

/**
 * Created by jakob on 22.06.18.
 */
public class BitStream implements Iterable<Bit> {

    @Getter
    private final List<Bit> bits;

    @Getter
    private final Input input;

    @Getter
    private final Output output;

    int id;

    @Getter
    private int delay;

    public BitStream(int id, List<Bit> bits, int delay, Input input, Output output) {
        this.id = id;
        this.delay = delay;
        this.input = input;
        this.output = output;
        this.bits = new ArrayList<>(bits);
    }

    public BitStream(int id, boolean[] bits, int delay) {
        this.id = id;
        this.input = null;
        this.output = null;
        this.delay = delay;
        this.bits = new ArrayList<>();
        for (int tick = 0; tick < bits.length; tick++) {
            Bit bit = new Bit(bits[tick] ? 1 : 0, this);
            this.bits.add(bit);
        }
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return this.bits.size() + delay;
    }

    public int[] getBitValues() {
        int[] bitValues = new int[bits.size()];
        int count = 0;
        for (Bit bit : bits) {
            bitValues[count] = bit.getWeight() ? 1 : 0;
            count++;
        }
        return bitValues;
    }

    @Override
    public Iterator<Bit> iterator() {
        return bits.iterator();
    }


    public List<Clause> toCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        for (int tick = 0; tick < getLength(); tick++) {


                if (tick < delay) {
                    for (InputPin inputPin : output.getInputPins()) {
                        Clause outputClause = new Clause(
                                new BitAtComponentVariable(tick, this.getId(), true, inputPin),
                                new BitAtComponentVariable(tick, this.getId(), false, inputPin));
                        clausesForTick.add(outputClause);
                    }
                } else {
                    Bit bit = bits.get(tick-delay);
                    for (InputPin inputPin : output.getInputPins()) {
                        Clause outputClause = new Clause(
                                new BitAtComponentVariable(tick, this.getId(), bit.getWeight(), inputPin));
                        clausesForTick.add(outputClause);
                    }
                }

                if (tick >= getLength() - delay) {
                    /**
                     * zuerst wird der bitstream am input angelegt, dann delay-bits lang 0's
                     *
                     */
                    Clause inputClause = new Clause(
                            //new BitAtComponentVariable(tick, this.getId(), true, gate.getOutputPin()),
                            new BitAtComponentVariable(tick, this.getId(), false, input.getOutputPin()));
                    clausesForTick.add(inputClause);
                } else {
                    Bit bit = bits.get(tick);
                    Clause inputClause = new Clause(
                            new BitAtComponentVariable(tick, this.getId(), bit.getWeight(), input.getOutputPin()));
                    clausesForTick.add(inputClause);
                }

            }

        return clausesForTick;
    }

    @Override
    public String toString() {
        return Arrays.toString(getBitValues());
    }
}
