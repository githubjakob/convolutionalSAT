package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.components.connections.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.modules.Module;
import lombok.Getter;
import lombok.Setter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

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
    boolean noiseEnabled;

    @Getter
    int blockLength;

    @Getter
    int maxNumberOfIterations;

    private List<Module> modules = new ArrayList<>();

    @Setter
    @Getter
    private int numberOfChannels;

    Logger logger = LogManager.getLogger();

    public Requirements() {
        this.blockLength = 10;
        this.delay = 2;
        this.flippedBits = 1;
        noiseEnabled = true;
        this.maxNumberOfIterations = 10;
        logger.info("Searching convolutional code with block length: {}, delay {}, flipped Bits {}",
                blockLength, delay, flippedBits);
    }

    public void addBitStream(BitStream bitStream) {
        bitStreams.add(bitStream);
    }

    public void addModule(Module module) {
        this.modules.add(module);
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

    /**
     *
     * @return Die Anzahl aller Gates ohne Register und Inputs
     */
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
