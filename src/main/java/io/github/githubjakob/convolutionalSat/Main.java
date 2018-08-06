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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
        encoder.addAnd();
        encoder.addXor();
        encoder.addXor();
        encoder.addNot();
        encoder.addNot();
        encoder.addRegister();
        encoder.addRegister();

        Decoder decoder = guice.getInstance(Decoder.class);
        decoder.addInput();
        decoder.addInput();
        decoder.addGlobalOutput();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addNot();
        decoder.addNot();
        decoder.addNot();
        decoder.addNot();
        decoder.addXor();
        decoder.addRegister();
        decoder.addRegister();
        decoder.addXor();
        decoder.addXor();

        Channel channel = guice.getInstance(Channel.class);

        Problem problem = guice.getInstance(Problem.class);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit latestCircuit = null;

        //MainGui mainGui = new MainGui();

        Logger logger = LogManager.getLogger();


        int counter = 0;
        while (counter < requirements.getMaxNumberOfIterations()) {
            logger.info("Iteration: {} ", counter);

            problem.addFailingForOrRandom(latestCircuit);

            latestCircuit = booleanExpression.solve();

            if (latestCircuit == null) {
                continue;
            }

            //Graph solution = new Graph(latestCircuit);
            //mainGui.addPanel(solution);

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
