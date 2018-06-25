package io.github.githubjakob.convolutionalSat.components;

/**
 * Created by jakob on 22.06.18.
 */
public class Bit {

    private final BitStream correspondingBitStream;

    int bit;

    int tick;

    public Bit(int bit, int tick, BitStream correspondingBitStream) {
        this.bit = bit;
        this.tick = tick;
        this.correspondingBitStream = correspondingBitStream;
    }

    public boolean getBit() {
        return bit == 1;
    }

    public int getTick() {
        return tick;
    }

    public int getBitStreamid() {
        return this.correspondingBitStream.getId();
    }
}
