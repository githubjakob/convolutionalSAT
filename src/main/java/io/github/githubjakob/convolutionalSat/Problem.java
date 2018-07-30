package io.github.githubjakob.convolutionalSat;


import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.connection.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Getter;

import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jakob on 31.05.18.
 */
public class Problem {

    @Getter
    private final Requirements requirements;

    @Getter
    private int numberOfBitStreams = 0;

    @Getter
    private int numberOfBits = 0;

    private List<Module> modules;

    private List<BitStream> bitStreams = new ArrayList<>();

    public Problem(List<Module> modules, Requirements requirements) {
        this.modules = modules;
        this.requirements = requirements;
        registerBitStreamsAsInputOutputRequirement(requirements.getBitStreams());
    }

    public List<Clause> convertProblemToCnf() {
        List<Clause> cnf = new ArrayList<>();

        cnf.addAll(convertGatesToCnf());
        cnf.addAll(convertConnectionsToCnf());
        cnf.addAll(convertBitStreamsToCnf());

        return cnf;
    }

    public Module getEncoder() {
        for (Module module :modules) {
            if (module.getType().equals(Enums.Module.ENCODER)) return module;
        }
        return null;
    }

    public Module getDecoder() {
        for (Module module :modules) {
            if (module.getType().equals(Enums.Module.DECODER)) return module;
        }
        return null;
    }

    public Module getChannel() {
        for (Module module :modules) {
            if (module.getType().equals(Enums.Module.CHANNEL)) return module;
        }
        return null;
    }

    public List<OutputPin> getOutputPins() {
        List<OutputPin> allPins = new ArrayList<>();

        for (Module module : modules) {
            List<OutputPin> gatesFromModule = module.getOutputPins();
            allPins.addAll(gatesFromModule);
        }

        return allPins;
    }

    public List<InputPin> getInputPins() {
        List<InputPin> allPins = new ArrayList<>();

        for (Module module : modules) {
            List<InputPin> gatesFromModule = module.getInputPins();
            allPins.addAll(gatesFromModule);
        }

        return allPins;
    }

    public List<Gate> getGates() {
        List<Gate> allGates = new ArrayList<>();

        for (Module module : modules) {
            List<Gate> gatesFromModule = module.getGates();
            allGates.addAll(gatesFromModule);
        }

        return allGates;
    }

    public List<Connection> getConnections() {
        List<Connection> allGates = new ArrayList<>();

        for (Module module : modules) {
            List<Connection> connectionsFromModule = module.getConnections();
            allGates.addAll(connectionsFromModule);
        }

        return allGates;
    }

    public void registerBitStreamsAsInputOutputRequirement(List<BitStream> bitStreams) {
        Module encoder = getEncoder();
        Module decoder = getDecoder();

        List<BitStream> toRegister = new ArrayList<>();

        for (BitStream bitStream : bitStreams) {
            BitStream bitStreamToRegister = new BitStream(bitStream.getId(), bitStream.getBits(),  bitStream.getDelay(),
                    encoder.getInputs().get(0), decoder.getOutputs().get(0));
            toRegister.add(bitStreamToRegister);
        }
        registerBitStreams(toRegister);
    }

    public void registerBitStreams(List<BitStream> bitStreams) {
        for (BitStream bitStream : bitStreams) {
            numberOfBitStreams++;
            this.bitStreams.add(bitStream);
            numberOfBits = bitStream.getLength();
            requirements.addBitStream(bitStream);
        }
    }

    List<Clause> convertGatesToCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        // für jedes Bauteil
        for (Gate gate : getGates()) {
            for (BitStream bitStream : bitStreams) {
                clausesForTick.addAll(gate.convertToCnf(bitStream, getMaxMicrotticks()));

            }
        }
        return clausesForTick;
    }

    private List<Clause> convertConnectionsToCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        int MICROTICKS_MAX = getMaxMicrotticks();
        System.out.println("Microticks " + MICROTICKS_MAX);

        //für jede Verbindung
        for (Connection connection : getConnections()) {
            clausesForTick.addAll(connection.convertMicroticksRequirement(MICROTICKS_MAX));
            for (BitStream bitStream : bitStreams) {
                clausesForTick.addAll(connection.convertToCnfAtTick(bitStream, MICROTICKS_MAX));
            }
        }

        /**
         * Für alle Verbindungen, die vom selben Output Pin weg gehen, muss mindestens eine gesetzt sein.
         */
        for (OutputPin outputPin : getOutputPins()) {
            // for alle Connections die von diesem Output Pin weg geht
            Clause possibleConnections = new Clause();
            for (Connection connections : getConnections()) {
                if (connections.getFrom().equals(outputPin)) {
                    possibleConnections.addVariable(new ConnectionVariable(true, connections));
                }
            }
            clausesForTick.add(possibleConnections);
        }


        for (InputPin inputPin : getInputPins()) {

            List<Connection> connectionsWithSameTo = getConnections().stream().filter(connection -> connection.getTo().equals(inputPin)).collect(Collectors.toList());

            /**
             * Für alle Verbindungen, die zum selben Input Pin führen, muss mindestens eine gesetzt sein.
             */
            Clause possibleConnections = new Clause();
            for (Connection connection : connectionsWithSameTo) {
                possibleConnections.addVariable(new ConnectionVariable(true, connection));

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
                    ConnectionVariable connectionFalse = new ConnectionVariable(false, connection);
                    ConnectionVariable otherFalse = new ConnectionVariable(false, other);
                    Clause exclude = new Clause(connectionFalse, otherFalse);
                    clausesForTick.add(exclude);
                }

            }
        }

        return clausesForTick;
    }

    public List<Clause> convertBitStreamsToCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        for (BitStream bitStream : bitStreams) {
            clausesForTick.addAll(bitStream.toCnf());
        }

        return clausesForTick;

    }

    private int getMaxMicrotticks() {
        int count = 0;
        for (Gate gate : getGates()) {
            if (gate.getType().equals("register") || gate.getType().equals("input")) {
                continue;
            }
            count++;
        }
        return count;
    }
}
