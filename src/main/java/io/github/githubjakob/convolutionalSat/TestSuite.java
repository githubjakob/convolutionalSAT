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

    public TestSuite() {
        BitStream bitsStreamIn0 = new BitStream(0, new int[] { 0, 0, 0, 0});
        BitStream bitsStreamIn1 = new BitStream(1, new int[] { 0, 0, 0, 1});
        BitStream bitsStreamIn2 = new BitStream(2, new int[] { 0, 0, 1, 0});
        BitStream bitsStreamIn3 = new BitStream(3, new int[] { 0, 0, 1, 1});
        BitStream bitsStreamIn4 = new BitStream(4, new int[] { 0, 1, 0, 0});
        BitStream bitsStreamIn5 = new BitStream(5, new int[] { 0, 1, 0, 1});
        BitStream bitsStreamIn6 = new BitStream(6, new int[] { 0, 1, 1, 0});
        BitStream bitsStreamIn7 = new BitStream(7, new int[] { 0, 1, 1, 1});
        BitStream bitsStreamIn8 = new BitStream(8, new int[] { 1, 0, 0, 0});
        BitStream bitsStreamIn9 = new BitStream(9, new int[] { 1, 0, 0, 1});
        BitStream bitsStreamIn10 = new BitStream(10, new int[] { 1, 0, 1, 0});
        BitStream bitsStreamIn11 = new BitStream(11, new int[] { 1, 0, 1, 1});
        BitStream bitsStreamIn12 = new BitStream(12, new int[] { 1, 1, 0, 0});
        BitStream bitsStreamIn13 = new BitStream(13, new int[] { 1, 1, 0, 1});
        BitStream bitsStreamIn14 = new BitStream(14, new int[] { 1, 1, 1, 0});
        BitStream bitsStreamIn15 = new BitStream(15, new int[] { 1, 1, 1, 1});
        bitStreams = Arrays.asList(bitsStreamIn0, bitsStreamIn1, bitsStreamIn2, bitsStreamIn3,
                bitsStreamIn4, bitsStreamIn5, bitsStreamIn6, bitsStreamIn7, bitsStreamIn8, bitsStreamIn9,
                bitsStreamIn10, bitsStreamIn11, bitsStreamIn12, bitsStreamIn13, bitsStreamIn14, bitsStreamIn15);

        noise = new Noise();
    }

    public Noise getNoise() {
        return noise;
    }
}
