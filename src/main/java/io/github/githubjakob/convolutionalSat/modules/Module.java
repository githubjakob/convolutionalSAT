package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.connection.Connection;
import io.github.githubjakob.convolutionalSat.components.connection.NoiseFreeConnection;
import io.github.githubjakob.convolutionalSat.components.gates.*;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;


public class Module {

    @Getter
    List<Input> inputs = new ArrayList<>();

    @Getter
    List<Output> outputs = new ArrayList<>();

    @Getter
    List<Gate> gates = new ArrayList<>();

    @Getter
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
        List<Gate> allGatesExceptOutput =
                gates.stream().filter(gate -> !(gate instanceof Output)).collect(Collectors.toList());
        List<Gate> allGatesExceptInput =
                gates.stream().filter(gate -> !(gate instanceof Input)).collect(Collectors.toList());

        for (Gate gate : allGatesExceptOutput) {
            if (gate.equals(justCreated)) {
                continue;
            }

            for (InputPin inputPin : justCreated.getInputPins()) {
                connections.add(new NoiseFreeConnection(gate.getOutputPin(), inputPin));
            }
        }

        for (Gate gate : allGatesExceptInput) {
            if (gate.equals(justCreated)) {
                continue;
            }

            for (InputPin componentInputPin : gate.getInputPins()) {
                connections.add(new NoiseFreeConnection(justCreated.getOutputPin(), componentInputPin));
            }
        }
    }
}
