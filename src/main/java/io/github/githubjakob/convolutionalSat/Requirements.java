package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.BitStream;
import lombok.Getter;

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

    public Requirements(int delay, int blockLength, int noiseRatioPercent, int distortedChannel) {
        this.blockLength = blockLength;

        this.delay = delay;
        this.noiseRatioPercent = noiseRatioPercent;
        this.distortedChannel = distortedChannel;
        enableNoise = false;

        //createBitStreams(lenght);
        //BitStream bitsStreamIn1 = addRandomBitStream();
        //BitStream bitsStreamIn2 = addRandomBitStream();
        //bitStreams.addAll(Arrays.asList(bitsStreamIn1, bitsStreamIn2));

        //sanitiyCheck(blockLength, delay, bitStreams);
        System.out.println("Test Suite with: delay " + delay + ", bitStreamLenght: " + blockLength + ", noise enabled: " + (noiseRatioPercent > 0));
    }

    public void setDistortedChannel(int distortedChannel) {
        this.distortedChannel = distortedChannel;
        System.out.println("Setting distorted channel: " + distortedChannel + " with value: " + noiseRatioPercent + " %");
    }



    private void sanitiyCheck(int blockLength, int delay, List<BitStream> bitStreams) {
        for (BitStream bitStream : bitStreams) {
            if (delay != bitStream.getDelay() || blockLength != (bitStream.getLengthWithDelay() - bitStream.getDelay())) {
                throw new RuntimeException("Invalid block and/or delay length");
            }
        }
    }

    public void addBitStream(BitStream bitStream) {
        bitStreams.add(bitStream);
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
