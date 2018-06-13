package io.github.githubjakob.convolutionalSat;


import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Variable;

import java.util.*;

/**
 * Created by jakob on 11.06.18.
 */
public class Circuit {

    private static class EquivalentConnection {

        Gate from;

        Gate to;

        EquivalentConnection(Connection connection) {

            this.from = connection.getFrom().getGate();
            this.to = connection.getTo().getGate();

        }

        @Override
        public boolean equals(Object obj) {
            if (obj == null) return false;
            if (obj == this) return true;
            if (!(obj instanceof EquivalentConnection))return false;
            EquivalentConnection other = (EquivalentConnection) obj;
            return (from.getType().equals(other.from.getType())
                    && to.getType().equals(other.to.getType()));
         }

        @Override
        public int hashCode() {
            return from.getType().hashCode() + to.getType().hashCode();
        }


    }

    private final Set<Connection> connections;

    private final Set<EquivalentConnection> equivalentConnections = new HashSet<>();

    private final Set<Gate> gates;

    private List<Variable> variables = new ArrayList<>();

    public Circuit(List<Connection> connections, List<Gate> gates, boolean whatever) {
        this.connections = new HashSet<>(connections);
        this.gates = new HashSet<>(gates);
        for (Connection connection : connections) {
            equivalentConnections.add(new EquivalentConnection(connection));
        }
    }

    public Circuit(List<Variable> variables, List<Gate> gates) {
        List<Variable> cloned = new ArrayList<>();
        for (Variable variable : variables) {
            boolean weight = variable.getWeight();
            Component component = variable.getComponent();
            cloned.add(new Variable(weight, component));
        }
        this.variables = cloned;
        this.connections = extractFrom(variables);
        for (Connection connection : connections) {
            equivalentConnections.add(new EquivalentConnection(connection));
        }
        this.gates = new HashSet<>(gates);
    }

    public Set<Connection> getConnections() {
        return this.connections;
    }

    private Set<Connection> extractFrom(List<Variable> variables) {
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

    @Override
    public boolean equals(Object obj) {
        if (obj == null) return false;
        if (obj == this) return true;
        if (!(obj instanceof Circuit))return false;
        Circuit other = (Circuit) obj;
        return (equivalentConnections.equals(other.equivalentConnections) && gates.equals(other.gates));
    }

    @Override
    public int hashCode() {
        return equivalentConnections.hashCode() * gates.hashCode();
    }
}
