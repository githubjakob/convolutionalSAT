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
    public static final int MAX_NUMBER_OF_SOLUTIONS = 5;

    public static void main(String[] args) {

        BitStream bitsStreamIn0 = new BitStream(0, new int[] { 0, 0, 0, 0});
        BitStream bitsStreamIn1 = new BitStream(1, new int[] { 0, 0, 0, 1});
        BitStream bitsStreamIn2 = new BitStream(2, new int[] { 0, 0, 1, 0});
        BitStream bitsStreamIn3 = new BitStream(3, new int[] { 0, 0, 1, 1});
        BitStream bitsStreamIn4 = new BitStream(4, new int[] { 0, 1, 0, 0});
        BitStream bitsStreamIn5 = new BitStream(5, new int[] { 0, 1, 0, 1});
        BitStream bitsStreamIn6 = new BitStream(6, new int[] { 0, 1, 1, 0});
        BitStream bitsStreamIn7 = new BitStream(7, new int[] { 0, 1, 1, 1});
        BitStream bitsStreamIn8 = new BitStream(8, new int[] { 1, 0, 0, 0});
        BitStream bitsStreamIn9 = new BitStream(9, new int[] { 1, 0, 0, 1});
        BitStream bitsStreamIn10 = new BitStream(10, new int[] { 1, 0, 1, 0});
        BitStream bitsStreamIn11 = new BitStream(11, new int[] { 1, 0, 1, 1});
        BitStream bitsStreamIn12 = new BitStream(12, new int[] { 1, 1, 0, 0});
        BitStream bitsStreamIn13 = new BitStream(13, new int[] { 1, 1, 0, 1});
        BitStream bitsStreamIn14 = new BitStream(14, new int[] { 1, 1, 1, 0});
        BitStream bitsStreamIn15 = new BitStream(15, new int[] { 1, 1, 1, 1});
        List<BitStream> bitStreams = Arrays.asList(bitsStreamIn0, bitsStreamIn1, bitsStreamIn2, bitsStreamIn3,
                bitsStreamIn4, bitsStreamIn5, bitsStreamIn6, bitsStreamIn7, bitsStreamIn8, bitsStreamIn9,
                bitsStreamIn10, bitsStreamIn11, bitsStreamIn12, bitsStreamIn13, bitsStreamIn14, bitsStreamIn15);

        Module encoder = new Module(Enums.Module.ENCODER);
        encoder.addInput();
        encoder.addOutput();
        encoder.addOutput();
        encoder.addAnd();
        encoder.addAnd();
        encoder.addAnd();
        encoder.addNot();
        encoder.addNot();
        encoder.addRegister();
        encoder.addRegister();

        Module decoder = new Module(Enums.Module.DECODER);
        decoder.addInput();
        decoder.addInput();
        decoder.addOutput();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addNot();
        decoder.addXor();

        Channel channel = new Channel(encoder, decoder);

        MainGui mainGui = new MainGui(bitStreams);

        Problem problem = new Problem(Arrays.asList(encoder, decoder, channel));
        problem.registerBitStreamsAsInputOutputRequirement(bitStreams);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Instant start = Instant.now();

        int counter = 0;
        while (counter < MAX_NUMBER_OF_SOLUTIONS) {
            Circuit circuit = booleanExpression.solveNext();
            Graph graph = new Graph(circuit);

            if (graph.isGraphFullyConnected()) {
                mainGui.addPanel(graph);
                counter++;
            }
        }

        Instant end = Instant.now();
        long millis = (end.toEpochMilli() - start.toEpochMilli());
        System.out.println("Time " + millis + " ms");
    }

}
