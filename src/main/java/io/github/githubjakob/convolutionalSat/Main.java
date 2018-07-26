package io.github.githubjakob.convolutionalSat;

import com.sun.org.apache.regexp.internal.RE;
import io.github.githubjakob.convolutionalSat.components.BitStream;
import io.github.githubjakob.convolutionalSat.graph.Graph;
import io.github.githubjakob.convolutionalSat.gui.MainGui;
import io.github.githubjakob.convolutionalSat.modules.Channel;
import io.github.githubjakob.convolutionalSat.modules.Module;
import org.bouncycastle.ocsp.Req;

import java.time.Instant;
import java.util.*;

public class Main {

    /* Nach n gefundenen Modellen das Lösen abbrechen */
    public static final int MAX_NUMBER_OF_ITERATIONS = 3;

    public static void main(String[] args) {

        Module encoder = new Module(Enums.Module.ENCODER);
        encoder.addGlobalInput();
        encoder.addOutput();
        encoder.addOutput();
        encoder.addXor();
        encoder.addRegister();
        encoder.addRegister();
        encoder.addRegister();


        Module decoder = new Module(Enums.Module.DECODER);
        decoder.addInput();
        decoder.addInput();
        decoder.addGlobalOutput();
        decoder.addXor();

        Requirements requirements = new Requirements(3, 128, 80);

        Channel channel = new Channel(encoder, decoder, requirements);

        Problem problem = new Problem(Arrays.asList(encoder, decoder, channel), requirements);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Instant start = Instant.now();

        Graph solution = null;

        int counter = 0;
        while (counter < MAX_NUMBER_OF_ITERATIONS) {
            Circuit circuit = booleanExpression.solve();

            if (circuit == null) {
                System.out.println("is not satisfiable");
                return;
            }

            if (!circuit.testValidity(requirements)) {
                System.err.println("circuit is not valid, searching next solution");
                booleanExpression.addLastModelNegated();
                continue;
            }

            solution = new Graph(circuit);

            if (!solution.isValid()) {
                System.err.println("graph is not valid, searching next solution");
                booleanExpression.addLastModelNegated();
                continue;
            }

            BitStream failed = findFailingBitStream(circuit, requirements);
            if (failed != null) {
                requirements.addBitStream(failed);
                problem.registerBitStreamsAsInputOutputRequirement(Arrays.asList(failed));
            }




            counter++;
        }

        MainGui mainGui = new MainGui();
        mainGui.addPanel(solution);

        Instant end = Instant.now();
        long millis = (end.toEpochMilli() - start.toEpochMilli());
        System.out.println("Time " + millis + " ms");
    }

    private static BitStream findFailingBitStream(Circuit circuit, Requirements requirements) {
        int counter = 0;
        int MAX_RETRIES = 100;
        while(counter < MAX_RETRIES) {
            BitStream bitStream = requirements.createRandomBitStream();
            if (!circuit.testBitStream(bitStream, requirements.getDelay())) {
                return bitStream;
            }
            counter++;
        }
        return null;
    }

}
