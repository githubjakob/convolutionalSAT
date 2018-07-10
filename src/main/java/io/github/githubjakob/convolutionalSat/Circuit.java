package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import io.github.githubjakob.convolutionalSat.logic.MicrotickVariable;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

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

    private List<ConnectionVariable> variables = new ArrayList<>();

    @Setter
    @Getter
    private int numberOfBitsPerBitStream;

    @Setter
    @Getter
    private int numberOfBitStreams;

    public Circuit(List<Connection> connections, List<Gate> gates, boolean whatever) {
        this.connections = new HashSet<>(connections);
        this.gates = new HashSet<>(gates);
        for (Connection connection : connections) {
            equivalentConnections.add(new EquivalentConnection(connection));
        }
    }

    public Circuit(List<ConnectionVariable> variables, List<Gate> gates) {
        List<ConnectionVariable> cloned = new ArrayList<>();
        for (ConnectionVariable variable : variables) {
            Component component = variable.getComponent();
            boolean weight = variable.getWeight();
            if (variable instanceof BitAtComponentVariable) {
                BitAtComponentVariable old = (BitAtComponentVariable) variable;
                cloned.add(new BitAtComponentVariable(old.getTick(), old.getBitStreamId(), weight, component));
            } else if (variable instanceof MicrotickVariable) {
                MicrotickVariable old = (MicrotickVariable) variable;
                cloned.add(new MicrotickVariable(old.getMicrotick(), weight, component));
            } else {
                cloned.add(new ConnectionVariable(weight, component));
            }
        }
        this.variables = cloned;
        this.connections = extractFrom(variables);
        for (Connection connection : connections) {
            equivalentConnections.add(new EquivalentConnection(connection));
        }
        HashMap<Component, Integer> microtickAsDecimal = new HashMap<>();
        for (ConnectionVariable variable : variables) {
            if (variable instanceof MicrotickVariable) {
                MicrotickVariable microtickVariable = (MicrotickVariable) variable;

                if (microtickAsDecimal.containsKey(variable.getComponent())) {
                    if (microtickVariable.getWeight()) {
                        microtickAsDecimal.put(microtickVariable.getComponent(), microtickAsDecimal.get(variable.getComponent())+1);
                    }
                } else {
                    microtickAsDecimal.put(microtickVariable.getComponent(), 0);
                }

            }
        }
        for (Map.Entry<Component, Integer> entry : microtickAsDecimal.entrySet()) {
            System.out.println("Microtick: " + entry.getKey().toString() + " " + entry.getValue());
        }
        this.gates = new HashSet<>(gates);
    }

    public Map<Component, int[][]> getBitsAtNodes() {

        Map<Component, int[][]> bitsAtNodes = new HashMap<>();

        for (ConnectionVariable variable : variables) {
            if (!(variable instanceof BitAtComponentVariable)) {
                continue;
            }
            BitAtComponentVariable underConsideration = (BitAtComponentVariable) variable;
            Component component = underConsideration.getComponent();
            int tick = underConsideration.getTick();
            int value = underConsideration.getWeight() ? 1 : 0;
            int bitStream = underConsideration.getBitStreamId();

            if (bitsAtNodes.containsKey(component)) {
                int[][] bitStreams = bitsAtNodes.get(component);
                int[] bits = bitStreams[bitStream];
                bits[tick] = value;

            } else {
                int[][] bitStreams = new int[numberOfBitStreams][numberOfBitsPerBitStream];
                int[] bits = new int[numberOfBitsPerBitStream];
                bits[tick] = value;
                bitStreams[underConsideration.getBitStreamId()] = bits;
                bitsAtNodes.put(component, bitStreams);
            }
        }
        return bitsAtNodes;
    }

    public Set<Connection> getConnections() {
        return this.connections;
    }

    private Set<Connection> extractFrom(List<ConnectionVariable> variables) {
        if (variables == null) {
            return Collections.emptySet();
        }

        Set<Connection> connections = new HashSet<>();

        for (ConnectionVariable variable : variables) {
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

    public List<Identity> getIdentities() {
        List<Identity> identities = new ArrayList<>();
        for (Gate gate : gates) {
            if (gate instanceof Identity) {
                identities.add((Identity) gate);
            }
        }
        return identities;
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

    public Set<Gate> getGates() {
        return gates;
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
