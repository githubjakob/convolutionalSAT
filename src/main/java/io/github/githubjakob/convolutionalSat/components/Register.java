package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import io.github.githubjakob.convolutionalSat.modules.AbstractModule;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public class Register implements Gate {

    private static int idCounter = 0;

    private final InputPin inputPin;

    private final OutputPin outputPin;

    private final Enums.Module module;

    private int id;

    public int in;

    public Register(Enums.Module module) {
        this.module = module;
        this.id = idCounter++;
        this.inputPin = new InputPin(this);
        this.outputPin = new OutputPin(this);
    }

    @Override
    public String toString() {
        return "Register" + id;
    }

    @Override
    public List<Clause> convertToCnf(BitStream bitStream) {

        List<Clause> clausesForAllTicks = new ArrayList<>();

        int bits = bitStream.getLength();
        for (int tick = 0; tick < bits; tick++) {
            List<Clause> clausesAtTick = new ArrayList<>();

            if (tick == 0) {
                Variable variable = new TimeDependentVariable(tick, bitStream.getId(), false, outputPin);
                Clause clause = new Clause(variable);
                clausesAtTick.add(clause);
            } else {
            /*
            Bedingungen:
           1. 22 => 11 (wenn output zu tick 2 true, dann muss input 1 zu tick 1 wahr sein)
           2. ~22 => ~11 (wenn output zu tick 2 false, dann muss input 1 zu tick 1 false sein)

           umformung ergibt
           (~22 v 11) u (22 v ~11)
             */

                int previousTick = tick - 1;

                Variable previousInputTrue = new TimeDependentVariable(previousTick, bitStream.getId(), true, inputPin);
                Variable previousInputFalse = new TimeDependentVariable(previousTick, bitStream.getId(), false, inputPin);

                Variable outputTrue = new TimeDependentVariable(tick, bitStream.getId(), true, outputPin);
                Variable outputFalse = new TimeDependentVariable(tick, bitStream.getId(), false, outputPin);

                Clause clause1 = new Clause(outputFalse, previousInputTrue);
                Clause clause2 = new Clause(outputTrue, previousInputFalse);

                List<Clause> clauses = Arrays.asList(clause1, clause2);

                clausesAtTick.addAll(clauses);
            }

            clausesForAllTicks.addAll(clausesAtTick);
        }


        return clausesForAllTicks;
    }

    @Override
    public Enums.Module getModule() {
        return module;
    }

    @Override
    public String getType() {
        return "register";
    }


    @Override
    public OutputPin getOutputPin() {
        return outputPin;
    }

    @Override
    public List<InputPin> getInputPins() {
        return Arrays.asList(inputPin);
    }


}
