package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.logic.Clause;

import java.util.ArrayList;
import java.util.List;

public class Encoder extends AbstractModule {

    public Encoder() {
        this.group = Enums.Group.ENCODER;
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
