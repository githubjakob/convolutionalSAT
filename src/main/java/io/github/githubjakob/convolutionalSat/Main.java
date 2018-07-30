package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.BitStream;
import io.github.githubjakob.convolutionalSat.graph.Graph;
import io.github.githubjakob.convolutionalSat.gui.MainGui;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    /* Nach n gefundenen Modellen das Lösen abbrechen */
    public static final int MAX_NUMBER_OF_ITERATIONS = 10;

    public static void main(String[] args) {

        Requirements requirements = new Requirements(3, 5, 10, 0);

        Module encoder = new Module(Enums.Module.ENCODER);
        encoder.addGlobalInput();
        encoder.addOutput();
        encoder.addOutput();
        encoder.addOutput();
        encoder.addXor();
        encoder.addXor();
        encoder.addRegister();
        encoder.addRegister();
        encoder.addRegister();

        Module decoder = new Module(Enums.Module.DECODER);
        decoder.addInput();
        decoder.addInput();
        decoder.addInput();
        decoder.addGlobalOutput();
        decoder.addXor();
        decoder.addXor();

        Channel channel = new Channel(encoder, decoder, requirements);

        Problem problem = new Problem(Arrays.asList(encoder, decoder, channel), requirements);

        BooleanExpression booleanExpression;

        Circuit latestCircuit = null;
        Graph solution = null;

        int counter = 0;
        int RANDOM_BITSTREAM = 0;
        int FAILING_BITSTREAM = 1;
        int state = RANDOM_BITSTREAM;
        MainGui mainGui = new MainGui();
        while (counter < MAX_NUMBER_OF_ITERATIONS) {

            BitStream bitStreamUnderTest;
            if (state == RANDOM_BITSTREAM) {
                bitStreamUnderTest = requirements.createRandomBitStream();
                System.out.println("Using random Bitstream " + bitStreamUnderTest.toString());
                problem.registerBitStreamsAsInputOutputRequirement(Arrays.asList(bitStreamUnderTest));
                requirements.setDistortedChannel(ThreadLocalRandom.current().nextInt(0, 100) % 3);
                state = FAILING_BITSTREAM;
            } else if (state == FAILING_BITSTREAM) {
                bitStreamUnderTest = requirements.findFailingBitStream(latestCircuit, requirements);
                if (bitStreamUnderTest == null) {
                    state = RANDOM_BITSTREAM;
                    continue;
                }
                problem.registerBitStreamsAsInputOutputRequirement(Arrays.asList(bitStreamUnderTest));
                requirements.setDistortedChannel(ThreadLocalRandom.current().nextInt(0, 100) % 3);
            }



            booleanExpression = new BooleanExpression(problem);
            Instant start = Instant.now();
            latestCircuit = booleanExpression.solve();
            Instant end = Instant.now();
            long millis = (end.toEpochMilli() - start.toEpochMilli());
            System.out.println("Solving took " + millis + " ms");

            if (latestCircuit == null) {
                System.out.println("is not satisfiable");
                return;
            }

            solution = new Graph(latestCircuit);
            mainGui.addPanel(solution);


            if (!latestCircuit.testValidity(requirements)) {
                System.err.println("circuit is not valid, searching next solution");
                booleanExpression.addLastModelNegated();
                continue;
            }



            if (!solution.isValid()) {
                System.err.println("graph is not valid, searching next solution");
                booleanExpression.addLastModelNegated();
                continue;
            }



            counter++;
        }

        System.out.println("end");

    }
}
