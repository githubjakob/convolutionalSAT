package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;


public abstract class AbstractModule implements Module {

    @Getter
    Integer numberOfBits = null;

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

    Enums.Module module;


    public void addBitStream(BitStream bitStream) {
        bitstreams.add(bitStream);
        setNumberOfBits(bitStream.getLength());
    }

    private void setNumberOfBits(int numberOfBitsInInputStream) {
        int newNumber = numberOfBitsInInputStream;
        if (numberOfBits != null && !numberOfBits.equals(newNumber)) {
            throw new RuntimeException("Wrong lenght of input/output stream");
        }
        this.numberOfBits = newNumber;
    }

    public Output addOutput() {
        Output output = new Output(module);
        outputs.add(output);
        gates.add(output);
        inputPins.addAll(output.getInputPins());
        return output;
    }

     public Input addInput() {
        Input input = new Input(module);
        this.inputs.add(input);
        this.gates.add(input);
        this.outputPins.add(input.getOutputPin());
        return input;
    }

    public Register addRegister() {
        Register register = new Register(module);
        setupNewGate(register);
        return register;
    };

    public And addAnd() {
        And and = new And(module);
        setupNewGate(and);
        return and;
    };

    public Not addNot() {
        Not not = new Not(module);
        setupNewGate(not);
        return not;
    };

    public Xor addXor() {
        Xor xor = new Xor(module);
        setupNewGate(xor);
        return xor;
    };

    public Identity addIdentity() {
        Identity identity = new Identity(module);
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

    List<Clause> convertGatesToCnf(BitStream bitStream) {
        List<Clause> clausesForTick = new ArrayList<>();

        // für jedes Bauteil
        for (Gate gate : gates) {
            clausesForTick.addAll(gate.convertToCnf(bitStream));
        }
        return clausesForTick;
    }

    List<Clause> convertConnectionsToCnf(BitStream bitStream) {
        List<Clause> clausesForTick = new ArrayList<>();

        //für jede Verbindung
        for (Connection connection : connections) {
            clausesForTick.addAll(connection.convertToCnfAtTick(bitStream));
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
            clausesForTick.add(possibleConnections);
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
            clausesForTick.add(possibleConnections);

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
                    clausesForTick.add(exclude);
                }

            }
        }

        return clausesForTick;
    }

    List<Clause>  convertBitStreams() {
        List<Clause> clausesForTick = new ArrayList<>();
        if (bitstreams.size() > 0 ) {
            for (BitStream bitStream : bitstreams) {
                List<Clause> clauses = convertBitStreamToCnf(bitStream);
                clausesForTick.addAll(clauses);
            }
        }

        return clausesForTick;

    }

    private  List<Clause> convertBitStreamToCnf(BitStream bitStream) {
        List<Clause> clausesForTick = new ArrayList<>();

        List<Gate> gates = bitStream.getGates();

        for (Gate gate : gates) {
            if ("output".equals(gate.getType())) {
                for (Bit bit : bitStream) {
                    for (InputPin inputPin : gate.getInputPins()) {
                        Clause outputClause = new Clause(
                                new TimeDependentVariable(bit.getTick(), bitStream.getId(), bit.getBit(), inputPin));
                        clausesForTick.add(outputClause);
                    }

                }
            }

            if ("input".equals(gate.getType())) {
                for (Bit bit : bitStream) {
                    Clause outputClause = new Clause(
                            new TimeDependentVariable(bit.getTick(), bitStream.getId(), bit.getBit(), gate.getOutputPin()));
                    clausesForTick.add(outputClause);
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

    @Override
    public List<Clause> convertModuleToCnf() {

        List<Clause> allClauses = new ArrayList<>();

        for (BitStream bitStream : bitstreams) {
            allClauses.addAll(convertGatesToCnf(bitStream));
            allClauses.addAll(convertConnectionsToCnf(bitStream));

        }

        allClauses.addAll(convertBitStreams());

        return allClauses;

    };

    public List<Connection> getConnections() {
        return connections;
    }
}
