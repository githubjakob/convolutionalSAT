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

public class Decoder extends AbstractModule {

    @Setter
    private int[] outputBitStream;

    @Getter
    private List<Input> globalInputs = new ArrayList<>();

    private Output globalOutput = new Output(Enums.Group.DECODER);

    public Decoder(int numberOfInputs) {
        group = Enums.Group.DECODER;
        gates.add(globalOutput);
        inputPins.addAll(globalOutput.getInputPins());

        for (int i = 0; i < numberOfInputs; i++) {
            Input globalInput = new Input(Enums.Group.DECODER);
            globalInputs.add(globalInput);
            gates.add(globalInput);
            outputPins.add(globalInput.getOutputPin());
        }
    }

    public List<Clauses> convertModuleToCnf() {
        List<Clauses> allClauses = new ArrayList<>();

        for (int tick = 0; tick < outputBitStream.length; tick++) {
            allClauses.add(convertConnectionsToCnf(tick));
            allClauses.add(convertOutputBitStreamToCnf(tick));
            allClauses.add(convertGatesToCnf(tick));

        }

        return allClauses;
    }

    private Clauses convertOutputBitStreamToCnf(int tick) {
        Clauses clausesForTick = new Clauses(tick);

        // für jedes bit
        boolean outputBit = outputBitStream[tick] == 1;
        Clause outputClause = new Clause(new TimeDependentVariable(tick, outputBit, globalOutput.getInputPins().get(0)));
        clausesForTick.addClause(outputClause);
        return clausesForTick;
    }
}
