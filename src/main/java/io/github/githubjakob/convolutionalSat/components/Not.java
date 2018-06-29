package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 22.06.18.
 */
public class Not implements Gate {
    private static int idCounter = 0;

    private final Module module;

    private int id;

    public InputPin inputPin;

    public OutputPin outputPin;

    public Not(Module module) {
        this.module = module;
        this.id = idCounter++;
        this.inputPin = new InputPin(this);
        this.outputPin = new OutputPin(this);
    }

    @Override
    public String toString() {
        return "Not" + id;
    }

    public List<Clause> convertToCnf() {
        List<Clause> clausesForAllTicks = new ArrayList<>();

        for (BitStream bitStream : getModule().getBitstreams()) {
            int bits = bitStream.getLength();
            for (int tick = 0; tick < bits; tick++) {
                Variable outputTrue = new TimeDependentVariable(tick, bitStream.getId(),true, outputPin);
                Variable outputFalse = new TimeDependentVariable(tick, bitStream.getId(),false, outputPin);

                Variable inputTrue = new TimeDependentVariable(tick, bitStream.getId(),true, inputPin);
                Variable inputFalse = new TimeDependentVariable(tick, bitStream.getId(),false, inputPin);

                Clause clause1 = new Clause(outputFalse, inputFalse);
                Clause clause2 = new Clause(outputTrue, inputTrue);

                clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));
            }
        }



        return clausesForAllTicks;
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public String getType() {
        return "not";
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
        if (!(obj instanceof Not))return false;
        Not other = (Not)obj;
        return (this.inputPin == other.inputPin
                && this.outputPin == other.outputPin);
    }
}