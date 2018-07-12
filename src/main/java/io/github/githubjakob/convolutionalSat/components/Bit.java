package io.github.githubjakob.convolutionalSat.components;

import lombok.Getter;

/**
 * Created by jakob on 22.06.18.
 */
public class Bit {

    @Getter
    private final BitStream correspondingBitStream;

    @Getter
    int bit;


    public Bit(int bit, BitStream correspondingBitStream) {
        this.bit = bit;
        this.correspondingBitStream = correspondingBitStream;
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
