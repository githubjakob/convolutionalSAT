package io.github.githubjakob.convolutionalSat.components.bitstream;

import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.gates.Input;
import io.github.githubjakob.convolutionalSat.components.gates.Output;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import lombok.Getter;

import javax.inject.Inject;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static afu.org.checkerframework.checker.units.UnitsTools.min;

/**
 * Created by jakob on 22.06.18.
 */
public class BitStream {

    public static class BitStreamFactory {

        private Requirements requirements;

        @Inject
        public BitStreamFactory(Requirements requirements) {
            this.requirements = requirements;
        }

        public BitStream createBitStream(BitStream bitStream, Input input, Output output) {
            return new BitStream(bitStream.getBits(), bitStream.flippedBits, bitStream.getDelay(), bitStream.distortedChannel, input, output, bitStream.requirements);
        }

        public BitStream createBitStreamWithNoIdAndRandomBits(int blockLength, int delay) {
            return new BitStream(-1, createRandomBits(blockLength), delay, null, null, requirements);
        }

        private int[] createRandomBits(int length) {
            Random rnd = new Random();
            int[] randomBooleans = new int[length];
            for (int i = 0; i < randomBooleans.length; i++) {
                randomBooleans[i] = rnd.nextBoolean() ? 1 : 0;
            }
            return randomBooleans;
        }
    }

    @Getter
    int[] bits;

    @Getter
    private final Input input;

    @Getter
    private final Output output;

    @Getter
    int id;

    @Getter
    private int delay;

    int[] flippedBits;

    private static int idCounter = 0;

    private Requirements requirements;

    private int distortedChannel;

    private BitStream(int id, int[] bits, int delay, Input input, Output output, Requirements requirements) {
        this.id = id;
        this.bits = bits;
        this.delay = delay;
        this.input = input;
        this.output = output;
        this.requirements = requirements;
        flippedBits = createFlippedBits();
        this.distortedChannel = chooseDistortedChannel();
    }

    private BitStream(int[] bits, int[] flippedBits, int delay, int distortedChannel,
                      Input input, Output output, Requirements requirements) {
        this.requirements = requirements;
        this.id = idCounter++;
        this.bits = bits;
        this.delay = delay;
        this.input = input;
        this.output = output;
        this.flippedBits = flippedBits;
        this.distortedChannel = distortedChannel;
    }

    public int getLengthWithDelay() {
        return this.bits.length + delay;
    }

    public int getLength() {
        return this.bits.length;
    }

    public List<Clause> toCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        if (output != null) {
            clausesForTick.addAll(bitStreamAtOutput());
        }


        if (input != null) {
            clausesForTick.addAll(bitStreamAtInput());
        }

        return clausesForTick;

    }

    private List<Clause> bitStreamAtOutput() {
        List<Clause> clausesForTick = new ArrayList<>();

        for (int tick = 0; tick < getLengthWithDelay(); tick++) {
            if (tick < delay) {
                for (InputPin inputPin : output.getInputPins()) {
                    Clause outputClause = new Clause(
                            new BitAtComponentVariable(tick, this.getId(), true, inputPin),
                            new BitAtComponentVariable(tick, this.getId(), false, inputPin));
                    clausesForTick.add(outputClause);
                }
            } else {
                boolean bitSet = getBitValueAt(tick - delay);
                for (InputPin inputPin : output.getInputPins()) {
                    Clause outputClause = new Clause(
                            new BitAtComponentVariable(tick, this.getId(), bitSet, inputPin));
                    clausesForTick.add(outputClause);
                }
            }
        }
        return clausesForTick;

    }

    public boolean getBitValueAt(int tick) {
        return bits[tick] == 1;
    }

    public boolean getBitValueAtOrFalse(int tick) {
        boolean bitValue;
        if (tick < getLength()) {
            bitValue = getBitValueAt(tick);
        } else {
            bitValue = false;
        }
        return bitValue;
    }

    public int getBitAt(int tick) {
        return bits[tick];
    }

    private List<Clause> bitStreamAtInput() {
        List<Clause> clausesForTick = new ArrayList<>();
        for (int tick = 0; tick < getLengthWithDelay(); tick++) {
            boolean bitSet = getBitValueAtOrFalse(tick);
            Clause inputClause = new Clause(
                    new BitAtComponentVariable(tick, this.getId(), bitSet, input.getOutputPin()));
            clausesForTick.add(inputClause);
        }

        return clausesForTick;
    }

    public boolean isBitFlippedAt(int tick, int channel) {
        if (distortedChannel != channel) {
            return false;
        }
        //System.out.println("Flipped Bits " + Arrays.toString(flippedBits));
        return flippedBits[tick] == 1;
    }

    private int[] createFlippedBits() {
        int counter = 0;
        int[] flippedBits = new int[requirements.getFrameLength() + requirements.getDelay()];
        ;

        //System.out.println("channel id for flipped bits " + channel);
        while (counter != requirements.getNumberOfFlippedBits()) {
            counter = 0;
            flippedBits = new int[requirements.getFrameLength() + requirements.getDelay()];
            for (int n = 0; n < flippedBits.length; n++) {
                int randomNum = ThreadLocalRandom.current().nextInt(min, 100 + 1);
                if (randomNum < (requirements.getNumberOfFlippedBits() * 100 / requirements.getFrameLength())) {
                    flippedBits[n] = 1;
                    counter++;
                } else flippedBits[n] = 0;
            }
        }

        //System.out.println("counter " + counter);
        return flippedBits;
    }

    @Override
    public String toString() {
        return "Bits: " + Arrays.toString(bits) + ", flipped: " + getFlippedBitsAsString() + ", channel: " + distortedChannel;
    }

    private int chooseDistortedChannel() {
        return ThreadLocalRandom.current().nextInt(0, 100) % requirements.getNumberOfChannels();
    }

    public String getFlippedBitsAsString() {
        StringBuilder stringBuilder = new StringBuilder();
        stringBuilder.append("[");
        for (int i = 0; i < flippedBits.length; i++) {
            if (flippedBits[i] == 1) {
                stringBuilder.append(i);
            } else {
                stringBuilder.append("-");
            }
        }
        stringBuilder.append("]");
        return stringBuilder.toString();
    }

}
