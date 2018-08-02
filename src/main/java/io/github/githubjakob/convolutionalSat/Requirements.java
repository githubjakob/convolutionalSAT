package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.components.connections.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Decoder;
import io.github.githubjakob.convolutionalSat.modules.Encoder;
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
    private final Noise noise;

    @Getter
    List<BitStream> bitStreams = new ArrayList<>();

    /**
     * wait for delay ticks and then check that input(encoder) equals output(encoder)
     */
    @Getter
    private int delay;

    int noiseRatioPercent;

    private int distortedChannel;

    boolean enableNoise;

    int blockLength;

    @Setter
    private List<Module> modules;

    public Requirements() {
        this.blockLength = 10;
        this.delay = 3;
        this.noiseRatioPercent = 0;
        this.distortedChannel = 0;
        enableNoise = true;
        this.noise = new Noise(blockLength, delay, noiseRatioPercent, distortedChannel);
        System.out.println("Test Suite with: delay " + delay + ", bitStreamLenght: " + blockLength + ", noise enabled: " + (noiseRatioPercent > 0));
    }

    public Requirements(int delay, int blockLength, int noiseRatioPercent, int distortedChannel) {
        this.blockLength = blockLength;
        this.delay = delay;
        this.noiseRatioPercent = noiseRatioPercent;
        this.distortedChannel = distortedChannel;
        enableNoise = false;
        this.noise = new Noise(blockLength, delay, noiseRatioPercent, distortedChannel);
        System.out.println("Test Suite with: delay " + delay + ", bitStreamLenght: " + blockLength + ", noise enabled: " + (noiseRatioPercent > 0));
    }

    public void setDistortedChannel(int distortedChannel) {
        this.distortedChannel = distortedChannel;
        System.out.println("Setting distorted channel: " + distortedChannel + " with value: " + noiseRatioPercent + " %");
    }


    public void addBitStream(BitStream bitStream) {
        bitStreams.add(bitStream);
    }

    ///////////////////////////////////////////////////////

    public Module getEncoder() {
        for (Module module : modules) {
            if (module instanceof Encoder) return (Encoder) module;
        }
        return null;
    }

    public Module getDecoder() {
        for (Module module :modules) {
            if (module instanceof Decoder) return (Decoder) module;
        }
        return null;
    }

    public Channel getChannel() {
        for (Module module :modules) {
            if (module instanceof Channel) return (Channel) module;
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
