package io.github.githubjakob.convolutionalSat.components.gates;

import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.connections.NoisyConnection;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class Input extends AbstractGate {

    private static int idCounter = 0;

    private int id;

    private final OutputPin outputPin;

    private final InputPin inputPin;

    @Inject
    public Input(Requirements requirements) {
        this.requirements = requirements;
        this.id = idCounter;
        idCounter++;
        this.outputPin = new OutputPin(this);
        this.inputPin = new InputPin(this);
    }

    @Override
    public String toString() {
        return "Input" + id;
    }

    public OutputPin getOutputPin() {
        return outputPin;
    }

    public List<InputPin> getInputPins() {
        return Arrays.asList(inputPin);
    }

    @Override
    public List<Clause> getGateCnf() {
        List<Clause> clausesForAllTicks = new ArrayList<>();

        for (BitStream bitStream : requirements.getBitStreams()) {
            clausesForAllTicks.addAll(getGateCnf(bitStream));
        }

        return clausesForAllTicks;
    }

    private List<Clause> getGateCnf(BitStream bitStream) {
        List<Clause> clausesForAllTicks = new ArrayList<>();

        int bits = bitStream.getLengthWithDelay();
        for (int tick = 0; tick < bits; tick++) {
            BitAtComponentVariable outputTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, inputPin);
            BitAtComponentVariable outputFalse = new BitAtComponentVariable(tick, bitStream.getId(), false, inputPin);

            BitAtComponentVariable outputPinTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, outputPin);
            BitAtComponentVariable outputPinFalse = new BitAtComponentVariable(tick, bitStream.getId(),false, outputPin);

            Clause clause1 = new Clause(outputTrue, outputPinFalse);
            Clause clause2 = new Clause(outputFalse, outputPinTrue);
            clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));

        }

        return clausesForAllTicks;
    }

    @Override
    public boolean evaluate(BitStream bitStream, int tick) {


        if (inputPin.getConnection() instanceof NoisyConnection) {
            int flippedBit = bitStream.getFlippedBitAt(tick, id);
        }

        return inputPin.getConnection().getFrom().getGate().evaluate(bitStream, tick);
    }

    @Override
    public String getType() {
        return "input";
    }

}
