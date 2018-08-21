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

    static Injector guice = Guice.createInjector(new GuiceModule());

    static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {
        findWith(3, 2, 2, 5, 5, 4, 2, 2,
                5, 1);
    }

    public static void findWith(int enAnd, int enNot, int enReg, int decAnd, int decNot, int frameLength, int delay,
                            int numberOfChannels, int maxNumberOfIterations, int numberOfFlippedBits) {

        Requirements requirements = guice.getInstance(Requirements.class);
        requirements.setFrameLength(frameLength);
        requirements.setDelay(delay);
        requirements.setNumberOfChannels(numberOfChannels);
        requirements.setMaxNumberOfIterations(maxNumberOfIterations);
        requirements.setNumberOfFlippedBits(numberOfFlippedBits);
        requirements.setEnAnd(enAnd);
        requirements.setEnNot(enNot);
        requirements.setEnReg(enReg);
        requirements.setDecAnd(decAnd);
        requirements.setDecNot(decNot);

        logger.info("Finding with:");
        logger.info(requirements.toString());
        findWith(requirements);

    }

    public static void findWith(Requirements requirements) {

        Encoder encoder = guice.getInstance(Encoder.class);
        Decoder decoder = guice.getInstance(Decoder.class);
        Channel channel = guice.getInstance(Channel.class);
        Problem problem = guice.getInstance(Problem.class);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit latestCircuit = null;

        MainGui mainGui = new MainGui();

        int iteration = 0;
        while (iteration < requirements.getMaxNumberOfIterations()) {
            logger.info("Iteration: {} ", iteration);

            BitStream failingOrRandomBitStream = problem.addFailingForOrRandom(latestCircuit);

            if (failingOrRandomBitStream == null) {
                break; // keine failingBitstreams gefunden -> fertig
            }

            latestCircuit = booleanExpression.solve();

            if (latestCircuit == null) {
                continue; // keine Lösung mit letztem failing Bitstream gefunden -> letzte Iteration wiederholen
            }

            //Graph solution = new Graph(latestCircuit);
            //mainGui.addPanel(solution);

            if (!latestCircuit.testValidity(requirements)) {
                logger.warn("circuit is not valid, searching next solution");
                booleanExpression.addLastModelNegated();
                continue;
            }


            iteration++;
        }

        System.out.println("end");

    }
}
