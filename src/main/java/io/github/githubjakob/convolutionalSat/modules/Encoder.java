package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.Clauses;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class Encoder extends AbstractModule {


    @Getter
    private final int numberOfOutputs;

    @Setter
    int[] inputBitStream;

    Input globalInput = new Input(Enums.Group.ENCODER);

    public List<Output> globalOutputs = new ArrayList<>();

    public Encoder(int numberOfOutputs) {
        group = Enums.Group.ENCODER;
        this.numberOfOutputs = numberOfOutputs;
        gates.add(globalInput);
        outputPins.add(globalInput.getOutputPin());

        for (int i = 0; i < numberOfOutputs; i++) {
            Output globalOutput = new Output(Enums.Group.ENCODER);
            globalOutputs.add(globalOutput);
            gates.add(globalOutput);
            inputPins.addAll(globalOutput.getInputPins());
        }
    }

    public int getNumberOfBits() {
        return inputBitStream.length;
    }

    public List<Clauses> convertCircuitToCnf() {
        List<Clauses> allClauses = new ArrayList<>();

        for (int tick = 0; tick < inputBitStream.length; tick++) {
            allClauses.add(convertConnectionsToCnf(tick));
            allClauses.add(convertInputBitStreamToCnf(tick));
            allClauses.add(convertGatesToCnf(tick));

        }

        return allClauses;
    }


    private Clauses convertInputBitStreamToCnf(int tick) {
        Clauses clausesForTick = new Clauses(tick);
        // für jedes bit
        boolean inputBit = inputBitStream[tick] == 1;
        Clause inputClause = new Clause(new TimeDependentVariable(tick, inputBit, globalInput.getOutputPin()));
        clausesForTick.addClause(inputClause);
        return clausesForTick;
    }

}
