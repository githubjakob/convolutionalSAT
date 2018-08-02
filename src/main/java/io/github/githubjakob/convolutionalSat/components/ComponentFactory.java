package io.github.githubjakob.convolutionalSat.components;

import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.connections.Connection;
import io.github.githubjakob.convolutionalSat.components.connections.NoiseFreeConnection;
import io.github.githubjakob.convolutionalSat.components.connections.NoisyChannelConnection;
import io.github.githubjakob.convolutionalSat.components.gates.*;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;

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
    private Requirements requirements;

    @Inject
    public ComponentFactory(Provider<And> andProvider, Provider<GlobalInput> globalInputProvider,
                            Provider<GlobalOutput> globalOutputProvider, Provider<Identity> identityProvider,
                            Provider<Input> inputProvider, Provider<Not> notProvider, Provider<Output> outputProvider,
                            Provider<Register> registerProvider, Provider<Xor> xorProvider,
                            Requirements requirements) {
        this.andProvider = andProvider;
        this.globalInputProvider = globalInputProvider;
        this.globalOutputProvider = globalOutputProvider;
        this.identityProvider = identityProvider;
        this.inputProvider = inputProvider;
        this.notProvider = notProvider;
        this.outputProvider = outputProvider;
        this.registerProvider = registerProvider;
        this.xorProvider = xorProvider;
        this.requirements = requirements;
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

    public Connection createNoisyConnectionIfNoiseEnabled(OutputPin outputPin, InputPin inputPin) {
        Connection connection;
        if (requirements.isNoiseEnabled()) {
            connection = new NoisyChannelConnection(outputPin, inputPin, requirements);
        } else {
            connection = new NoiseFreeConnection(outputPin, inputPin, requirements);
        }
        return connection;
    }

    public Connection createNoiseFreeConnection(OutputPin outputPin, InputPin inputPin) {
        return new NoiseFreeConnection(outputPin, inputPin, requirements);
    }
}
