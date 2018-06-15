package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class Input implements Gate {

    private static int idCounter = 0;

    private final Enums.Group group;

    private int id;

    private final OutputPin outputPin;

    private final InputPin inputPin;

    public Input(Enums.Group group) {
        this.group = group;
        this.id = idCounter;
        idCounter++;
        this.outputPin = new OutputPin(this);
        this.inputPin = new InputPin(this);
    }

    @Override
    public String toString() {
        return "GlobalInput" + id;
    }

    public OutputPin getOutputPin() {
        return outputPin;
    }

    public List<InputPin> getInputPins() {
        return Arrays.asList(inputPin);
    }

    @Override
    public List<Clause> convertToCnfAtTick(int tick) {
        TimeDependentVariable outputTrue = new TimeDependentVariable(tick, true, inputPin);
        TimeDependentVariable outputFalse = new TimeDependentVariable(tick, false, inputPin);

        TimeDependentVariable outputPinTrue = new TimeDependentVariable(tick, true, outputPin);
        TimeDependentVariable outputPinFalse = new TimeDependentVariable(tick, false, outputPin);

        Clause clause1 = new Clause(outputTrue, outputPinFalse);
        Clause clause2 = new Clause(outputFalse, outputPinTrue);

        return Arrays.asList(clause1, clause2);
    }

    @Override
    public Enums.Group getGroup() {
        return group;
    }

    @Override
    public String getType() {
        return "input";
    }

}
