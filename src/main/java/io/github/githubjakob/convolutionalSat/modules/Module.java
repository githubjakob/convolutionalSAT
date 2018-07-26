package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.gates.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import io.github.githubjakob.convolutionalSat.logic.Property;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;


public class Module implements Property {

    @Getter
    List<BitStream> bitstreams = new ArrayList<>();

    @Getter
    List<Input> inputs = new ArrayList<>();

    @Getter
    List<Output> outputs = new ArrayList<>();

    @Getter
    List<Gate> gates = new ArrayList<>();

    List<Connection> connections = new ArrayList<>();

    List<InputPin> inputPins = new ArrayList<>();

    List<OutputPin> outputPins = new ArrayList<>();

    @Getter
    Enums.Module type;

    public Module(Enums.Module type) {
        this.type = type;
    }

    public void addBitStream(BitStream bitStream) {
        bitstreams.add(bitStream);
    }

    public Output addOutput() {
        Output output = new Output(this);
        outputs.add(output);
        gates.add(output);
        inputPins.addAll(output.getInputPins());
        return output;
    }

    public Output addGlobalOutput() {
        Output output = new GlobalOutput(this);
        outputs.add(output);
        gates.add(output);
        inputPins.addAll(output.getInputPins());
        return output;
    }

     public Input addInput() {
        Input input = new Input(this);
        this.inputs.add(input);
        this.gates.add(input);
        this.outputPins.add(input.getOutputPin());
        return input;
    }

    public Input addGlobalInput() {
        Input input = new GlobalInput(this);
        this.inputs.add(input);
        this.gates.add(input);
        this.outputPins.add(input.getOutputPin());
        return input;
    }

    public Register addRegister() {
        Register register = new Register(this);
        setupNewGate(register);
        return register;
    };

    public And addAnd() {
        And and = new And(this);
        setupNewGate(and);
        return and;
    };

    public Not addNot() {
        Not not = new Not(this);
        setupNewGate(not);
        return not;
    };

    public Xor addXor() {
        Xor xor = new Xor(this);
        setupNewGate(xor);
        return xor;
    };

    public Identity addIdentity() {
        Identity identity = new Identity(this);
        setupNewGate(identity);
        return identity;
    }

    public int getNumberOfGates() {
        return gates.size() +2;
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

            for (InputPin inputPin : justCreated.getInputPins()) {
                connections.add(new Connection(gate.getOutputPin(), inputPin));
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

    List<Clause> convertGatesToCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        // für jedes Bauteil
        for (Gate gate : gates) {
            clausesForTick.addAll(gate.convertToCnf());
        }
        return clausesForTick;
    }

    List<Clause> convertConnectionsToCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        //für jede Verbindung
        for (Connection connection : connections) {
            clausesForTick.addAll(connection.convertToCnfAtTick(getNumberOfGates()));
        }

        /**
         * Für alle Verbindungen, die vom selben Output Pin weg gehen, muss mindestens eine gesetzt sein.
         */
        /*for (OutputPin outputPin : outputPins) {
            // for alle Connections die von diesem Output Pin weg geht
            Clause possibleConnections = new Clause();
            for (Connection connections : connections) {
                if (connections.getFrom().equals(outputPin)) {
                    possibleConnections.addVariable(new ConnectionVariable(true, connections));
                }
            }
            clausesForTick.add(possibleConnections);
        }*/


        for (InputPin inputPin : inputPins) {

            List<Connection> connectionsWithSameTo = connections.stream().filter(connection -> connection.getTo().equals(inputPin)).collect(Collectors.toList());

            /**
             * Für alle Verbindungen, die zum selben Input Pin führen, muss mindestens eine gesetzt sein.
             */
            Clause possibleConnections = new Clause();
            for (Connection connection : connectionsWithSameTo) {
                possibleConnections.addVariable(new ConnectionVariable(true, connection));

            }
            clausesForTick.add(possibleConnections);

            /**
             * Für eine bestimmte Verbindung zu einem Input Pin, dürfen alle anderen Verbindungen zum selben Pin nicht gesetzt sein.
             */
            for (Connection connection : connectionsWithSameTo) {
                for (Connection other : connectionsWithSameTo) {
                    if (connection.equals(other)) {
                        continue;
                    }
                    ConnectionVariable connectionFalse = new ConnectionVariable(false, connection);
                    ConnectionVariable otherFalse = new ConnectionVariable(false, other);
                    Clause exclude = new Clause(connectionFalse, otherFalse);
                    clausesForTick.add(exclude);
                }

            }
        }

        return clausesForTick;
    }

    List<Clause> convertBitStreamsToCnf() {
        if (bitstreams.size() == 0 ) {
            return new ArrayList<>();
        }

        List<Clause> clausesForTick = new ArrayList<>();

        for (BitStream bitStream : bitstreams) {
            clausesForTick.addAll(bitStream.toCnf());
        }

        return clausesForTick;

    }

    public List<Gate> getAllComponentsWithOutputs() {
        return gates.stream().filter(gate -> !(gate instanceof Output)).collect(Collectors.toList());
    }

    public List<Gate> getAllComponentsWithInputs() {
        return gates.stream().filter(gate -> !(gate instanceof Input)).collect(Collectors.toList());
    }

    @Override
    public List<Clause> toCnf() {

        List<Clause> allClauses = new ArrayList<>();

        allClauses.addAll(convertGatesToCnf());
        allClauses.addAll(convertConnectionsToCnf());
        allClauses.addAll(convertBitStreamsToCnf());

        return allClauses;

    };

    public List<Connection> getConnections() {
        return connections;
    }
}
