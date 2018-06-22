package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 15.06.18.
 */
public class Identity implements Gate {
    private static int idCounter = 0;

    private final Enums.Module module;

    private int id;

    public InputPin inputPin;

    public OutputPin outputPin;

    public Identity(Enums.Module module) {
        this.module = module;
        this.id = idCounter++;
        this.inputPin = new InputPin(this);
        this.outputPin = new OutputPin(this);
    }

    @Override
    public String toString() {
        return "Identity" + id;
    }

    public List<Clause> convertToCnfAtTick(int tick) {
        Variable outputTrue = new TimeDependentVariable(tick, true, outputPin);
        Variable outputFalse = new TimeDependentVariable(tick, false, outputPin);

        Variable inputTrue = new TimeDependentVariable(tick, true, inputPin);
        Variable inputFalse = new TimeDependentVariable(tick, false, inputPin);

        Clause clause1 = new Clause(outputFalse, inputTrue);
        Clause clause2 = new Clause(outputTrue, inputFalse);

        return Arrays.asList(clause1, clause2);
    }

    @Override
    public Enums.Module getModule() {
        return module;
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
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Identity))return false;
        Identity other = (Identity)obj;
        return (this.inputPin == other.inputPin
                && this.outputPin == other.outputPin);
    }
}
