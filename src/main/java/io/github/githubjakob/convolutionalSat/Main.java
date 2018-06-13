package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.Gui.MainApp;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        Problem problem = new Problem();

        problem.addInputBitStream(new int[] { 1, 1, 0, 1 });
        problem.addOutputBitStream(new int[] { 0, 1, 0, 0 });

        problem.addXor();
        problem.addRegister();
        problem.addRegister();

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        List<Circuit> circuits = booleanExpression.solveAll();

        Set<Circuit> uniqueCircuits = new HashSet<>(circuits);

        System.out.println("Found circuits " + circuits.size());
        System.out.println("Unique circuits " + uniqueCircuits.size());

        //booleanExpression.plotCircuitForModel();
        System.out.println("done");

        MainApp mainApp = new MainApp(new ArrayList<>(uniqueCircuits));
    }

}
