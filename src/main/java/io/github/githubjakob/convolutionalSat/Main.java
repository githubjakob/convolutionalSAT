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

        Problem problem = guice.getInstance(Problem.class);

        BooleanExpression booleanExpression;

        Circuit latestCircuit = null;

        MainGui mainGui = new MainGui();

        int counter = 0;
        while (counter < requirements.getMaxNumberOfIterations()) {

            requirements.setRandomDistortedChannel();

            problem.addFailingForOrRandom(latestCircuit);

            booleanExpression = new BooleanExpression(problem);

            latestCircuit = booleanExpression.solve();

            if (latestCircuit == null) {
                System.out.println("is not satisfiable");
                return;
            }

            Graph solution = new Graph(latestCircuit);
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
