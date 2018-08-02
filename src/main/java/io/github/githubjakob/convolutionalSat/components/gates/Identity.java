package io.github.githubjakob.convolutionalSat.components.gates;

import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 15.06.18.
 */
public class Identity extends AbstractGate {
    private static int idCounter = 0;

    private int id;

    public InputPin inputPin;

    public OutputPin outputPin;

    public Identity() {
        this.id = idCounter++;
        this.inputPin = new InputPin(this);
        this.outputPin = new OutputPin(this);
    }

    @Override
    public String toString() {
        return "Identity" + id;
    }

    public List<Clause> convertToCnf(BitStream bitStream, int maxMicroticks) {
        List<Clause> clausesForAllTicks = new ArrayList<>();

            int bits = bitStream.getLengthWithDelay();
            for (int tick = 0; tick < bits; tick++) {
                Variable outputTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, outputPin);
                Variable outputFalse = new BitAtComponentVariable(tick, bitStream.getId(), false, outputPin);

                Variable inputTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, inputPin);
                Variable inputFalse = new BitAtComponentVariable(tick, bitStream.getId(), false, inputPin);

                Clause clause1 = new Clause(outputFalse, inputTrue);
                Clause clause2 = new Clause(outputTrue, inputFalse);

                clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));
            }


        List<Clause> microtickClauses = getMicrotickCnf(maxMicroticks);
        clausesForAllTicks.addAll(microtickClauses);

        return clausesForAllTicks;

    }

    @Override
    public String getType() {
        return "identity";
    }

    @Override
    public OutputPin getOutputPin() {
        return outputPin;
    }

    @Override
    public List<InputPin> getInputPins() {
        return Arrays.asList(inputPin);
    }

    @Override
    public boolean evaluate(int tick) {
        Gate fromGate = inputPin.getConnection().getFrom().getGate();
        return fromGate.evaluate(tick);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Identity))return false;
        Identity other = (Identity)obj;
        return (this.inputPin == other.inputPin
                && this.outputPin == other.outputPin);
    }
}
