package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.BitStream;
import lombok.Getter;
import scala.Char;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 12.07.18.
 */
public class TestSuite {

    @Getter
    List<BitStream> bitStreams = new ArrayList<>();

    private Noise noise;

    /**
     * wait for delay ticks and then check that input(encoder) equals output(encoder)
     */
    private int delay;

    int lenght;

    int noiseRatioPercent;

    public TestSuite() {

        delay = 2;
        lenght = 8;
        noiseRatioPercent = 5;

        createBitStreams(lenght);

        int bitStreamLenght = bitStreams.get(0).getLength();
        noise = new Noise(bitStreamLenght, bitStreams.size(), noiseRatioPercent);

    }

    private void createBitStreams(int lenght) {
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
    }

    public Noise getNoise() {
        return noise;
    }

    public int getDelay() {
        return delay;
    }
}
