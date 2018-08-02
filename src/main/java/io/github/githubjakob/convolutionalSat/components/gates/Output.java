package io.github.githubjakob.convolutionalSat.components.gates;

import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
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
public class Output extends AbstractGate {

    private static int idCounter = 0;

    private int id;

    private final InputPin inputPin;

    private final OutputPin outputPin;

    @Inject
    public Output(Requirements requirements) {
        this.requirements = requirements;
        this.id = idCounter++;
        this.outputPin = new OutputPin(this);
        this.inputPin = new InputPin(this);
    }

    @Override
    public String toString() {
        return "Output" + id;
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
                BitAtComponentVariable outputTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, outputPin);
                BitAtComponentVariable outputFalse = new BitAtComponentVariable(tick, bitStream.getId(), false, outputPin);

                BitAtComponentVariable inputPinTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, inputPin);
                BitAtComponentVariable inputPinFalse = new BitAtComponentVariable(tick, bitStream.getId(), false, inputPin);

                Clause clause1 = new Clause(outputTrue, inputPinFalse);
                Clause clause2 = new Clause(outputFalse, inputPinTrue);

                clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));

            }


        return clausesForAllTicks;
    }

    @Override
    public boolean evaluate(int tick) {
        Gate fromGate = inputPin.getConnection().getFrom().getGate();
        boolean value = fromGate.evaluate(tick);
        //System.out.println("Value at " + this.toString() + " : " + value);
        return value;
    }

    @Override
    public String getType() {
        return "output";
    }
}
