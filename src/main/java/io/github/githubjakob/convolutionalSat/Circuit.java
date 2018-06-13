package io.github.githubjakob.convolutionalSat;


import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Variable;

import java.util.*;

/**
 * Created by jakob on 11.06.18.
 */
public class Circuit {

    private final Set<Connection> connections;

    private final List<Gate> gates;

    private List<Variable> variables = new ArrayList<>();

    public Circuit(List<Variable> variables, List<Gate> gates) {
        List<Variable> cloned = new ArrayList<>();
        for (Variable variable : variables) {
            boolean weight = variable.getWeight();
            Component component = variable.getComponent();
            cloned.add(new Variable(weight, component));
        }
        this.variables = cloned;
        this.connections = getConnections();
        this.gates = gates;

    }

    public Set<Connection> getConnections() {
        if (variables == null) {
            return Collections.emptySet();
        }

        Set<Connection> connections = new HashSet<>();

        for (Variable variable : variables) {
            Component component = variable.getComponent();

            if (variable.getWeight() && component instanceof Connection){
                connections.add((Connection) component);
            }
        }
        return connections;
    }

    public List<Register> getRegisters() {
        List<Register> registers = new ArrayList<>();
        for (Gate gate : gates) {
            if (gate instanceof Register) {
                registers.add((Register) gate);
            }
        }
        return registers;
    }

    public List<Xor> getXors() {
        List<Xor> xors = new ArrayList<>();
        for (Gate gate : gates) {
            if (gate instanceof Xor) {
                xors.add((Xor) gate);
            }
        }
        return xors;
    }

    public Input getInput() {
        for (Gate gate : gates) {
            if (gate instanceof Input) {
                return (Input) gate;
            }
        }
        return null;
    }

    public Output getOutput() {
        for (Gate gate : gates) {
            if (gate instanceof Output) {
                return (Output) gate;
            }
        }
        return null;
    }
}
