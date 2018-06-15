package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.Gui.MainApp;
import io.github.githubjakob.convolutionalSat.modules.Decoder;
import io.github.githubjakob.convolutionalSat.modules.Encoder;

import java.time.Instant;
import java.util.*;

public class Main {

    /* Nach n gefundenen Modellen das Lösen abbrechen */
    public static final int MAX_NUMBER_OF_SOLUTIONS = 50;

    public static void main(String[] args) {


        Encoder encoder = new Encoder(2);

        encoder.addXor();
        encoder.addXor();
        encoder.addRegister();
        encoder.addRegister();

        Decoder decoder = new Decoder(encoder.getNumberOfOutputs());

        decoder.addXor();
        decoder.addXor();
        decoder.addXor();
        //decoder.addRegister();

        int[] inputBitStream = new int[] { 1, 1, 0, 1 };

        Problem problem = new Problem(encoder, decoder, inputBitStream);


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
