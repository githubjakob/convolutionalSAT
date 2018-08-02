package io.github.githubjakob.convolutionalSat.components.gates;

import com.google.inject.Inject;
import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;

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

    private int id;

    private List<Boolean> outputBitValuesAtTick = new ArrayList<>(Arrays.asList(false));

    @Inject
    public Register(Requirements requirements) {
        this.requirements = requirements;
        this.id = idCounter++;
        this.inputPin = new InputPin(this);
        this.outputPin = new OutputPin(this);
    }

    @Override
    public String toString() {
        return "Register" + id;
    }

    @Override
    public List<Clause> getGateCnf() {
        List<Clause> clausesForAllTicks = new ArrayList<>();

        for (BitStream bitStream : requirements.getBitStreams()) {
            clausesForAllTicks.addAll(getGateCnf(bitStream));
        }

        return clausesForAllTicks;
    }

    private List<Clause> getGateCnf(BitStream bitStream) {

        List<Clause> clausesForAllTicks = new ArrayList<>();


            int bits = bitStream.getLengthWithDelay();
            for (int tick = 0; tick < bits; tick++) {
                List<Clause> clausesAtTick = new ArrayList<>();

                if (tick == 0) {
                    Variable variable = new BitAtComponentVariable(tick, bitStream.getId(), false, outputPin);
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

                    Variable previousInputTrue = new BitAtComponentVariable(previousTick, bitStream.getId(), true, inputPin);
                    Variable previousInputFalse = new BitAtComponentVariable(previousTick, bitStream.getId(), false, inputPin);

                    Variable outputTrue = new BitAtComponentVariable(tick, bitStream.getId(), true, outputPin);
                    Variable outputFalse = new BitAtComponentVariable(tick, bitStream.getId(), false, outputPin);

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
    public boolean evaluate(int tick) {
        //System.out.println("Evaluate Register " + id + ", tick "+ tick + ", size: " + outputBitValuesAtTick.size());

        if (outputBitValuesAtTick.size() - 2 == tick) {
            //System.out.println("using value from memory");
            return outputBitValuesAtTick.get(tick);
        }

        /**
         * add a null first to indictate that evaluate was called once allready
         * prevents circuits in the evulation
         *
         */
        Gate fromGate = inputPin.getConnection().getFrom().getGate();
        outputBitValuesAtTick.add(null);
        boolean inputValueAtThisTick = fromGate.evaluate(tick);
        outputBitValuesAtTick.set(outputBitValuesAtTick.size()-1, inputValueAtThisTick);

        return outputBitValuesAtTick.get(tick);
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

    public void reset() {
        outputBitValuesAtTick = new ArrayList<>(Arrays.asList(false));
    }


}
