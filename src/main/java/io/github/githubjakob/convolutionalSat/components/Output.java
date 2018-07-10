package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class Output extends AbstractGate {

    private static int idCounter = 0;

    private final Module module;

    private int id;

    private final InputPin inputPin;

    private final OutputPin outputPin;

    public Output(Module module) {
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
    public List<Clause> convertToCnf() {
        List<Clause> clausesForAllTicks = new ArrayList<>();

        for (BitStream bitStream : this.getModule().getBitstreams()) {
            int bits = bitStream.getLength();
            for (int tick = 0; tick < bits; tick++) {
                BitAtComponentVariable outputTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, outputPin);
                BitAtComponentVariable outputFalse = new BitAtComponentVariable(tick, bitStream.getId(), false, outputPin);

                BitAtComponentVariable inputPinTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, inputPin);
                BitAtComponentVariable inputPinFalse = new BitAtComponentVariable(tick, bitStream.getId(), false, inputPin);

                Clause clause1 = new Clause(outputTrue, inputPinFalse);
                Clause clause2 = new Clause(outputFalse, inputPinTrue);

                clausesForAllTicks.addAll(Arrays.asList(clause1, clause2));

            }
        }

        List<Clause> microtickClauses = getMicrotickCnf(this.getModule().getNumberOfGates());
        clausesForAllTicks.addAll(microtickClauses);


        return clausesForAllTicks;
    }

    @Override
    public Module getModule() {
        return module;
    }

    @Override
    public String getType() {
        return "output";
    }
}
