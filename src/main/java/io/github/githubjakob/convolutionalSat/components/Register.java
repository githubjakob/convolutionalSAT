package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public class Register extends AbstractGate {

    private static int idCounter = 0;

    private final InputPin inputPin;

    private final OutputPin outputPin;

    private final Module module;

    private int id;

    public int in;

    public Register(Module module) {
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
    public List<Clause> convertToCnf() {

        List<Clause> clausesForAllTicks = new ArrayList<>();

        List<BitStream> bitStreams = this.module.getBitstreams();

        for (BitStream bitStream : bitStreams) {

            int bits = bitStream.getLength();
            for (int tick = 0; tick < bits; tick++) {
                List<Clause> clausesAtTick = new ArrayList<>();

                if (tick == 0) {
                    ConnectionVariable variable = new BitAtComponentVariable(tick, bitStream.getId(), false, outputPin);
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

                    ConnectionVariable previousInputTrue = new BitAtComponentVariable(previousTick, bitStream.getId(), true, inputPin);
                    ConnectionVariable previousInputFalse = new BitAtComponentVariable(previousTick, bitStream.getId(), false, inputPin);

                    ConnectionVariable outputTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, outputPin);
                    ConnectionVariable outputFalse = new BitAtComponentVariable(tick, bitStream.getId(), false, outputPin);

                    Clause clause1 = new Clause(outputFalse, previousInputTrue);
                    Clause clause2 = new Clause(outputTrue, previousInputFalse);

                    List<Clause> clauses = Arrays.asList(clause1, clause2);

                    clausesAtTick.addAll(clauses);
                }

                clausesForAllTicks.addAll(clausesAtTick);
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
