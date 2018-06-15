package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clauses;

import java.util.ArrayList;
import java.util.List;

public class Channel extends AbstractModule {

    private final Encoder encoder;

    private final Decoder decoder;

    private int numberOfBits;

    public Channel(Encoder encoder, Decoder decoder) {
        this.encoder = encoder;
        this.numberOfBits = encoder.getNumberOfBits();
        this.decoder = decoder;

        createConnections();
    }

    /**
     * Erzeugt die Verbindungen des Kanals, zwischen Ausgang des Encoders und Eingang des Decoders
     */
    private void createConnections() {
        for (Output output : encoder.globalOutputs) {
            for (Input input : decoder.getGlobalInputs()) {
                OutputPin outputPin = output.getOutputPin();
                InputPin inputPin = input.getInputPins().get(0);
                connections.add(new Connection(outputPin, inputPin));
                outputPins.add(outputPin);
                inputPins.add(inputPin);
            }
        }
    }

    public List<Clauses> convertModuleToCnf() {
        List<Clauses> allClauses = new ArrayList<>();

        for (int tick = 0; tick < numberOfBits; tick++) {
            allClauses.add(convertConnectionsToCnf(tick));
        }

        return allClauses;
    }
}