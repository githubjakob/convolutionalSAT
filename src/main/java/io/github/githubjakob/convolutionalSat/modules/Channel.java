package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.Noise;
import io.github.githubjakob.convolutionalSat.Requirements;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.connection.NoiseFreeConnection;
import io.github.githubjakob.convolutionalSat.components.connection.NoisyConnection;
import io.github.githubjakob.convolutionalSat.components.gates.Input;
import io.github.githubjakob.convolutionalSat.components.gates.Output;
import lombok.Getter;

public class Channel extends Module {

    private final Module encoder;

    private final Module decoder;

    @Getter
    private Noise noise;

    public Channel(Module encoder, Module decoder) {
        super(Enums.Module.CHANNEL);
        this.encoder = encoder;
        this.decoder = decoder;
        this.noise = new Noise();
        createConnectionsForChannel();
    }

    /**
     * Erzeugt die Verbindungen des Kanals, zwischen Ausgang des Encoders und Eingang des Decoders
     */
    private void createConnectionsForChannel() {
        int numberOfChannels = 0;
        if (encoder.getOutputs().size() == decoder.getInputs().size()) {
            numberOfChannels = encoder.getOutputs().size();
        } else {
            throw new RuntimeException("unequal channels");
        }
        for (int i = 0; i < numberOfChannels; i++) {
            Output output = encoder.getOutputs().get(i);
            Input input = decoder.getInputs().get(i);
            OutputPin outputPin = output.getOutputPin();
            InputPin inputPin = input.getInputPins().get(0);

            if (noise.isNoiseEnabled()) {
                connections.add(new NoisyConnection(outputPin, inputPin, noise));
            } else {
                connections.add(new NoiseFreeConnection(outputPin, inputPin));
            }
            outputPins.add(outputPin);
            inputPins.add(inputPin);
        }
    }
}
