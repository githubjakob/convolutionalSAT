package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.modules.AbstractModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class Output implements Gate {

    private static int idCounter = 0;

    private final Enums.Module module;

    private int id;

    private final InputPin inputPin;

    private final OutputPin outputPin;

    public Output(Enums.Module module) {
        this.module = module;
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
    public List<Clause> convertToCnf(BitStream bitStream) {
        List<Clause> clausesForAllTicks = new ArrayList<>();

        int bits = bitStream.getLength();
        for (int tick = 0; tick < bits; tick++) {
            TimeDependentVariable outputTrue = new TimeDependentVariable(tick, bitStream.getId(), true, outputPin);
            TimeDependentVariable outputFalse = new TimeDependentVariable(tick, bitStream.getId(), false, outputPin);

            TimeDependentVariable inputPinTrue = new TimeDependentVariable(tick, bitStream.getId(), true, inputPin);
            TimeDependentVariable inputPinFalse = new TimeDependentVariable(tick, bitStream.getId(), false, inputPin);

            Clause clause1 = new Clause(outputTrue, inputPinFalse);
            Clause clause2 = new Clause(outputFalse, inputPinTrue);

            clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));
        }

        return clausesForAllTicks;
    }

    @Override
    public Enums.Module getModule() {
        return module;
    }

    @Override
    public String getType() {
        return "output";
    }
}
