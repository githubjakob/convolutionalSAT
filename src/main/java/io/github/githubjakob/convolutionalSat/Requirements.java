package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.Bit;
import io.github.githubjakob.convolutionalSat.components.BitStream;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import lombok.Getter;
import lombok.Setter;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static afu.org.checkerframework.checker.units.UnitsTools.min;

/**
 * Created by jakob on 12.07.18.
 */
public class Requirements {

    @Getter
    List<BitStream> bitStreams = new ArrayList<>();

    /**
     * wait for delay ticks and then check that input(encoder) equals output(encoder)
     */
    @Getter
    private int delay;

    int noiseRatioPercent;

    private int distortedChannel;

    boolean enableNoise;

    int blockLength;

    int bitStreamCounter = 0;

    public Requirements(int delay, int blockLength, int noiseRatioPercent, int distortedChannel) {
        this.blockLength = blockLength;

        this.delay = delay;
        this.noiseRatioPercent = noiseRatioPercent;
        this.distortedChannel = distortedChannel;
        enableNoise = false;

        //createBitStreams(lenght);
        //BitStream bitsStreamIn1 = createRandomBitStream();
        //BitStream bitsStreamIn2 = createRandomBitStream();
        //bitStreams.addAll(Arrays.asList(bitsStreamIn1, bitsStreamIn2));

        //sanitiyCheck(blockLength, delay, bitStreams);
        System.out.println("Test Suite with: delay " + delay + ", bitStreamLenght: " + blockLength + ", noise enabled: " + (noiseRatioPercent > 0));
    }

    public void setDistortedChannel(int distortedChannel) {
        this.distortedChannel = distortedChannel;
        System.out.println("Setting distorted channel: " + distortedChannel + " with value: " + noiseRatioPercent + " %");
    }

    public BitStream findFailingBitStream(Circuit circuit, Requirements requirements) {
        int counter = 0;
        int MAX_RETRIES = 1000;
        while(counter < MAX_RETRIES) {
            BitStream bitStream = new BitStream(-1, createRandomBits(blockLength), delay);
            if (bitStream == null) {
                System.out.println("asdfadsf");
            }
            if (!circuit.testBitStream(bitStream, requirements.getDelay())) {
                System.out.println("found failing Bitstream " + bitStream.toString());
                return new BitStream(bitStreamCounter++, bitStream.getBits(), bitStream.getDelay(), null);
            }
            counter++;
        }
        return null;
    }

    public BitStream createRandomBitStream() {
        return new BitStream(bitStreamCounter++, createRandomBits(blockLength), delay);
    }

    private void sanitiyCheck(int blockLength, int delay, List<BitStream> bitStreams) {
        for (BitStream bitStream : bitStreams) {
            if (delay != bitStream.getDelay() || blockLength != (bitStream.getLength() - bitStream.getDelay())) {
                throw new RuntimeException("Invalid block and/or delay length");
            }
        }
    }

    public void addBitStream(BitStream bitStream) {
        bitStreams.add(bitStream);
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

    public int[] getFlippedBits(int channel) {
        //System.out.println("channel id for flipped bits " + channel);
        int[] flippedBits = new int[blockLength + delay];

        for (int n = 0; n < flippedBits.length; n++) {
            int randomNum = ThreadLocalRandom.current().nextInt(min, 100 + 1);
            if (randomNum < noiseRatioPercent && channel == distortedChannel) {
                flippedBits[n] = 1;
            } else flippedBits[n] = 0;
        }

        return flippedBits;
    }
}
