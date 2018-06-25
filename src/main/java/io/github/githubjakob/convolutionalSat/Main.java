package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.BitStream;
import io.github.githubjakob.convolutionalSat.components.Input;
import io.github.githubjakob.convolutionalSat.components.Output;
import io.github.githubjakob.convolutionalSat.gui.MainApp;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Decoder;
import io.github.githubjakob.convolutionalSat.modules.Encoder;

import java.time.Instant;
import java.util.*;

public class Main {

    /* Nach n gefundenen Modellen das Lösen abbrechen */
    public static final int MAX_NUMBER_OF_SOLUTIONS = 50;

    public static int[] inputBits1 = new int[] { 0, 0, 1, 0, 1, 1, 0 };
    public static int[] inputBits2 = new int[] { 1, 0, 0, 1, 1, 0, 1 };

    public static void main(String[] args) {

        Encoder encoder = new Encoder();
        Input input = encoder.addInput();
        encoder.addOutput();
        encoder.addOutput();
        encoder.addAnd();
        encoder.addAnd();
        encoder.addAnd();
        encoder.addNot();
        encoder.addNot();

        BitStream bitsStreamIn = new BitStream(0, inputBits1, input);
        BitStream bitsStreamIn2 = new BitStream(1, inputBits2, input);

        encoder.addBitStream(bitsStreamIn);
        encoder.addBitStream(bitsStreamIn2);

        Decoder decoder = new Decoder();
        decoder.addInput();
        decoder.addInput();
        Output decoderOutput = decoder.addOutput();
        decoder.addAnd();
        decoder.addAnd();
        decoder.addNot();
        decoder.addXor();

        BitStream outputBitsStream = new BitStream(0, inputBits1, decoderOutput);
        BitStream outputBitsStream2 = new BitStream(1, inputBits2, decoderOutput);

        decoder.addBitStream(outputBitsStream);
        decoder.addBitStream(outputBitsStream2);

        BitStream channelBitStream = new BitStream(0, inputBits1);
        BitStream channelBitStream2 = new BitStream(1, inputBits2);

        Channel channel = new Channel(encoder, decoder);
        channel.addBitStream(channelBitStream);
        channel.addBitStream(channelBitStream2);

        Problem problem = new Problem(Arrays.asList(encoder, decoder, channel), inputBits1.length, 2);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Instant start = Instant.now();

        List<Circuit> circuits = booleanExpression.solveAll();
        //Circuit circuits = booleanExpression.solve();

        Instant end = Instant.now();

        Set<Circuit> uniqueCircuits = new HashSet<>(circuits);

        int numberOfSolutions = circuits.size();

        if (numberOfSolutions == 0) {
            System.out.println("No circuits for this input");
            return;
        }

        System.out.println("Found circuits " + circuits.size());

        long millis = (end.toEpochMilli() - start.toEpochMilli());
        System.out.println("Time " + millis + " ms");
        System.out.println("Unique circuits " + uniqueCircuits.size());

        //booleanExpression.plotCircuitForModel();
        System.out.println("done");

        MainApp mainApp = new MainApp(new ArrayList<>(uniqueCircuits));
    }

}
