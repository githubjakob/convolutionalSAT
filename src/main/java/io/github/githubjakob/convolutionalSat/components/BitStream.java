package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.Property;
import lombok.Getter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jakob on 22.06.18.
 */
public class BitStream implements Iterable<Bit>, Property {

    @Getter
    private final List<Bit> bits;

    @Getter
    private final List<Gate> gates = new ArrayList<>();

    int id;

    public BitStream(int id, List<Bit> bits, Gate... gates) {
        this.id = id;
        this.gates.addAll(Arrays.asList(gates));
        this.bits = bits;
    }

    public BitStream(int id, List<Integer> bits) {
        this.id = id;
        this.bits = new ArrayList<>();
        for (int tick = 0; tick < bits.size(); tick++) {
            Bit bit = new Bit(bits.get(tick), tick, this);
            this.bits.add(bit);
        }
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return this.bits.size();
    }

    public void addGate(Gate gate) {
        this.gates.add(gate);
    }

    @Override
    public Iterator<Bit> iterator() {
        return bits.iterator();
    }


    @Override
    public List<Clause> toCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        for (Gate gate : gates) {
            if ("output".equals(gate.getType())) {
                for (Bit bit : this) {
                    for (InputPin inputPin : gate.getInputPins()) {
                        Clause outputClause = new Clause(
                                new BitAtComponentVariable(bit.getTick(), this.getId(), bit.getWeight(), inputPin));
                        clausesForTick.add(outputClause);
                    }

                }
            }

            if ("input".equals(gate.getType())) {
                for (Bit bit : this) {
                    Clause outputClause = new Clause(
                            new BitAtComponentVariable(bit.getTick(), this.getId(), bit.getWeight(), gate.getOutputPin()));
                    clausesForTick.add(outputClause);
                }
            }
        }

        return clausesForTick;
    }
}
