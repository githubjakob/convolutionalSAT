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

    public BitStream(int id, int[] bits) {
        this.id = id;

        for (int tick = 0; tick < bits.length; tick++) {
            Bit bit = new Bit(bits[tick], tick, this);
            this.bits.add(bit);
        }
    }

    public int getId() {
        return id;
    }

    public int getLength() {
        return this.bits.size();
    }

    @Override
    public Iterator<Bit> iterator() {
        return bits.iterator();
    }


}
