package io.github.githubjakob.convolutionalSat;


import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Getter;

import java.util.*;

/**
 * Created by jakob on 31.05.18.
 */
public class Problem {

    @Getter
    private final TestSuite testSuite;

    @Getter
    private int numberOfBitStreams = 0;

    @Getter
    private int numberOfBits = 0;

    private List<Module> modules;

    public Problem(List<Module> modules, TestSuite testSuite) {
        this.modules = modules;
        this.testSuite = testSuite;
        registerBitStreamsAsInputOutputRequirement(testSuite.getBitStreams(), testSuite.getDelay());
    }

    public List<Clause> convertProblemToCnf() {
        List<Clause> cnf = new ArrayList<>();

        for (Module module : modules) {
            List<Clause> clauses = module.toCnf();
            cnf.addAll(clauses);
        }

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

    public void registerBitStreamsAsInputOutputRequirement(List<BitStream> bitStreams,  int delay) {
        Module encoder = getEncoder();
        Module decoder = getDecoder();

        for (BitStream bitStream : bitStreams) {
            numberOfBitStreams++;
            BitStream bitSreamAtEncoder = new BitStream(bitStream.getId(), bitStream.getBits(),  delay,
                    encoder.getInputs().get(0));
            BitStream bitStreamAtDecoder = new BitStream(bitStream.getId(), bitStream.getBits(),  delay,
                    decoder.getOutputs().get(0));
            encoder.addBitStream(bitSreamAtEncoder);
            decoder.addBitStream(bitStreamAtDecoder);
            numberOfBits = bitSreamAtEncoder.getLength();
        }
    }
}
