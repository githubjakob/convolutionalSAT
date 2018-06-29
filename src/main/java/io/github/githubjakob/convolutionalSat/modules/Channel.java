package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;

import java.util.ArrayList;
import java.util.List;

public class Channel extends Module {

    private final Module encoder;

    private final Module decoder;

    public Channel(Module encoder, Module decoder) {
        super(Enums.Module.CHANNEL);
        this.encoder = encoder;
        this.decoder = decoder;
        createConnections();
    }

    /**
     * Erzeugt die Verbindungen des Kanals, zwischen Ausgang des Encoders und Eingang des Decoders
     */
    private void createConnections() {
        for (Output output : encoder.getOutputs()) {
            for (Input input : decoder.getInputs()) {
                OutputPin outputPin = output.getOutputPin();
                InputPin inputPin = input.getInputPins().get(0);
                connections.add(new Connection(outputPin, inputPin));
                outputPins.add(outputPin);
                inputPins.add(inputPin);
            }
        }
    }

    public List<Clause> convertModuleToCnf() {
        List<Clause> allClauses = new ArrayList<>();

        allClauses.addAll(convertConnectionsToCnf());

        return allClauses;
    }
}
