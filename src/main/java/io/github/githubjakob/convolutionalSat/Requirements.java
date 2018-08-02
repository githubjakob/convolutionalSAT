package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.components.connections.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Getter;
import lombok.Setter;

import java.util.*;

/**
 * Wrapper/Utility Klasse für alle Anforderungen an den zu findenden Faltungskodierers. Bietet Zugriff auf
 *
 * - alle zur Verfügung stehenden Gatter
 * - globalen Input und globalen Output des Faltungskodierers
 * - die mögliche Verbindungen, die zur Verfügung stehen (in den Modulen Channel, Encoder, Decoder)
 * - Noise in den Kanälen
 *
 */
public class Requirements {

    @Getter
    List<BitStream> bitStreams = new ArrayList<>();

    /**
     * wait for delay ticks and then check that input(encoder) equals output(encoder)
     */
    @Getter
    private int delay;

    @Getter
    int flippedBits;

    @Getter
    private int distortedChannel;

    @Getter
    boolean noiseEnabled;

    @Getter
    int blockLength;

    @Getter
    int maxNumberOfIterations;

    @Setter
    private List<Module> modules;

    public Requirements() {
        this.blockLength = 4;
        this.delay = 2;
        this.flippedBits = 2;
        this.distortedChannel = 0;
        noiseEnabled = true;
        this.maxNumberOfIterations = 2;
        System.out.println("Test Suite with: delay " + delay + ", bitStreamLenght: " + blockLength + ", noise enabled: " + (flippedBits > 0));
    }

    public Requirements(int delay, int blockLength, int flippedBits, int distortedChannel) {
        this.blockLength = blockLength;
        this.delay = delay;
        this.flippedBits = flippedBits;
        this.distortedChannel = distortedChannel;
        noiseEnabled = false;
        System.out.println("Test Suite with: delay " + delay + ", bitStreamLenght: " + blockLength + ", noise enabled: " + (flippedBits > 0));
    }

    public void setDistortedChannel(int distortedChannel) {
        this.distortedChannel = distortedChannel;
        System.out.println("Setting distorted channel: " + distortedChannel + " with flippedBits: " + flippedBits);
    }


    public void addBitStream(BitStream bitStream) {
        bitStreams.add(bitStream);
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

    public int getMaxMicrotticks() {
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
