package io.github.githubjakob.convolutionalSat;

import java.util.concurrent.ThreadLocalRandom;

import static afu.org.checkerframework.checker.units.UnitsTools.min;

/**
 * Created by jakob on 31.07.18.
 */
public class Noise {

    private int blockLength;

    private int delay;

    private int noiseRatioPercent;

    private int distortedChannel;

    public void setup(int blockLength, int delay, int noiseRatioPercent, int distortedChannel) {
        this.blockLength = blockLength;
        this.delay = delay;
        this.distortedChannel = distortedChannel;
        this.noiseRatioPercent = noiseRatioPercent;
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
