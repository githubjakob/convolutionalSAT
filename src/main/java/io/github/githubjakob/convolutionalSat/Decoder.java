package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.Clauses;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Created by jakob on 14.06.18.
 */
public class Decoder {

    public final int numberOfOutputs;

    int[] outputBitStream;

    Set<Gate> gates = new HashSet<>();

    private List<Connection> connections = new ArrayList<>();

    private List<InputPin> inputPins = new ArrayList<>();

    private List<OutputPin> outputPins = new ArrayList<>();

    List<Input> globalInputs = new ArrayList<>();

    Output globalOutput = new Output(Enums.Group.DECODER);

    public Decoder(int numberOfInputs) {
        this.numberOfOutputs = numberOfInputs;
        gates.add(globalOutput);
        inputPins.addAll(globalOutput.getInputPins());

        for (int i = 0; i < numberOfInputs; i++) {
            Input globalInput = new Input(Enums.Group.DECODER);
            globalInputs.add(globalInput);
            gates.add(globalInput);
            outputPins.add(globalInput.getOutputPin());
        }
    }

    public void setOutputBitStream(int[] input) {
        this.outputBitStream = input;
    }

    public List<Connection> getConnections() {
        return this.connections;
    }

    public Register addRegister() {
        Register register = new Register(Enums.Group.DECODER);
        gates.add(register);
        inputPins.addAll(register.getInputPins());
        outputPins.add(register.getOutputPin());
        createNewConnectionsFor(register);
        return register;
    };

    public Xor addXor() {
        Xor xor = new Xor(Enums.Group.DECODER);
        gates.add(xor);
        inputPins.addAll(xor.getInputPins());
        outputPins.add(xor.getOutputPin());
        createNewConnectionsFor(xor);
        return xor;
    };

    public List<Gate> getGates() {
        return new ArrayList<>(this.gates);
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

    private Clauses convertCircuitToCnfForTick(int tick) {
        Clauses clausesForTick = new Clauses(tick);

        //für jede Verbindung
        for (Connection connection : connections) {
            clausesForTick.addAllClauses(connection.convertToCnfAtTick(tick));
        }

        // für jedes Bauteil
        for (Gate gate : gates) {
            clausesForTick.addAllClauses(gate.convertToCnfAtTick(tick));
        }

        // für jedes bit
        // todo
        // boolean inputBit = inputBitStream[tick] == 1;
        /*
        for (Input globalInput : globalInputs) {
            Clause inputClause = new Clause(new TimeDependentVariable(tick, inputBit, globalInput));
            clausesForTick.addClause(inputClause);
        }*/

        boolean outputBit = outputBitStream[tick] == 1;
        Clause outputClause = new Clause(new TimeDependentVariable(tick, outputBit, globalOutput.getInputPins().get(0)));
        clausesForTick.addClause(outputClause);

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

        // für jeden Input pin

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

    public List<Clauses> convertCircuitToCnf() {
        List<Clauses> allClauses = new ArrayList<>();

        for (int tick = 0; tick < outputBitStream.length; tick++) {
            allClauses.add(convertCircuitToCnfForTick(tick));
        }

        return allClauses;
    }

    public List<Gate> getAllComponentsWithOutputs() {
        return gates.stream().filter(gate -> !(gate instanceof Output)).collect(Collectors.toList());
    }

    public List<Gate> getAllComponentsWithInputs() {
        return gates.stream().filter(gate -> !(gate instanceof Input)).collect(Collectors.toList());
    }
}
