package io.github.githubjakob.convolutionalSat.components.gates;

import io.github.githubjakob.convolutionalSat.components.BitStream;
import io.github.githubjakob.convolutionalSat.components.InputPin;
import io.github.githubjakob.convolutionalSat.components.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 22.06.18.
 */
public class Not extends AbstractGate {
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

    public List<Clause> convertToCnf(BitStream bitStream) {
        List<Clause> clausesForAllTicks = new ArrayList<>();

            int bits = bitStream.getLength();
            for (int tick = 0; tick < bits; tick++) {
                Variable outputTrue = new BitAtComponentVariable(tick, bitStream.getId(),true, outputPin);
                Variable outputFalse = new BitAtComponentVariable(tick, bitStream.getId(),false, outputPin);

                Variable inputTrue = new BitAtComponentVariable(tick, bitStream.getId(),true, inputPin);
                Variable inputFalse = new BitAtComponentVariable(tick, bitStream.getId(),false, inputPin);

                Clause clause1 = new Clause(outputFalse, inputFalse);
                Clause clause2 = new Clause(outputTrue, inputTrue);

                clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));
            }


        List<Clause> microtickClauses = getMicrotickCnf(this.getModule().getNumberOfGates());
        clausesForAllTicks.addAll(microtickClauses);


        return clausesForAllTicks;
    }

    @Override
    public boolean evaluate(int tick) {
        Gate fromGate = inputPin.getConnection().getFrom().getGate();
        boolean value = !fromGate.evaluate(tick);
        //System.out.println("Value at " + this.toString() + " : " + value);
        return value;
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