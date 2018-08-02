package io.github.githubjakob.convolutionalSat.components.gates;

import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.*;
import jdk.nashorn.internal.ir.annotations.Reference;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 22.06.18.
 */
public class And extends AbstractGate  {

    private static int idCounter = 0;

    private int id;

    public InputPin inputPin1;

    public InputPin inputPin2;

    public OutputPin outputPin;

    @Inject
    public And(Requirements requirements) {
        this.id = idCounter++;
        this.requirements = requirements;
        this.inputPin1 = new InputPin(this);
        this.inputPin2 = new InputPin(this);
        this.outputPin = new OutputPin(this);
    }

    @Override
    public String toString() {
        return "And" + id;
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
        int bitStreamId = bitStream.getId();
        int bits = bitStream.getLengthWithDelay();

        for (int tick = 0; tick < bits; tick++) {
            Variable outputTrue = new BitAtComponentVariable(tick, bitStreamId, true, outputPin);
            Variable outputFalse = new BitAtComponentVariable(tick, bitStreamId, false, outputPin);

            Variable input1True = new BitAtComponentVariable(tick, bitStreamId, true, inputPin1);
            Variable input1False = new BitAtComponentVariable(tick, bitStreamId, false, inputPin1);

            Variable input2True = new BitAtComponentVariable(tick, bitStreamId, true, inputPin2);
            Variable input2False = new BitAtComponentVariable(tick, bitStreamId, false, inputPin2);

            Clause clause1 = new Clause(outputFalse, input1True, input2True);
            Clause clause2 = new Clause(outputTrue, input1False, input2False);
            Clause clause3 = new Clause(outputFalse, input1False, input2True);
            Clause clause4 = new Clause(outputFalse, input1True, input2False);
            clausesForAllTicks.addAll(Arrays.asList(clause1, clause2, clause3, clause4));
        }

        return clausesForAllTicks;
    }

    @Override
    public boolean evaluate(int tick) {
        Gate fromGate1 = inputPin1.getConnection().getFrom().getGate();
        Gate fromGate2 = inputPin2.getConnection().getFrom().getGate();
        boolean value = fromGate1.evaluate(tick) && fromGate2.evaluate(tick);
        //System.out.println("Value at " + this.toString() + " : " + value);
        return value;
    }

    @Override
    public String getType() {
        return "and";
    }

    @Override
    public OutputPin getOutputPin() {
        return outputPin;
    }

    @Override
    public List<InputPin> getInputPins() {
        return Arrays.asList(inputPin1, inputPin2);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof And)) return false;
        And other = (And) obj;
        return (this.inputPin1 == other.inputPin1
                && this.inputPin2 == other.inputPin2
                && this.outputPin == other.outputPin);
    }
}
