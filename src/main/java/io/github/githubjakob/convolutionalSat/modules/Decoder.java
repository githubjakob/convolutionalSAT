package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.logic.Clause;

import java.util.ArrayList;
import java.util.List;

public class Decoder extends AbstractModule {

    public Decoder() {
        module = Enums.Module.DECODER;
    }

    public List<Clause> convertModuleToCnf() {
        List<Clause> allClauses = new ArrayList<>();

        for (int tick = 0; tick < getNumberOfBits(); tick++) {
            allClauses.addAll(convertConnectionsToCnf(tick));
            allClauses.addAll(convertBitStreams(tick));
            allClauses.addAll(convertGatesToCnf(tick));

        }

        return allClauses;
    }
}
