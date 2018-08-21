package io.github.githubjakob.convolutionalSat;


import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.components.connections.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.components.gates.Input;
import io.github.githubjakob.convolutionalSat.components.gates.Output;
import io.github.githubjakob.convolutionalSat.components.pins.InputPin;
import io.github.githubjakob.convolutionalSat.components.pins.OutputPin;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import io.github.githubjakob.convolutionalSat.modules.Decoder;
import io.github.githubjakob.convolutionalSat.modules.Encoder;
import lombok.Getter;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.inject.Inject;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Created by jakob on 31.05.18.
 */
public class Problem {

    @Getter
    private final Requirements requirements;

    private Encoder encoder;

    private Decoder decoder;

    private BitStream.BitStreamFactory bitStreamFactory;

    @Getter
    private int numberOfBitStreams = 0;

    @Getter
    private int numberOfBits = 0;

    private List<BitStream> bitStreams = new ArrayList<>();

    Logger logger = LogManager.getLogger();

    @Inject
    public Problem(Requirements requirements, Encoder encoder, Decoder decoder, BitStream.BitStreamFactory bitStreamFactory) {
        this.requirements = requirements;
        this.encoder = encoder;
        this.decoder = decoder;
        this.bitStreamFactory = bitStreamFactory;
    }

    /**
     *
     * @return eine Liste an Klauseln, die alle Eigenschaften dieses Problems repräsentiert, insbesondere
     *  - Variablen über Gates
     *  - Variablen über Verbindungen
     *  - Variablen über Bitströme
     */
    public List<Clause> convertProblemToCnf() {
        List<Clause> cnf = new ArrayList<>();

        cnf.addAll(convertGatesToCnf());
        cnf.addAll(convertConnectionsToCnf());
        cnf.addAll(convertBitStreamsToCnf());

        return cnf;
    }

    private BitStream addInputAndOutputToBitStream(BitStream bitStream) {
        BitStream bitStreamToRegister = bitStreamFactory.createBitStream(bitStream, encoder.getInputs().get(0), decoder.getOutputs().get(0));

        logger.info("Adding Bitstream to Problem {}", bitStreamToRegister.toString());

        registerBitStream(bitStreamToRegister);
        return bitStreamToRegister;
    }

    private void registerBitStream(BitStream bitStream) {
        numberOfBitStreams++;
        this.bitStreams.add(bitStream);
        numberOfBits = bitStream.getLengthWithDelay();
        requirements.addBitStream(bitStream);
    }

    private List<Clause> convertGatesToCnf() {
        List<Clause> clausesForAllGates = new ArrayList<>();

        for (Gate gate : requirements.getGates()) {
            clausesForAllGates.addAll(gate.convertToCnf());
        }
        return clausesForAllGates;
    }

    private List<Clause> convertConnectionsToCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        //für jede Verbindung
        for (Connection connection : requirements.getConnections()) {
            clausesForTick.addAll(connection.convertToCnf());
        }

        /**
         * Für alle Verbindungen eines Inputs oder Outputs, die vom selben Output Pin weg gehen, muss mindestens eine gesetzt sein.
         */

        for (OutputPin outputPin : requirements.getOutputPins()) {
            if (!(outputPin.getGate() instanceof Input) && !(outputPin.getGate() instanceof Output)) {
                continue;
            }
            // for alle Connections die von diesem Output Pin weg geht
            Clause possibleConnections = new Clause();
            for (Connection connection : requirements.getConnections()) {
                if (connection.getFrom().equals(outputPin)) {
                    possibleConnections.addVariable(new ConnectionVariable(true, connection));
                }
            }
            clausesForTick.add(possibleConnections);
        }


        for (InputPin inputPin : requirements.getInputPins()) {

            List<Connection> connectionsWithSameTo = requirements.getConnections().stream().filter(connection -> connection.getTo().equals(inputPin)).collect(Collectors.toList());

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

    private List<Clause> convertBitStreamsToCnf() {
        List<Clause> clausesForTick = new ArrayList<>();

        for (BitStream bitStream : bitStreams) {
            clausesForTick.addAll(bitStream.toCnf());
        }

        return clausesForTick;

    }

    /**
     *
     * @param circuit
     * @return einen Bitstrom, für den die Schaltung nicht funktioniert
     *          oder einen zufälligen Bitstrom, falls die Schaltung null ist, oder falls kein fehlschlagender Bitstrom gefunden wird
     */
    public BitStream addFailingForOrRandom(Circuit circuit) {
        if (circuit == null) {
            return addRandomBitStream();
        }

        BitStream failingBitStream = findFailingFor(circuit, 10000);

        if (failingBitStream == null) {
            return null;
        }

        return addInputAndOutputToBitStream(failingBitStream);
    }

    private BitStream findFailingFor(Circuit circuit, int maxAttempts) {
        Instant start = Instant.now();
        logger.info("Searching for failing Bitstream...");
        int counter = 0;
        while(counter < maxAttempts) {
            BitStream potentialFailingBitStream =
                   bitStreamFactory.createBitStreamWithNoIdAndRandomBits(requirements.frameLength, requirements.getDelay());

            if (!circuit.testBitStream(potentialFailingBitStream, false)) {
                Instant end = Instant.now();
                long millis = (end.toEpochMilli() - start.toEpochMilli());
                logger.info("Found failing Bitstream in ms: {}, iterations: {}, {} ", millis, counter,
                        potentialFailingBitStream.toString());
                return potentialFailingBitStream;
            }
            counter++;
        }
        Instant end = Instant.now();
        long millis = (end.toEpochMilli() - start.toEpochMilli());
        logger.info("No failing Bitsream found in {} ms, {} attempts", millis, maxAttempts);
        return null;
    }

    private BitStream addRandomBitStream() {
        BitStream randomBitStream =
                bitStreamFactory.createBitStreamWithNoIdAndRandomBits(requirements.frameLength, requirements.getDelay());
        return addInputAndOutputToBitStream(randomBitStream);
    }
}
