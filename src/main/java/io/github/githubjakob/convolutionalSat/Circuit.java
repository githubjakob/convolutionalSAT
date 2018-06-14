package io.github.githubjakob.convolutionalSat;


import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import com.google.errorprone.annotations.Var;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.TimeDependentVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;

import java.util.*;

/**
 * Created by jakob on 11.06.18.
 */
public class Circuit {

    /*
    Schaltkreise sind äquivalent, wenn sie
    - die selbe Anzahl an Gates besitzen
    - die selbe Anzahl an Verbindungen besitzen
    - äquivalente Verbindungen beseitzen
        (Verbdinungen sind äquivalent, wenn sie zwischen gleichen Typen von Komponenten besetehen)
     */
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

    /* Das sind nur die gesetzten, also tatsächlichen Verbindungen in einem Schaltkreis  */
    private final Set<Connection> connections;

    private final Set<EquivalentConnection> equivalentConnections = new HashSet<>();

    private final Set<Gate> gates;

    private List<Variable> variables = new ArrayList<>();

    public int[] inputBitStream;

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
            Component component = variable.getComponent();
            boolean weight = variable.getWeight();
            if (variable instanceof TimeDependentVariable) {
                TimeDependentVariable old = (TimeDependentVariable) variable;
                cloned.add(new TimeDependentVariable(old.getTick(), weight, component));
            } else {
                cloned.add(new Variable(weight, component));
            }
        }
        this.variables = cloned;
        this.connections = extractFrom(variables);
        for (Connection connection : connections) {
            equivalentConnections.add(new EquivalentConnection(connection));
        }
        this.gates = new HashSet<>(gates);
    }

    public Map<Component, int[]> getBitsAtNodes() {

        Map<Component, int[]> bitsAtNodes = new HashMap<>();

        for (Variable variable : variables) {
            if (!(variable instanceof TimeDependentVariable)) {
                continue;
            }
            TimeDependentVariable underConsideration = (TimeDependentVariable) variable;
            Component component = underConsideration.getComponent();
            int tick = underConsideration.getTick();
            int value = underConsideration.getWeight() ? 1 : 0;

            if (bitsAtNodes.containsKey(component)) {
                int[] savedBits = bitsAtNodes.get(component);
                savedBits[tick] = value;

            } else {
                int[] bits = new int[inputBitStream.length];
                bits[tick] = value;
                bitsAtNodes.put(component, bits);
            }
        }
        return bitsAtNodes;
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

    public List<Input> getInputs() {
        List<Input> inputs = new ArrayList<>();
        for (Gate gate : gates) {
            if (gate instanceof Input) {
                inputs.add((Input) gate);
            }
        }
        return inputs;
    }

    public List<Output> getOutputs() {
        List<Output> outputs = new ArrayList<>();
        for (Gate gate : gates) {
            if (gate instanceof Output) {
                outputs.add((Output) gate);
            }
        }
        return outputs;
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
