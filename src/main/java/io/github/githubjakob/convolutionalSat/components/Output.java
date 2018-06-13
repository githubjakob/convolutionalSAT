package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.Clauses;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class Output implements Gate {

    private final InputPin inputPin;

    public Output() {
        this.inputPin = new InputPin(this);
    }
    @Override
    public String toString() {
        return "GlobalOutput";
    }

    public OutputPin getOutputPin() {
        return null;
    }

    public List<InputPin> getInputPins() {
        return Arrays.asList(inputPin);
    }

    @Override
    public Clauses convertToCnfAtTick(int tick) {
        TimeDependentVariable outputTrue = new TimeDependentVariable(tick, true, this);
        TimeDependentVariable outputFalse = new TimeDependentVariable(tick, false, this);

        TimeDependentVariable inputPinTrue = new TimeDependentVariable(tick, true, inputPin);
        TimeDependentVariable inputPinFalse = new TimeDependentVariable(tick, false, inputPin);


        Clause clause1 = new Clause(outputTrue, inputPinFalse);
        Clause clause2 = new Clause(outputFalse, inputPinTrue);

        return new Clauses(tick, clause1, clause2);
    }

    @Override
    public String getType() {
        return "output";
    }
}
