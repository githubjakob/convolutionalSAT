package io.github.githubjakob.convolutionalSat.components;

import lombok.Getter;

/**
 * Created by jakob on 22.06.18.
 */
public class Bit {

    private final BitStream correspondingBitStream;

    @Getter
    int bit;

    int tick;

    public Bit(int bit, int tick, BitStream correspondingBitStream) {
        this.bit = bit;
        this.tick = tick;
        this.correspondingBitStream = correspondingBitStream;
    }

    public int getTick() {
        return tick;
    }

    public int getBitStreamid() {
        return this.correspondingBitStream.getId();
    }

    public boolean getWeight() {
        return bit == 1;
    }

    @Override
    public String toString() {
        return getBit() + "";
    }
}
