package io.github.githubjakob.convolutionalSat.components.gates;

import io.github.githubjakob.convolutionalSat.components.BitStream;
import io.github.githubjakob.convolutionalSat.components.InputPin;
import io.github.githubjakob.convolutionalSat.components.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Setter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 07.06.18.
 */
public class Input extends AbstractGate {

    private static int idCounter = 0;

    private final Module module;

    private int id;

    private final OutputPin outputPin;

    private final InputPin inputPin;

    @Setter
    private Boolean bitValue = null;

    public Input(Module module) {
        this.module = module;
        this.id = idCounter;
        idCounter++;
        this.outputPin = new OutputPin(this);
        this.inputPin = new InputPin(this);
    }

    @Override
    public String toString() {
        return "Input" + id;
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
                BitAtComponentVariable outputTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, inputPin);
                BitAtComponentVariable outputFalse = new BitAtComponentVariable(tick, bitStream.getId(), false, inputPin);

                BitAtComponentVariable outputPinTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, outputPin);
                BitAtComponentVariable outputPinFalse = new BitAtComponentVariable(tick, bitStream.getId(),false, outputPin);

                Clause clause1 = new Clause(outputTrue, outputPinFalse);
                Clause clause2 = new Clause(outputFalse, outputPinTrue);
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
    public boolean evaluate(int tick) {
        boolean value;
        if (bitValue != null) {
            value = bitValue;
        } else {
            value = inputPin.getConnection().getFrom().getGate().evaluate(tick);
        }
        //System.out.println("Value at " + this.toString() + " : " + value);
        return value;
    }

    @Override
    public String getType() {
        return "input";
    }

}
