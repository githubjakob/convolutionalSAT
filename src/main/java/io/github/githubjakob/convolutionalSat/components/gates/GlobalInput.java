package io.github.githubjakob.convolutionalSat.components.gates;

import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.modules.Module;

import javax.inject.Inject;

/**
 * Created by jakob on 24.07.18.
 */
public class GlobalInput extends Input {

    @Inject
    public GlobalInput(Requirements requirements) {
        super(requirements);
    }
}
