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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class Main {

    static Injector guice = Guice.createInjector(new GuiceModule());

    static Logger logger = LogManager.getLogger();

    public static void main(String[] args) {

        // klein
        findConvolutionalCode(0, 0, 2, 2,
                10, 20, 1, 0,
                4, 2, 2, 5, 1);

        // mittel-frameLength
        /*findConvolutionalCode(0, 0, 2, 3,
                0, 0, 1, 2,
                10, 2, 2, 5, 1);/*

        // mittel-memory
        /*findConvolutionalCode(0, 0, 5, 6,
                0, 0, 2, 4,
                4, 5, 2, 5, 1);/*

        // groß
        /*findConvolutionalCode(0, 0, 2, 3,
                0, 0, 1, 2,
                20, 2, 2, 5, 1);*/
    }

    public static void findConvolutionalCode(int enAnd, int enNot, int enReg, int enXor,
                                             int decAnd, int decNot, int decReg, int decXor,
                                             int frameLength, int delay, int numberOfChannels, int maxNumberOfIterations, int numberOfFlippedBits) {

        Requirements requirements = guice.getInstance(Requirements.class);
        requirements.setFrameLength(frameLength);
        requirements.setDelay(delay);
        requirements.setNumberOfChannels(numberOfChannels);
        requirements.setMaxNumberOfIterations(maxNumberOfIterations);
        requirements.setNumberOfFlippedBits(numberOfFlippedBits);
        requirements.setEnAnd(enAnd);
        requirements.setEnNot(enNot);
        requirements.setEnReg(enReg);
        requirements.setEnXor(enXor);
        requirements.setDecAnd(decAnd);
        requirements.setDecNot(decNot);
        requirements.setDecReg(decReg);
        requirements.setDecXor(decXor);

        logger.info("Finding onvolutional code with:");
        logger.info(requirements.toString());
        findConvolutionalCode(requirements);
    }

    public static void findConvolutionalCode(Requirements requirements) {

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
                break; // keine Lösung mit letztem failing Bitstream gefunden -> fertig
            }

            Graph solution = new Graph(latestCircuit);
            mainGui.addPanel(solution);

            iteration++;
        }

        System.out.println("end");
    }
}
