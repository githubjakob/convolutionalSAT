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
        addNots();
        addAnds();
        addXors();
        addRegisters();
    }

    protected void addNots() {
        for (int i = 0; i < requirements.getDecNot(); i++) {
            addNot();
        }
    }

    protected void addAnds() {
        for (int i = 0; i < requirements.getDecAnd(); i++) {
            addAnd();
        }
    }

    protected void addXors() {
        for (int i = 0; i < requirements.getDecXor(); i++) {
            addAnd();
        }
    }

    private void addRegisters() {
        for (int i = 0; i < requirements.getDecReg(); i++) {
            addRegister();
        }
    }

    private void addInputs(int n) {
        for (int i = 0; i < n; i++) {
            addInput();
        }
    }
}
