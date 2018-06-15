package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class Output implements Gate {

    private static int idCounter = 0;

    private final Enums.Group group;

    private int id;

    private final InputPin inputPin;

    private final OutputPin outputPin;

    public Output(Enums.Group group) {
        this.group = group;
        this.id = idCounter;
        idCounter++;
        this.outputPin = new OutputPin(this);
        this.inputPin = new InputPin(this);
    }
    @Override
    public String toString() {
        return "GlobalOutput" + id;
    }

    public OutputPin getOutputPin() {
        return outputPin;
    }

    public List<InputPin> getInputPins() {
        return Arrays.asList(inputPin);
    }

    @Override
    public List<Clause> convertToCnfAtTick(int tick) {
        TimeDependentVariable outputTrue = new TimeDependentVariable(tick, true, outputPin);
        TimeDependentVariable outputFalse = new TimeDependentVariable(tick, false, outputPin);

        TimeDependentVariable inputPinTrue = new TimeDependentVariable(tick, true, inputPin);
        TimeDependentVariable inputPinFalse = new TimeDependentVariable(tick, false, inputPin);


        Clause clause1 = new Clause(outputTrue, inputPinFalse);
        Clause clause2 = new Clause(outputFalse, inputPinTrue);

        return Arrays.asList(clause1, clause2);
    }

    @Override
    public Enums.Group getGroup() {
        return group;
    }

    @Override
    public String getType() {
        return "output";
    }
}
