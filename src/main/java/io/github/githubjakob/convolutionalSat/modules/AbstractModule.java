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

    HashMap<Input, int[]> inputBitStreams = new HashMap<>();

    HashMap<Output, int[]> outputBitStreams = new HashMap<>();

    @Getter
    List<Input> inputs = new ArrayList<>();

    @Getter
    List<Output> outputs = new ArrayList<>();

    @Getter
    List<Gate> gates = new ArrayList<>();

    List<Connection> connections = new ArrayList<>();

    List<InputPin> inputPins = new ArrayList<>();

    List<OutputPin> outputPins = new ArrayList<>();

    Enums.Group group;


    public void addInputBitStream(int[] inputBitStream, Input input) {
        inputBitStreams.put(input, inputBitStream);
        setNumberOfBits(inputBitStream);
    }

    public void addOutputBitStream(int[] outputBitStream, Output output) {
        outputBitStreams.put(output, outputBitStream);
        setNumberOfBits(outputBitStream);
    }

    private void setNumberOfBits(int[] bitStream) {
        int newNumber = bitStream.length;
        if (numberOfBits != null && !numberOfBits.equals(newNumber)) {
            throw new RuntimeException("Wrong lenght of input/output stream");
        }
        this.numberOfBits = newNumber;
    }

    public Output addOutput() {
        Output output = new Output(group);
        outputs.add(output);
        gates.add(output);
        inputPins.addAll(output.getInputPins());
        return output;
    }

     public Input addInput() {
        Input input = new Input(group);
        this.inputs.add(input);
        this.gates.add(input);
        this.outputPins.add(input.getOutputPin());
        return input;
    }

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

    List<Clause> convertGatesToCnf(int tick) {
        List<Clause> clausesForTick = new ArrayList<>();

        // für jedes Bauteil
        for (Gate gate : gates) {
            clausesForTick.addAll(gate.convertToCnfAtTick(tick));
        }
        return clausesForTick;
    }

    List<Clause> convertConnectionsToCnf(int tick) {
        List<Clause> clausesForTick = new ArrayList<>();

        //für jede Verbindung
        for (Connection connection : connections) {
            clausesForTick.addAll(connection.convertToCnfAtTick(tick));
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

    List<Clause>  convertBitStreams(int tick) {
        List<Clause> clausesForTick = new ArrayList<>();
        if (inputBitStreams.size() > 0 ) {
            for (Map.Entry<Input, int[]> entry : inputBitStreams.entrySet()) {
                List<Clause> clauses = convertInputBitStream(tick, entry);
                clausesForTick.addAll(clauses);
            }

        }

        if (outputBitStreams.size() > 0 ) {
            for (Map.Entry<Output, int[]> entry : outputBitStreams.entrySet()) {
                List<Clause> clauses = convertOutputBitStream(tick, entry);
                clausesForTick.addAll(clauses);
            }

        }

        return clausesForTick;

    }

    private  List<Clause> convertOutputBitStream(int tick, Map.Entry<Output, int[]> entry) {
        List<Clause> clausesForTick = new ArrayList<>();

        // für jedes bit
        boolean outputBit = entry.getValue()[tick] == 1;
        for (InputPin inputPin : entry.getKey().getInputPins()) {
            Clause outputClause = new Clause(new TimeDependentVariable(tick, outputBit, inputPin));
            clausesForTick.add(outputClause);
        }

        return clausesForTick;
    }

    private List<Clause> convertInputBitStream(int tick, Map.Entry<Input, int[]> entry) {
        List<Clause> clausesForTick = new ArrayList<>();
        // für jedes bit
        boolean inputBit = entry.getValue()[tick] == 1;
        Clause inputClause = new Clause(new TimeDependentVariable(tick, inputBit, entry.getKey().getOutputPin()));
        clausesForTick.add(inputClause);
        return clausesForTick;
    }

    public List<Gate> getAllComponentsWithOutputs() {
        return gates.stream().filter(gate -> !(gate instanceof Output)).collect(Collectors.toList());
    }

    public List<Gate> getAllComponentsWithInputs() {
        return gates.stream().filter(gate -> !(gate instanceof Input)).collect(Collectors.toList());
    }

    @Override
    public abstract List<Clause> convertModuleToCnf();
}
