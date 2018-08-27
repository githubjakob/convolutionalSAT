package io.github.githubjakob.convolutionalSat.modules;

import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.Requirements;
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

    ComponentFactory componentFactory;

    Requirements requirements;

    public Module(ComponentFactory componentFactory, Requirements requirements) {
        this.componentFactory = componentFactory;
        this.requirements = requirements;
        requirements.addModule(this);
    }

    Output addOutput() {
        Output output = componentFactory.getOutput();
        output.setModule(this);
        outputs.add(output);
        gates.add(output);
        inputPins.addAll(output.getInputPins());
        return output;
    }

    public Output addGlobalOutput() {
        Output output = componentFactory.getGlobalOutput();
        output.setModule(this);
        outputs.add(output);
        gates.add(output);
        inputPins.addAll(output.getInputPins());
        return output;
    }

     public Input addInput() {
        Input input = componentFactory.getInput();
        input.setModule(this);
        this.inputs.add(input);
        this.gates.add(input);
        this.outputPins.add(input.getOutputPin());
        return input;
    }

    public Input addGlobalInput() {
        Input input = componentFactory.getGlobalInput();
        input.setModule(this);
        this.inputs.add(input);
        this.gates.add(input);
        this.outputPins.add(input.getOutputPin());
        return input;
    }

    public Register addRegister() {
        Register register = componentFactory.getRegister();
        setupNewGate(register);
        return register;
    };

    public And addAnd() {
        And and = componentFactory.getAnd();
        setupNewGate(and);
        return and;
    };

    public Not addNot() {
        Not not = componentFactory.getNot();
        setupNewGate(not);
        return not;
    };

    Xor addXor() {
        Xor xor = componentFactory.getXor();
        setupNewGate(xor);
        return xor;
    };

    Identity addIdentity() {
        Identity identity = componentFactory.getIdentity();
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
                new ArrayList<>();
        for (Gate gate1 : gates) {
            if (!(gate1 instanceof Output)) {
                allGatesExceptOutput.add(gate1);
            }
        }
        List<Gate> allGatesExceptInput =
                new ArrayList<>();
        for (Gate gate1 : gates) {
            if (!(gate1 instanceof Input)) {
                allGatesExceptInput.add(gate1);
            }
        }

        for (Gate gate : allGatesExceptOutput) {
            if (gate.equals(justCreated)) {
                continue;
            }

            for (InputPin inputPin : justCreated.getInputPins()) {
                connections.add(componentFactory.createNoiseFreeConnection(gate.getOutputPin(), inputPin));
            }
        }

        for (Gate gate : allGatesExceptInput) {
            if (gate.equals(justCreated)) {
                continue;
            }

            for (InputPin componentInputPin : gate.getInputPins()) {
                connections.add(componentFactory.createNoiseFreeConnection(justCreated.getOutputPin(), componentInputPin));
            }
        }
    }
}
