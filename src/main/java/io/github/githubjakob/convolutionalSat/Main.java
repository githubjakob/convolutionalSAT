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

        Channel channel = new Channel(encoder, decoder);

        Requirements requirements = new Requirements(Arrays.asList(encoder, decoder, channel),
                3, 20, 0, 0);

        Problem problem = new Problem(requirements);

        BooleanExpression booleanExpression;

        Circuit latestCircuit = null;
        Graph solution = null;

        int counter = 0;
        MainGui mainGui = new MainGui();

        while (counter < MAX_NUMBER_OF_ITERATIONS) {


            BitStream underTest = problem.addFailingForOrRandom(latestCircuit);
            System.out.println("Using random Bitstream id " + underTest.getId() +" : " + underTest.toString());
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
