package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.BitStream;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

import static afu.org.checkerframework.checker.units.UnitsTools.min;

/**
 * Created by jakob on 12.07.18.
 */
public class Noise {

    private int numberOfBitStreams;

    int[][] noiseInAllBitStreams = new int[][] {
            // channel // tick
            new int[] {0, 0 }
    };

    int[][] noiseInChannelBitstreamAtTick = new int[][] {
            // channel // bitstream // tick
            new int[] {0, 0, 0 },
            new int[] {0, 0, 1 }
    };

    /**
     *
     * @param noiseRatioPercent 30 means 1/3 a third of the bits is flipped
     */
    public Noise(int bitStreamLenght, int numberOfBitStreams, int noiseRatioPercent) {
        this.numberOfBitStreams = numberOfBitStreams;
        noiseInAllBitStreams = new int[numberOfBitStreams][];
        for (int i = 0; i < numberOfBitStreams; i++) {
            int[] flippedBits = new int[bitStreamLenght];
            for (int n = 0; n < flippedBits.length; n++) {
                int randomNum = ThreadLocalRandom.current().nextInt(min, 100 + 1);
                if (randomNum < noiseRatioPercent) {
                    flippedBits[n] = 1;
                } else flippedBits[n] = 0;
            }
            noiseInAllBitStreams[i] = flippedBits;
        }
    }

    public boolean isBitFlipped(int channelId, int bitstreamId, int tick) {
        int randomNum = ThreadLocalRandom.current().nextInt(min, numberOfBitStreams + 1);
        int[] definition = noiseInAllBitStreams[randomNum];
        if (definition[tick] == 1) {
            return true;
        }
        /*for (int[] definition : noiseInChannelBitstreamAtTick) {
            if (definition[0] == channelId && definition[1] == bitstreamId && definition[2] == tick) {
                return true;
            }
        }*/
        return false;
    }

    public int[] getFlippedBits() {
        int randomNum = ThreadLocalRandom.current().nextInt(min, 10);
        return noiseInAllBitStreams[randomNum];
    }
}