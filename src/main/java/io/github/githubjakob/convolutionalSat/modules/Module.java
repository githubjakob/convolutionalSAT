package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.gates.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;


public class Module {

    @Getter
    BitStream bitStream;

    @Getter
    List<Input> inputs = new ArrayList<>();

    @Getter
    List<Output> outputs = new ArrayList<>();

    @Getter
    List<Gate> gates = new ArrayList<>();

    List<Connection> connections = new ArrayList<>();

    @Getter
    List<InputPin> inputPins = new ArrayList<>();

    @Getter
    List<OutputPin> outputPins = new ArrayList<>();

    @Getter
    Enums.Module type;

    public Module(Enums.Module type) {
        this.type = type;
    }

    public void setBitStream(BitStream bitStream) {
        this.bitStream = bitStream;
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
        System.out.println("Microticks in Module " + gates.size() + 2);
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
                connections.add(new NoiseFreeConnection(gate.getOutputPin(), inputPin));
            }
        }

        for (Gate gate : getAllComponentsWithInputs()) {
            if (gate.equals(justCreated)) {
                continue;
            }

            for (InputPin componentInputPin : gate.getInputPins()) {
                connections.add(new NoiseFreeConnection(justCreated.getOutputPin(), componentInputPin));
            }
        }
    }



    public List<Gate> getAllComponentsWithOutputs() {
        return gates.stream().filter(gate -> !(gate instanceof Output)).collect(Collectors.toList());
    }

    public List<Gate> getAllComponentsWithInputs() {
        return gates.stream().filter(gate -> !(gate instanceof Input)).collect(Collectors.toList());
    }

    public List<Connection> getConnections() {
        return connections;
    }

    public List<BitStream> getBitstreams() {
        return Arrays.asList(bitStream);
    }
}
