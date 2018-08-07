package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.ComponentFactory;

import javax.inject.Inject;

/**
 * Created by jakob on 02.08.18.
 */
public class Decoder extends Module {
    @Inject
    public Decoder(ComponentFactory componentFactory, Requirements requirements) {
        super(componentFactory, requirements);
        this.type = Enums.Module.DECODER;
        addGlobalOutput();
        addInputs(requirements.getNumberOfChannels());
    }

    private void addInputs(int n) {
        for (int i = 0; i < n; i++) {
            addInput();
        }
    }
}
