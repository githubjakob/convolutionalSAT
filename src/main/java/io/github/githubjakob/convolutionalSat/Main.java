package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.Gui.MainApp;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    /* Nach n gefundenen Modellen das Lösen abbrechen */
    public static final int MAX_NUMBER_OF_SOLUTIONS = 500;

    public static void main(String[] args) {

        Problem problem = new Problem();

        problem.addInputBitStream(new int[] { 1, 1, 0, 1, 1, 0, 1, 1, 0, 1, 0 });
        problem.addOutputBitStream(new int[] { 0, 1, 0, 0, 0, 1, 1, 1, 0, 1, 1});

        problem.addXor();
        problem.addXor();
        problem.addXor();
        problem.addRegister();
        problem.addRegister();

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Instant start = Instant.now();

        List<Circuit> circuits = booleanExpression.solveAll();

        Instant end = Instant.now();

        Set<Circuit> uniqueCircuits = new HashSet<>(circuits);

        System.out.println("Found circuits " + circuits.size());

        long millis = (end.toEpochMilli() - start.toEpochMilli());
        System.out.println("Time " + millis + " ms");
        System.out.println("Unique circuits " + uniqueCircuits.size());

        //booleanExpression.plotCircuitForModel();
        System.out.println("done");

        MainApp mainApp = new MainApp(new ArrayList<>(uniqueCircuits));
    }

}
