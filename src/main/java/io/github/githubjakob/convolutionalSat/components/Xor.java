package io.github.githubjakob.convolutionalSat.components;


import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public class Xor implements Gate {

    private static int idCounter = 0;

    private final Enums.Group group;

    private int id;

    public InputPin inputPin1;

    public InputPin inputPin2;

    public OutputPin outputPin;

    public Xor(Enums.Group group) {
        this.group = group;
        this.id = idCounter++;
        this.inputPin1 = new InputPin(this);
        this.inputPin2 = new InputPin(this);
        this.outputPin = new OutputPin(this);
    }

    @Override
    public String toString() {
        return "Xor" + id;
    }

    public List<Clause> convertToCnfAtTick(int tick) {
        Variable outputTrue = new TimeDependentVariable(tick, true, outputPin);
        Variable outputFalse = new TimeDependentVariable(tick, false, outputPin);

        Variable input1True = new TimeDependentVariable(tick, true, inputPin1);
        Variable input1False = new TimeDependentVariable(tick, false, inputPin1);

        Variable input2True = new TimeDependentVariable(tick, true, inputPin2);
        Variable input2False = new TimeDependentVariable(tick, false, inputPin2);

        Clause clause1 = new Clause(outputFalse, input1False, input2False);
        Clause clause2 = new Clause(outputFalse, input1True, input2True);
        Clause clause3 = new Clause(outputTrue, input1False, input2True);
        Clause clause4 = new Clause(outputTrue, input1True, input2False);

        return Arrays.asList(clause1, clause2, clause3, clause4);
    }

    @Override
    public Enums.Group getGroup() {
        return group;
    }

    @Override
    public String getType() {
        return "xor";
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
        if (!(obj instanceof Xor))return false;
        Xor other = (Xor)obj;
        return (this.inputPin1 == other.inputPin1
            && this.inputPin2 == other.inputPin2
            && this.outputPin == other.outputPin);
    }
}
