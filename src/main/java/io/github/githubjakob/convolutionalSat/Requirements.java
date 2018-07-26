package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.BitStream;
import lombok.Getter;

import java.util.*;

/**
 * Created by jakob on 12.07.18.
 */
public class Requirements {

    @Getter
    List<BitStream> bitStreams = new ArrayList<>();

    @Getter
    private Noise noise;

    /**
     * wait for delay ticks and then check that input(encoder) equals output(encoder)
     */
    @Getter
    private int delay;

    int noiseRatioPercent;

    boolean enableNoise;

    int blockLength;

    public Requirements(int delay, int blockLength, int noiseRatioPercent) {
        this.blockLength = blockLength;

        this.delay = delay;
        this.noiseRatioPercent = noiseRatioPercent;
        enableNoise = false;

        //createBitStreams(lenght);
        BitStream bitsStreamIn1 = new BitStream(0, createRandomBits(blockLength), delay);
        BitStream bitsStreamIn2 = new BitStream(1, createRandomBits(blockLength), delay);
        bitStreams.addAll(Arrays.asList(bitsStreamIn1, bitsStreamIn2));

        if (noiseRatioPercent > 0) {
            noise = new Noise(blockLength, bitStreams.size(), noiseRatioPercent);
        }

        sanitiyCheck(blockLength, delay, bitStreams);
        System.out.println("Test Suite with: delay " + delay + ", bitStreamLenght: " + blockLength + ", noise enabled: " + (noiseRatioPercent > 0));
    }

    private void sanitiyCheck(int blockLength, int delay, List<BitStream> bitStreams) {
        for (BitStream bitStream : bitStreams) {
            if (delay != bitStream.getDelay() || blockLength != (bitStream.getLength() - bitStream.getDelay())) {
                throw new RuntimeException("Invalid block and/or delay length");
            }
        }
    }

    private boolean[] createRandomBits(int length) {
        Random rnd = new Random();
        boolean[] randomBooleans = new boolean[length];
        for (int i = 0; i < randomBooleans.length; i++) {
            randomBooleans[i] = rnd.nextBoolean();
        }
        return randomBooleans;
    }

    public boolean isNoiseEnabled() {
        return noiseRatioPercent > 0;
    }

    /*private void createBitStreams(int lenght) {
        for (int i = 0; i < Math.pow(2, lenght); i++) {
            List<Integer> bits = new ArrayList<>(lenght);
            for (int n = 0; n < lenght; n++) {
                bits.add(0);
            }
            String binary = Integer.toBinaryString(i);
            char[] digits = binary.toCharArray();

            for (int n = 0; n < digits.length; n++) {
                char digit = digits[n];
                String digitNumber = digit + "";
                Integer integer = Integer.valueOf(digitNumber);
                bits.set(bits.size()-digits.length + n, integer);
            }

            BitStream bitStream = new BitStream(i, bits, delay);
            bitStreams.add(bitStream);
        }
    }*/
}
