package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.BitStream;
import lombok.Getter;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 12.07.18.
 */
public class TestSuite {

    @Getter
    List<BitStream> bitStreams;

    private Noise noise;

    /**
     * wait for delay ticks and then check that input(encoder) equals output(encoder)
     */
    private int delay;

    public TestSuite() {
        BitStream bitsStreamIn0 = new BitStream(0, Arrays.asList(0, 0, 0, 0));
        BitStream bitsStreamIn1 = new BitStream(1, Arrays.asList(0, 0, 0, 1));
        BitStream bitsStreamIn2 = new BitStream(2, Arrays.asList(0, 0, 1, 0));
        BitStream bitsStreamIn3 = new BitStream(3, Arrays.asList(0, 0, 1, 1));
        BitStream bitsStreamIn4 = new BitStream(4, Arrays.asList(0, 1, 0, 0));
        BitStream bitsStreamIn5 = new BitStream(5, Arrays.asList(0, 1, 0, 1));
        BitStream bitsStreamIn6 = new BitStream(6, Arrays.asList(0, 1, 1, 0));
        BitStream bitsStreamIn7 = new BitStream(7, Arrays.asList(0, 1, 1, 1));
        BitStream bitsStreamIn8 = new BitStream(8, Arrays.asList(1, 0, 0, 0));
        BitStream bitsStreamIn9 = new BitStream(9, Arrays.asList(1, 0, 0, 1));
        BitStream bitsStreamIn10 = new BitStream(10, Arrays.asList(1, 0, 1, 0));
        BitStream bitsStreamIn11 = new BitStream(11, Arrays.asList(1, 0, 1, 1));
        BitStream bitsStreamIn12 = new BitStream(12, Arrays.asList(1, 1, 0, 0));
        BitStream bitsStreamIn13 = new BitStream(13, Arrays.asList(1, 1, 0, 1));
        BitStream bitsStreamIn14 = new BitStream(14, Arrays.asList(1, 1, 1, 0));
        BitStream bitsStreamIn15 = new BitStream(15, Arrays.asList(1, 1, 1, 1));
        bitStreams = Arrays.asList(bitsStreamIn0, bitsStreamIn1, bitsStreamIn2, bitsStreamIn3,
                bitsStreamIn4, bitsStreamIn5, bitsStreamIn6, bitsStreamIn7, bitsStreamIn8, bitsStreamIn9,
                bitsStreamIn10, bitsStreamIn11, bitsStreamIn12, bitsStreamIn13, bitsStreamIn14, bitsStreamIn15);

        noise = new Noise();
        delay = 2;
    }

    public Noise getNoise() {
        return noise;
    }

    public int getDelay() {
        return delay;
    }
}
