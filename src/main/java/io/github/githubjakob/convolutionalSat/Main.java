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
    public static final int MAX_NUMBER_OF_SOLUTIONS = 2;

    public static void main(String[] args) {

        Module encoder = new Module(Enums.Module.ENCODER);
        Input input = encoder.addInput();
        encoder.addOutput();
        encoder.addOutput();
        encoder.addAnd();
        encoder.addAnd();
        encoder.addAnd();
        encoder.addNot();
        encoder.addNot();

        BitStream bitsStreamIn0 = new BitStream(0, new int[] { 0, 0, 1, 0, 1, 1, 0 });
        BitStream bitsStreamIn1 = new BitStream(1, new int[] { 1, 0, 0, 1, 1, 0, 1 });

        encoder.addBitStream(bitsStreamIn0, input);
        encoder.addBitStream(bitsStreamIn1, input);

        Module decoder = new Module(Enums.Module.DECODER);
        decoder.addInput();
        decoder.addInput();
        Output decoderOutput = decoder.addOutput();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addNot();
        decoder.addXor();

        decoder.addBitStream(bitsStreamIn0, decoderOutput);
        decoder.addBitStream(bitsStreamIn1, decoderOutput);

        Channel channel = new Channel(encoder, decoder);
        channel.addBitStream(bitsStreamIn0);
        channel.addBitStream(bitsStreamIn1);

        MainGui mainGui = new MainGui(Arrays.asList(bitsStreamIn0, bitsStreamIn1));

        Problem problem = new Problem(Arrays.asList(encoder, decoder, channel), bitsStreamIn0.getLength(), 2);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Instant start = Instant.now();

        int counter = 0;
        while (counter < MAX_NUMBER_OF_SOLUTIONS) {
            Circuit circuit = booleanExpression.solveNext();
            Graph graph = new Graph(circuit);

            //if (graph.isGraphFullyConnected()) {
            if (true) {
                mainGui.addPanel(graph);
                counter++;
            }
        }

        Instant end = Instant.now();
        long millis = (end.toEpochMilli() - start.toEpochMilli());
        System.out.println("Time " + millis + " ms");
    }

}
