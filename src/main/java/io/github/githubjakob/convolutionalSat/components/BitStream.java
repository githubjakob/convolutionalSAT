package io.github.githubjakob.convolutionalSat.components;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;

/**
 * Created by jakob on 22.06.18.
 */
public class BitStream implements Iterable<Bit> {

    private final List<Bit> bits = new ArrayList<>();

    int id;

    List<Gate> gates = new ArrayList<>();

    public BitStream(int id, int[] bits, Gate... gates) {
        for (Gate gate : gates) {
            this.gates.add(gate);
        }
        this.id = id;

        for (int tick = 0; tick < bits.length; tick++) {
            Bit bit = new Bit(bits[tick], tick, this);
            this.bits.add(bit);
        }
    }

    public int getId() {
        return id;
    }

    public List<Gate> getGates() {
        return gates;
    }

    public int getLength() {
        return this.bits.size();
    }

    @Override
    public Iterator<Bit> iterator() {
        return bits.iterator();
    }


}
