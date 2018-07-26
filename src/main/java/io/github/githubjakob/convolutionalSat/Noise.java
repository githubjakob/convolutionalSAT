package io.github.githubjakob.convolutionalSat;

import lombok.Getter;

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
        createNoise(bitStreamLenght, numberOfBitStreams, noiseRatioPercent);
    }

    private void createNoise(int bitStreamLenght, int numberOfBitStreams, int noiseRatioPercent) {
        noiseInAllBitStreams = new int[this.numberOfBitStreams][];
        for (int i = 0; i < this.numberOfBitStreams; i++) {
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

    public int[] getFlippedBits() {
        int randomNum = ThreadLocalRandom.current().nextInt(min, noiseInAllBitStreams.length);
        return noiseInAllBitStreams[randomNum];
    }
}