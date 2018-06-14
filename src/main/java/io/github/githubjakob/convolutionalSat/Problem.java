package io.github.githubjakob.convolutionalSat;


import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.Clauses;
import io.github.githubjakob.convolutionalSat.logic.Variable;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jakob on 31.05.18.
 */
public class Problem {

    Encoder encoder;

    Decoder decoder;

    List<Connection> connections = new ArrayList<>();

    List<OutputPin> inputPins = new ArrayList<>();

    List<InputPin> outputPins = new ArrayList<>();

    public int[] inputBitStream;

    public Problem(Encoder encoder, Decoder decoder, int[] inputBitStream) {
        this.inputBitStream = inputBitStream;
        this.encoder = encoder;
        encoder.setInptBitStream(inputBitStream);
        this.decoder = decoder;
        decoder.setOutputBitStream(inputBitStream);

        // für jedes output von encoder: verbindung zu input von decoder
        for (Output output : encoder.globalOutputs) {
            for (Input input : decoder.globalInputs) {
                OutputPin outputPin = output.getOutputPin();
                InputPin inputPin = input.getInputPins().get(0);
                connections.add(new Connection(outputPin, inputPin));
                inputPins.add(outputPin);
                outputPins.add(inputPin);
            }
        }

    }

    public void addInputBitStream(int[] input) {
        this.inputBitStream = input;
    }

    public List<Clauses> convertProblemToCnf() {

        List<Clauses> cnf = new ArrayList<>();

        for (int tick = 0; tick < inputBitStream.length; tick++) {

            Clauses clausesForTick = new Clauses(tick);

            // für jede verbindung zwischen encoder und decoder
            for (Connection connection : connections) {
                clausesForTick.addAllClauses(connection.convertToCnfAtTick(tick));
            }

            /**
             * Für alle Verbindungen, die vom selben Output Pin weg gehen, muss mindestens eine gesetzt sein.
             */
            for (OutputPin outputPin : inputPins) {
                // for alle Connections die von diesem Output Pin weg geht
                Clause possibleConnections = new Clause();
                for (Connection connection : connections) {
                    if (connection.getFrom().equals(outputPin)) {
                        possibleConnections.addVariable(new Variable(true, connection));
                    }
                }
                clausesForTick.addClause(possibleConnections);
            }

            // für jeden Input pin

            for (InputPin inputPin : outputPins) {

                List<Connection> connectionsWithSameTo = connections.stream().filter(connection -> connection.getTo().equals(inputPin)).collect(Collectors.toList());

                /**
                 * Für alle Verbindungen, die zum selben Input Pin führen, muss mindestens eine gesetzt sein.
                 */
                Clause possibleConnections = new Clause();
                for (Connection connection : connectionsWithSameTo) {
                    possibleConnections.addVariable(new Variable(true, connection));

                }
                clausesForTick.addClause(possibleConnections);

                /**
                 * Für eine bestimmte Verbindung zu einem Input Pin, dürfen alle anderen Verbindungen zum selben Pin nicht gesetzt sein.
                 */
                for (Connection connection : connectionsWithSameTo) {
                    for (Connection other : connectionsWithSameTo) {
                        if (connection.equals(other)) {
                            continue;
                        }
                        Variable connectionFalse = new Variable(false, connection);
                        Variable otherFalse = new Variable(false, other);
                        Clause exclude = new Clause(connectionFalse, otherFalse);
                        clausesForTick.addClause(exclude);
                    }

                }
            }

            cnf.add(clausesForTick);

        }



        List<Clauses> encoder = this.encoder.convertCircuitToCnf();
        cnf.addAll(encoder);

        List<Clauses> decoder = this.decoder.convertCircuitToCnf();
        cnf.addAll(decoder);

        return cnf;

    }

    public List<Gate> getGates() {
        List<Gate> gates = new ArrayList<>();
        gates.addAll(encoder.getGates());
        gates.addAll(decoder.getGates());
        return gates;
    }
}
