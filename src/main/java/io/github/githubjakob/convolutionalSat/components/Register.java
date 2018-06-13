package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.Clauses;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;

import java.util.Arrays;
import java.util.List;

/**
 * Created by jakob on 31.05.18.
 */
public class Register implements Gate {

    private static int idCounter = 0;

    private final InputPin inputPin;

    private final OutputPin outputPin;

    private int id;

    public int in;

    public Register() {
        this.id = idCounter++;
        this.inputPin = new InputPin(this);
        this.outputPin = new OutputPin(this);
    }

    @Override
    public String toString() {
        return "Register" + id;
    }

    @Override
    public Clauses convertToCnfAtTick(int tick) {

        Clauses clausesAtTick = new Clauses(tick);

        if (tick == 0) {
            Variable variable = new TimeDependentVariable(tick, false, outputPin);
            Clause clause = new Clause(variable);
            clausesAtTick.addClause(clause);
        } else {
            /*
            Bedingungen:
           1. 22 => 11 (wenn output zu tick 2 true, dann muss input 1 zu tick 1 wahr sein)
           2. ~22 => ~11 (wenn output zu tick 2 false, dann muss input 1 zu tick 1 false sein)

           umformung ergibt
           (~22 v 11) u (22 v ~11)
             */

            int previousTick = tick - 1;

            Variable previousInputTrue = new TimeDependentVariable(previousTick, true, inputPin);
            Variable previousInputFalse = new TimeDependentVariable(previousTick, false, inputPin);

            Variable outputTrue = new TimeDependentVariable(tick, true, outputPin);
            Variable outputFalse = new TimeDependentVariable(tick, false, outputPin);

            Clause clause1 = new Clause(outputFalse, previousInputTrue);
            Clause clause2 = new Clause(outputTrue, previousInputFalse);

            Clauses clauses = new Clauses(tick, clause1, clause2);

            clausesAtTick.addAllClauses(clauses);
        }

        return clausesAtTick;
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
