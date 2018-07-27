package io.github.githubjakob.convolutionalSat;

import com.sun.org.apache.regexp.internal.RE;
import io.github.githubjakob.convolutionalSat.components.BitStream;
import io.github.githubjakob.convolutionalSat.graph.Graph;
import io.github.githubjakob.convolutionalSat.gui.MainGui;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Module;
import org.bouncycastle.ocsp.Req;

import java.time.Instant;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;

import static afu.org.checkerframework.checker.units.UnitsTools.min;

public class Main {

    /* Nach n gefundenen Modellen das Lösen abbrechen */
    public static final int MAX_NUMBER_OF_ITERATIONS = 20;

    public static void main(String[] args) {

        Requirements requirements = new Requirements(2, 4, 10, 0);

        Module encoder = new Module(Enums.Module.ENCODER);
        encoder.addGlobalInput();
        encoder.addOutput();
        encoder.addOutput();
        encoder.addAnd();
        encoder.addNot();
        encoder.addRegister();
        encoder.addRegister();

        Module decoder = new Module(Enums.Module.DECODER);
        decoder.addInput();
        decoder.addInput();
        decoder.addGlobalOutput();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addNot();
        decoder.addNot();

        Channel channel = new Channel(encoder, decoder, requirements);

        Problem problem = new Problem(Arrays.asList(encoder, decoder, channel), requirements);

        BooleanExpression booleanExpression;

        Circuit latestCircuit = null;
        Graph solution = null;

        int counter = 0;
        MainGui mainGui = new MainGui();
        while (counter < MAX_NUMBER_OF_ITERATIONS) {


            BitStream bitStreamUnderTest = requirements.createRandomBitStream();
            System.out.println("Using random Bitstream " + bitStreamUnderTest.toString());
            requirements.addBitStream(bitStreamUnderTest);
            problem.registerBitStreamsAsInputOutputRequirement(Arrays.asList(bitStreamUnderTest));
            requirements.setDistortedChannel(ThreadLocalRandom.current().nextInt(0, 100) % 3);

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

            if (!latestCircuit.testValidity(requirements)) {
                System.err.println("circuit is not valid, searching next solution");
                booleanExpression.addLastModelNegated();
                continue;
            }

            solution = new Graph(latestCircuit);


            if (!solution.isValid()) {
                System.err.println("graph is not valid, searching next solution");
                booleanExpression.addLastModelNegated();
                continue;
            }
            mainGui.addPanel(solution);



            counter++;
        }

        System.out.println("end");

    }

    private static BitStream findFailingBitStream(Circuit circuit, Requirements requirements) {
        int counter = 0;
        int MAX_RETRIES = 200;
        while(counter < MAX_RETRIES) {
            BitStream bitStream = requirements.createRandomBitStream();
            if (!circuit.testBitStream(bitStream, requirements.getDelay())) {
                return bitStream;
            }
            counter++;
        }
        return null;
    }

}
