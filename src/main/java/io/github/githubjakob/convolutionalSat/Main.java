package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.BitStream;
import io.github.githubjakob.convolutionalSat.components.Input;
import io.github.githubjakob.convolutionalSat.components.Output;
import io.github.githubjakob.convolutionalSat.gui.Graph;
import io.github.githubjakob.convolutionalSat.gui.MainGui;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Module;

import java.time.Instant;
import java.util.*;

public class Main {

    /* Nach n gefundenen Modellen das Lösen abbrechen */
    public static final int MAX_NUMBER_OF_SOLUTIONS = 1;

    public static void main(String[] args) {

        Module encoder = new Module(Enums.Module.ENCODER);
        encoder.addInput();
        encoder.addOutput();
        encoder.addOutput();
        encoder.addAnd();
        encoder.addAnd();
        encoder.addAnd();
        encoder.addAnd();
        encoder.addNot();
        encoder.addNot();
        encoder.addRegister();
        encoder.addRegister();
        encoder.addRegister();
        encoder.addRegister();
        encoder.addRegister();

        Module decoder = new Module(Enums.Module.DECODER);
        decoder.addInput();
        decoder.addInput();
        decoder.addOutput();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addNot();
        decoder.addNot();
        decoder.addNot();
        decoder.addNot();
        decoder.addXor();

        TestSuite testSuite = new TestSuite();

        Channel channel = new Channel(encoder, decoder, testSuite.getNoise());

        MainGui mainGui = new MainGui();


        Problem problem = new Problem(Arrays.asList(encoder, decoder, channel), testSuite);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Instant start = Instant.now();

        int counter = 0;
        while (counter < MAX_NUMBER_OF_SOLUTIONS) {
            Circuit circuit = booleanExpression.solveNext();
            Graph graph = new Graph(circuit);

            mainGui.addPanel(graph);
            if (graph.isGraphFullyConnected()) {

                counter++;
            }
        }

        Instant end = Instant.now();
        long millis = (end.toEpochMilli() - start.toEpochMilli());
        System.out.println("Time " + millis + " ms");
    }

}
