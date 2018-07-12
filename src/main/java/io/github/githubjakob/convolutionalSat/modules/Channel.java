package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.Noise;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;

import java.util.ArrayList;
import java.util.List;

public class Channel extends Module {

    private final Module encoder;

    private final Module decoder;

    private Noise noise;

    public Channel(Module encoder, Module decoder, Noise noise) {
        super(Enums.Module.CHANNEL);
        this.encoder = encoder;
        this.decoder = decoder;
        this.noise = noise;
        createConnections();
    }

    /**
     * Erzeugt die Verbindungen des Kanals, zwischen Ausgang des Encoders und Eingang des Decoders
     */
    private void createConnections() {
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
            connections.add(new NoisyConnection(outputPin, inputPin, noise));
            outputPins.add(outputPin);
            inputPins.add(inputPin);
        }
    }

    public List<Clause> convertModuleToCnf() {
        List<Clause> allClauses = new ArrayList<>();

        allClauses.addAll(convertConnectionsToCnf());

        return allClauses;
    }
}
