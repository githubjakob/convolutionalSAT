package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.components.Gate;
import io.github.githubjakob.convolutionalSat.logic.Clause;

import java.util.List;

public interface Module {
    List<Clause> convertModuleToCnf();

    List<Gate> getGates();
}
