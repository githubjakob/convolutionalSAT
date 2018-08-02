package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.connections.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.*;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import io.github.githubjakob.convolutionalSat.logic.MicrotickVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;
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

    @Getter
    private List<BitStream> bitStreams;

    private final Set<EquivalentConnection> equivalentConnections = new HashSet<>();

    private final Set<Gate> gates;

    private List<Variable> variables = new ArrayList<>();

    @Setter
    @Getter
    private int numberOfBitsPerBitStream;

    @Setter
    @Getter
    private int numberOfBitStreams;

    GlobalInput globalInput;

    GlobalOutput globalOutput;

    @Getter
    HashMap<Component, Integer> microtickAsDecimal;

    public Circuit(List<Connection> connections, List<Gate> gates) {
        this.connections = new HashSet<>(connections);
        this.gates = new HashSet<>(gates);
        for (Connection connection : connections) {
            equivalentConnections.add(new EquivalentConnection(connection));
        }
    }

    public Circuit(List<Variable> variables, Requirements requirements) {
        this.variables = cloneVariables(variables);
        this.connections = extractFrom(variables);
        this.bitStreams = requirements.getBitStreams();
        this.gates = new HashSet<>(requirements.getGates());
        for (Gate gate : gates) {
            if (gate instanceof GlobalOutput) {
                this.globalOutput = (GlobalOutput) gate;
            }
            if (gate instanceof GlobalInput) {
                this.globalInput = (GlobalInput) gate;
            }
        }
        for (Connection connection : connections) {
            equivalentConnections.add(new EquivalentConnection(connection));
            setConnectionToGate(connection);
        }
        this.microtickAsDecimal = extractMicroticks(variables);

    }

    private void setConnectionToGate(Connection connection) {
        InputPin connectionTo = connection.getTo();
        OutputPin connectionFrom = connection.getFrom();
        for (Gate gate : gates) {
            for (InputPin inputPin : gate.getInputPins()) {
                if (inputPin.equals(connectionTo)) {
                    inputPin.setConnection(connection);
                }
            }
            if (gate.getOutputPin().equals(connectionFrom)) {
                gate.getOutputPin().getConnections().add(connection);
            }

        }
    }

    private HashMap<Component,Integer> extractMicroticks(List<Variable> variables) {
        HashMap<Component, Integer> microtickAsDecimal = new HashMap<>();
        for (Variable variable : variables) {
            if (variable instanceof MicrotickVariable) {
                MicrotickVariable microtickVariable = (MicrotickVariable) variable;

                if (microtickAsDecimal.containsKey(variable.getComponent())) {
                    if (microtickVariable.getWeight()) {
                        microtickAsDecimal.put(microtickVariable.getComponent(), microtickAsDecimal.get(variable.getComponent())+1);
                    }
                } else {
                    microtickAsDecimal.put(microtickVariable.getComponent(), microtickVariable.getWeight() ? 1 : 0);
                }

            }
        }
        for (Map.Entry<Component, Integer> entry : microtickAsDecimal.entrySet()) {
            //System.out.println("Microtick: " + entry.getKey().toString() + " " + entry.getValue());
        }
        return microtickAsDecimal;
    }

    private List<Variable> cloneVariables(List<Variable> variables) {
        List<Variable> cloned = new ArrayList<>();
        for (Variable variable : variables) {
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
        return cloned;
    }

    public Map<Component, int[][]> getBitsAtNodes() {

        Map<Component, int[][]> bitsAtNodes = new HashMap<>();

        for (Variable variable : variables) {
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

    private Set<Connection> extractFrom(List<Variable> variables) {
        if (variables == null) {
            return Collections.emptySet();
        }

        Set<Connection> connections = new HashSet<>();

        for (Variable variable : variables) {
            Component component = variable.getComponent();

            if (variable.getWeight() && variable instanceof ConnectionVariable && component instanceof Connection){
                Connection currentConnection = (Connection) component;
                currentConnection.getFrom().getConnections().add(currentConnection);
                currentConnection.getTo().setConnection(currentConnection);
                connections.add(currentConnection);
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

    public boolean testBitStream(BitStream bitStream) {
        System.out.println("Testing on Circuit " + bitStream.toString());
        int[] valuesAtOutput = new int[bitStream.getLengthWithDelay()];
        int[] valuesAtInput = new int[bitStream.getLengthWithDelay()];
        for (int tick = 0; tick < bitStream.getLengthWithDelay(); tick++) {
            //System.out.println("testing bitstream " + bits.getId());
            boolean valueAtInput = bitStream.getBitValueAtOrFalse(tick);
            setGlobalInputTo(valueAtInput);
            boolean valueAtOutput = evaluateGlobalOutput(bitStream, tick);
            valuesAtOutput[tick] = valueAtOutput ? 1 : 0;
            valuesAtInput[tick] = valueAtInput ? 1 : 0;
        }

        boolean success = compareShiftedDelay(valuesAtInput, valuesAtOutput, bitStream.getDelay());
        System.out.println("Results " + Arrays.toString(valuesAtInput) + " -> " + Arrays.toString(valuesAtOutput) + " : " + success );
        resetRegisters();
        return success;
    }

    private boolean compareShiftedDelay(int[] valuesAtInput, int[] valuesAtOutput, int delay) {
        for (int i = 0; i < valuesAtInput.length - delay; i++) {
            if (valuesAtInput[i] != valuesAtOutput[i+delay]) {
                return false;
            }
        }
        return true;
    }

    public boolean evaluateGlobalOutput(BitStream bitStream, int tick) {
        //System.out.println("Evaluating globalOutput (" + globalOutput.toString() + ")");
        return globalOutput.evaluate(bitStream, tick);
    }

    private void resetRegisters() {
        for (Register register : getRegisters()) {
            register.reset();
        }
    }

    private void setGlobalInputTo(boolean valueAtInput) {
        globalInput.setBitValue(valueAtInput);
    }

    public boolean testValidity(Requirements requirements) {
        System.out.println("Testing " + requirements.bitStreams.size() + " BitStreams on Circuit");
        for (BitStream bitStream : requirements.bitStreams) {
            //System.out.println("Testing " + bitStream.toString());
            if (!testBitStream(bitStream)) {
                return false;
            }
        }
        System.out.println("All " + requirements.bitStreams.size() + " tests successfull!");
        return true;
    }
}
