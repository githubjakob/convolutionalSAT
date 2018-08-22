package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.ComponentFactory;
import org.bouncycastle.ocsp.Req;

import javax.inject.Inject;

/**
 * Created by jakob on 02.08.18.
 */
public class Encoder extends Module {
    @Inject
    public Encoder(ComponentFactory componentFactory, Requirements requirements) {
        super(componentFactory, requirements);
        this.type = Enums.Module.ENCODER;
        addGlobalInput();
        addOutputs(requirements.getNumberOfChannels());
        addRegisters();
        addAnds();
        addNots();
        addXors();
    }

    private void addNots() {
        for (int i = 0; i < requirements.getEnNot(); i++) {
            addNot();
        }
    }

    private void addAnds() {
        for (int i = 0; i < requirements.getEnAnd(); i++) {
            addAnd();
        }
    }

    private void addRegisters() {
        for (int i = 0; i < requirements.getEnReg(); i++) {
            addRegister();
        }
    }

    protected void addXors() {
        for (int i = 0; i < requirements.getEnXor(); i++) {
            addAnd();
        }
    }

    private void addOutputs(int n) {
        for (int i = 0; i < n; i++) {
            addOutput();
        }
    }
}
