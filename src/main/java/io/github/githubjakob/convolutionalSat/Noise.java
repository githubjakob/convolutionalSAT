package io.github.githubjakob.convolutionalSat;

/**
 * Created by jakob on 12.07.18.
 */
public class Noise {

    int[][] noiseInAllBitStreams = new int[][] {
            // channel // tick
            new int[] {0, 0 },
            new int[] {0, 1 },
            new int[] {1, 3 }
    };

    int[][] noiseInChannelBitstreamAtTick = new int[][] {
            // channel // bitstream // tick
            new int[] {0, 0, 0 },
            new int[] {0, 0, 1 }
    };

    public boolean isBitFlipped(int channelId, int bitstreamId, int tick) {
        for (int[] definition : noiseInAllBitStreams) {
            if (definition[0] == channelId && definition[1] == tick) {
                return true;
            }
        }
        for (int[] definition : noiseInChannelBitstreamAtTick) {
            if (definition[0] == channelId && definition[1] == bitstreamId && definition[2] == tick) {
                return true;
            }
        }
        return false;
    }
}