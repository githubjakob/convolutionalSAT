package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.Clauses;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import lombok.Getter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;


public class AbstractModule {

    @Getter
    Set<Gate> gates = new HashSet<>();

    List<Connection> connections = new ArrayList<>();

    List<InputPin> inputPins = new ArrayList<>();

    List<OutputPin> outputPins = new ArrayList<>();

    Enums.Group group;

    public Register addRegister() {
        Register register = new Register(group);
        setupNewGate(register);
        return register;
    };

    public Xor addXor() {
        Xor xor = new Xor(group);
        setupNewGate(xor);
        return xor;
    };

    public Identity addIdentity() {
        Identity identity = new Identity(group);
        setupNewGate(identity);
        return identity;
    }

    /**
     * Nach dem Hinzufügen eines neuen Gates zum Modul muss diese Methode aufgerufen werden
     *
     */
    private void setupNewGate(Gate newGate) {
        gates.add(newGate);
        inputPins.addAll(newGate.getInputPins());
        outputPins.add(newGate.getOutputPin());
        createNewConnectionsFor(newGate);
    }

    private void createNewConnectionsFor(Gate justCreated) {
        for (Gate gate : getAllComponentsWithOutputs()) {
            if (gate.equals(justCreated)) {
                continue;
            }

            for (InputPin xorInputPin : justCreated.getInputPins()) {
                connections.add(new Connection(gate.getOutputPin(), xorInputPin));
            }
        }

        for (Gate gate : getAllComponentsWithInputs()) {
            if (gate.equals(justCreated)) {
                continue;
            }

            for (InputPin componentInputPin : gate.getInputPins()) {
                connections.add(new Connection(justCreated.getOutputPin(), componentInputPin));
            }
        }
    }

    Clauses convertGatesToCnf(int tick) {
        Clauses clausesForTick = new Clauses(tick);

        // für jedes Bauteil
        for (Gate gate : gates) {
            clausesForTick.addAllClauses(gate.convertToCnfAtTick(tick));
        }
        return clausesForTick;
    }

    Clauses convertConnectionsToCnf(int tick) {
        Clauses clausesForTick = new Clauses(tick);

        //für jede Verbindung
        for (Connection connection : connections) {
            clausesForTick.addAllClauses(connection.convertToCnfAtTick(tick));
        }

        /**
         * Für alle Verbindungen, die vom selben Output Pin weg gehen, muss mindestens eine gesetzt sein.
         */
        for (OutputPin outputPin : outputPins) {
            // for alle Connections die von diesem Output Pin weg geht
            Clause possibleConnections = new Clause();
            for (Connection connection : connections) {
                if (connection.getFrom().equals(outputPin)) {
                    possibleConnections.addVariable(new Variable(true, connection));
                }
            }
            clausesForTick.addClause(possibleConnections);
        }


        for (InputPin inputPin : inputPins) {

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

        return clausesForTick;
    }

    public List<Gate> getAllComponentsWithOutputs() {
        return gates.stream().filter(gate -> !(gate instanceof Output)).collect(Collectors.toList());
    }

    public List<Gate> getAllComponentsWithInputs() {
        return gates.stream().filter(gate -> !(gate instanceof Input)).collect(Collectors.toList());
    }

}
