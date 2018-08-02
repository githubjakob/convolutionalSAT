package io.github.githubjakob.convolutionalSat.components;

import afu.org.checkerframework.checker.igj.qual.I;
import io.github.githubjakob.convolutionalSat.components.gates.*;

import javax.inject.Inject;
import javax.inject.Provider;

/**
 * Created by jakob on 02.08.18.
 */
public class ComponentFactory {

    private Provider<And> andProvider;
    private Provider<GlobalInput> globalInputProvider;
    private Provider<GlobalOutput> globalOutputProvider;
    private Provider<Identity> identityProvider;
    private Provider<Input> inputProvider;
    private Provider<Not> notProvider;
    private Provider<Output> outputProvider;
    private Provider<Register> registerProvider;
    private Provider<Xor> xorProvider;

    @Inject
    public ComponentFactory(Provider<And> andProvider, Provider<GlobalInput> globalInputProvider,
                            Provider<GlobalOutput> globalOutputProvider, Provider<Identity> identityProvider,
                            Provider<Input> inputProvider, Provider<Not> notProvider, Provider<Output> outputProvider,
                            Provider<Register> registerProvider, Provider<Xor> xorProvider) {
        this.andProvider = andProvider;
        this.globalInputProvider = globalInputProvider;
        this.globalOutputProvider = globalOutputProvider;
        this.identityProvider = identityProvider;
        this.inputProvider = inputProvider;
        this.notProvider = notProvider;
        this.outputProvider = outputProvider;
        this.registerProvider = registerProvider;
        this.xorProvider = xorProvider;
    }

    public And getAnd() {
        return andProvider.get();
    }

    public GlobalInput getGlobalInput() {
        return globalInputProvider.get();
    }

    public GlobalOutput getGlobalOutput() {
        return globalOutputProvider.get();
    }

    public Identity getIdentity() {
        return identityProvider.get();
    }

    public Input getInput() {
        return inputProvider.get();
    }

    public Not getNot() {
        return notProvider.get();
    }

    public Output getOutput() {
        return outputProvider.get();
    }

    public Register getRegister() {
        return registerProvider.get();
    }

    public Xor getXor() {
        return xorProvider.get();
    }
}