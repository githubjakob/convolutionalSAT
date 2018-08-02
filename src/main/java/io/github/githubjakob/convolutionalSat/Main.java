package io.github.githubjakob.convolutionalSat;

import com.google.inject.Guice;
import com.google.inject.Injector;
import io.github.githubjakob.convolutionalSat.components.bitstream.BitStream;
import io.github.githubjakob.convolutionalSat.graph.Graph;
import io.github.githubjakob.convolutionalSat.gui.MainGui;
import io.github.githubjakob.convolutionalSat.guice.GuiceModule;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Decoder;
import io.github.githubjakob.convolutionalSat.modules.Encoder;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.time.Instant;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Main {

    public static void main(String[] args) {

        Injector guice = Guice.createInjector(new GuiceModule());

        Requirements requirements = guice.getInstance(Requirements.class);

        Encoder encoder = guice.getInstance(Encoder.class);
        encoder.addGlobalInput();
        encoder.addOutput();
        encoder.addOutput();
        encoder.addAnd();
        encoder.addAnd();
        encoder.addXor();
        encoder.addNot();
        encoder.addRegister();
        encoder.addRegister();

        Decoder decoder = guice.getInstance(Decoder.class);
        decoder.addInput();
        decoder.addInput();
        decoder.addGlobalOutput();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addNot();
        decoder.addXor();

        Channel channel = guice.getInstance(Channel.class);

        requirements.setModules(Arrays.asList(encoder, decoder, channel));

        Problem problem = guice.getInstance(Problem.class);

        BooleanExpression booleanExpression;

        Circuit latestCircuit = null;
        Graph solution = null;

        int counter = 0;
        MainGui mainGui = new MainGui();

        while (counter < requirements.getMaxNumberOfIterations()) {

            requirements.setDistortedChannel(ThreadLocalRandom.current().nextInt(0, 100) % 2);


            BitStream underTest = problem.addFailingForOrRandom(latestCircuit);
            System.out.println("Adding Bitstream to SAT Solver " + underTest.toString());



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



            /*if (!solution.isValid()) {
                System.err.println("graph is not valid, searching next solution");
                booleanExpression.addLastModelNegated();
                continue;
            }*/



            counter++;
        }

        System.out.println("end");

    }
}
