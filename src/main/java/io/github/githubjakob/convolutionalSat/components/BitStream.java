package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.Property;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jakob on 22.06.18.
 */
public class BitStream implements Iterable<Bit>, Property {

    @Getter
    private final List<Bit> bits;

    @Getter
    private final Gate gate;

    int id;

    @Getter
    private int delay;

    public BitStream(int id, List<Bit> bits, int delay, Gate gate) {
        this.id = id;
        this.delay = delay;
        this.gate = gate;
        this.bits = new ArrayList<>(bits);
    }

    public BitStream(int id, List<Integer> bits, int delay) {
        this.id = id;
        this.gate = null;
        this.delay = delay;
        this.bits = new ArrayList<>();
        for (int tick = 0; tick < bits.size(); tick++) {
            Bit bit = new Bit(bits.get(tick), this);
            this.bits.add(bit);
        }
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return this.bits.size() + delay;
    }

    @Override
    public Iterator<Bit> iterator() {
        return bits.iterator();
    }


    @Override
    public List<Clause> toCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        for (int tick = 0; tick < getLength(); tick++) {

            if ("output".equals(gate.getType())) {

                if (tick < delay) {
                    for (InputPin inputPin : gate.getInputPins()) {
                        Clause outputClause = new Clause(
                                new BitAtComponentVariable(tick, this.getId(), true, inputPin),
                                new BitAtComponentVariable(tick, this.getId(), false, inputPin));
                        clausesForTick.add(outputClause);
                    }
                } else {
                    Bit bit = bits.get(tick-delay);
                    for (InputPin inputPin : gate.getInputPins()) {
                        Clause outputClause = new Clause(
                                new BitAtComponentVariable(tick, this.getId(), bit.getWeight(), inputPin));
                        clausesForTick.add(outputClause);
                    }
                }
            }

            if ("input".equals(gate.getType())) {

                if (tick >= getLength() - delay) {
                    Clause outputClause = new Clause(
                            new BitAtComponentVariable(tick, this.getId(), true, gate.getOutputPin()),
                            new BitAtComponentVariable(tick, this.getId(), false, gate.getOutputPin()));
                    clausesForTick.add(outputClause);
                } else {
                    Bit bit = bits.get(tick);
                    Clause outputClause = new Clause(
                            new BitAtComponentVariable(tick, this.getId(), bit.getWeight(), gate.getOutputPin()));
                    clausesForTick.add(outputClause);
                }

            }


        }

        return clausesForTick;
    }
}
