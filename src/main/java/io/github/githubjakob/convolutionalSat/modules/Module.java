package io.github.githubjakob.convolutionalSat.modules;

import afu.org.checkerframework.checker.oigj.qual.O;
import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.connections.Connection;
import io.github.githubjakob.convolutionalSat.components.connections.NoiseFreeConnection;
import io.github.githubjakob.convolutionalSat.components.gates.*;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;


public abstract class Module {

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

    private ComponentFactory gateFactory;

    public Module(ComponentFactory gateFactory) {
        this.gateFactory = gateFactory;
    }

    public Output addOutput() {
        Output output = gateFactory.getOutput();
        output.setModule(this);
        outputs.add(output);
        gates.add(output);
        inputPins.addAll(output.getInputPins());
        return output;
    }

    public Output addGlobalOutput() {
        Output output = gateFactory.getGlobalOutput();
        output.setModule(this);
        outputs.add(output);
        gates.add(output);
        inputPins.addAll(output.getInputPins());
        return output;
    }

     public Input addInput() {
        Input input = gateFactory.getInput();
        input.setModule(this);
        this.inputs.add(input);
        this.gates.add(input);
        this.outputPins.add(input.getOutputPin());
        return input;
    }

    public Input addGlobalInput() {
        Input input = gateFactory.getGlobalInput();
        input.setModule(this);
        this.inputs.add(input);
        this.gates.add(input);
        this.outputPins.add(input.getOutputPin());
        return input;
    }

    public Register addRegister() {
        Register register = gateFactory.getRegister();
        setupNewGate(register);
        return register;
    };

    public And addAnd() {
        And and = gateFactory.getAnd();
        setupNewGate(and);
        return and;
    };

    public Not addNot() {
        Not not = gateFactory.getNot();
        setupNewGate(not);
        return not;
    };

    public Xor addXor() {
        Xor xor = gateFactory.getXor();
        setupNewGate(xor);
        return xor;
    };

    public Identity addIdentity() {
        Identity identity = gateFactory.getIdentity();
        setupNewGate(identity);
        return identity;
    }

    /**
     * Nach dem Hinzufügen eines neuen Gates zum Modul muss diese Methode aufgerufen werden
     *
     */
    private void setupNewGate(Gate newGate) {
        gates.add(newGate);
        newGate.setModule(this);
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
