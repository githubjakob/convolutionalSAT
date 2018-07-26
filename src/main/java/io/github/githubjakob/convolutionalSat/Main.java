package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.graph.Graph;
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
        encoder.addGlobalInput();
        encoder.addOutput();
        encoder.addOutput();
        encoder.addXor();
        encoder.addXor();
        encoder.addRegister();
        encoder.addRegister();
        encoder.addRegister();


        Module decoder = new Module(Enums.Module.DECODER);
        decoder.addInput();
        decoder.addInput();
        decoder.addGlobalOutput();
        decoder.addXor();
        decoder.addXor();

        Requirements requirements = new Requirements(3, 20, 0);

        Channel channel = new Channel(encoder, decoder, requirements);

        MainGui mainGui = new MainGui();

        Problem problem = new Problem(Arrays.asList(encoder, decoder, channel), requirements);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Instant start = Instant.now();

        int counter = 0;
        while (counter < MAX_NUMBER_OF_SOLUTIONS) {
            Circuit circuit = booleanExpression.solveNext();

            if (circuit == null) {
                System.out.println("is not satisfiable");
                return;
            }

            if (!circuit.testValidity(requirements)) {
                System.err.println("circuit is not valid, searching next solution");
                continue;
            }


            Graph graph = new Graph(circuit);
            if (!graph.isValid()) {
                System.err.println("graph is not valid, searching next solution");
                continue;
            }

            mainGui.addPanel(graph);
            counter++;
        }

        Instant end = Instant.now();
        long millis = (end.toEpochMilli() - start.toEpochMilli());
        System.out.println("Time " + millis + " ms");
    }

}
