package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.ComponentFactory;

import javax.inject.Inject;

/**
 * Created by jakob on 02.08.18.
 */
public class Decoder extends Module {
    @Inject
    public Decoder(ComponentFactory gateFactory) {
        super(gateFactory);
        this.type = Enums.Module.DECODER;
    }
}
