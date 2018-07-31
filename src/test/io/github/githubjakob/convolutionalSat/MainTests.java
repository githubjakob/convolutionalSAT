package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.connection.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.Input;
import io.github.githubjakob.convolutionalSat.components.gates.Output;
import io.github.githubjakob.convolutionalSat.modules.Module;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


import java.util.*;

/**
 * Created by jakob on 07.06.18.
 */
public class MainTests {
/*
    @Before
    public void before() {
        BooleanExpression.resetSolver();
    }

    @Test
    public void oneXor_oneBit_connectionIsCorrect() {
        Module encoder = new Module(Enums.Module.ENCODER);
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();

        BitStream bitStream = new BitStream(0, new int[] { 0 }, 0, input, output);

        Problem problem = new Problem(Arrays.asList(encoder), new Requirements(0, 1, 0, 0));
        problem.registerBitStream(bitStream);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(connections.size(), is(3));
    }


    @Test
    public void oneXor_moreBits_connectionIsCorrect() {
        Module encoder = new Module(Enums.Module.ENCODER);
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();

        BitStream inputBitStream = new BitStream(0, new int[] { 0, 1, 0 }, 0, input, null);
        BitStream outputBitStream = new BitStream(0, new int[] { 0, 0, 0}, 0, null, output);

        Problem problem = new Problem(Arrays.asList(encoder), new Requirements(0, 3, 0, 0));
        problem.registerBitStream(inputBitStream);
        problem.registerBitStream(outputBitStream);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(connections.size(), is(3));
    }


    @Test
    public void numberOfConnections_xorAndRegister() {
        Module encoder = new Module(Enums.Module.ENCODER);
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();
        encoder.addRegister();

        BitStream bitStream = new BitStream(0, new int[] { 0, 1, 0 }, 0, input, output);

        Problem problem = new Problem(Arrays.asList(encoder), new Requirements(0, 3, 0, 0));
        problem.registerBitStream(bitStream);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        assertThat(problem.getConnections().size(), is(8));
    }

    @Test
    public void numberOfConnections_twoAndsNoCircularCircuit() {
        Module encoder = new Module(Enums.Module.ENCODER);
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addAnd();
        encoder.addAnd();

        BitStream bitStream = new BitStream(0, new int[] { 0 }, 0, input, output);

        Problem problem = new Problem(Arrays.asList(encoder), new Requirements(0, 1, 0, 0));

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(connections.size(), is(5));
    }

    @Test
    public void numberOfConnections_twoXorAndRegister() {
        Module encoder = new Module(Enums.Module.ENCODER);
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();
        encoder.addXor();
        encoder.addRegister();

        BitStream bitStream = new BitStream(0, new int[] { 0, 1, 0 }, 0, input, output);

        Problem problem = new Problem(Arrays.asList(encoder), new Requirements(0, 3, 0, 0));
        problem.registerBitStream(bitStream);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(problem.getConnections().size(), is(18));
        assertThat(connections.size(), is(6));
    }

    @Test
    public void numberOfConnections_threeXorAndRegister() {
        Module encoder = new Module(Enums.Module.ENCODER);
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();
        encoder.addXor();
        encoder.addXor();
        encoder.addRegister();

        BitStream bitStream = new BitStream(0, new int[] { 0, 1, 0 }, 0, input, output);

        Problem problem = new Problem(Arrays.asList(encoder), new Requirements(0, 3, 0, 0));

        assertThat(problem.getConnections().size(), is(32));
    }

    @Test
    public void oneXor_oneRegister_moreBits_connectionIsCorrect() {
        Module encoder = new Module(Enums.Module.ENCODER);
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();
        encoder.addRegister();

        BitStream inputBitStream = new BitStream(0, new int[] { 0, 1, 0 }, 0, input, null);
        BitStream outputBitStream = new BitStream(0, new int[] { 0, 1, 1 }, 0, null, output);

        Problem problem = new Problem(Arrays.asList(encoder), new Requirements(0, 3, 0, 0));
        problem.registerBitStream(inputBitStream);
        problem.registerBitStream(outputBitStream);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(problem.getConnections().size(), is(8));
        assertThat(connections.size(), is(4));
    }

    @Test
    public void oneXor_oneRegister_moreBits_noSolution() {
        Module encoder = new Module(Enums.Module.ENCODER);
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();

        BitStream inputBitStream = new BitStream(0, new int[] { 0, 0, 0 }, 0, input, null);
        BitStream outputBitStream = new BitStream(0, new int[] { 1, 1, 1}, 0, null, output);

        Problem problem = new Problem(Arrays.asList(encoder), new Requirements(0, 3, 0, 0));
        problem.registerBitStream(inputBitStream);
        problem.registerBitStream(outputBitStream);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        assertTrue(model==null);
    }

    @Test
    public void oneXor_moreBitstreams_notSolveable() {
        Module encoder = new Module(Enums.Module.ENCODER);
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();

        BitStream inputBitStream = new BitStream(0, new int[]{0, 1, 0}, 0, input, null);
        BitStream outputBitStream = new BitStream(0, new int[]{0, 0, 0}, 0, null, output);

        BitStream inputBitStream1 = new BitStream(1, new int[]{0, 0, 0}, 0, input, null);
        BitStream outputBitStream1 = new BitStream(1, new int[]{1, 1, 1}, 0, null, output);

        Problem problem = new Problem(Arrays.asList(encoder), new Requirements(0, 3, 0, 0));
        problem.registerBitStream(inputBitStream);
        problem.registerBitStream(outputBitStream);
        problem.registerBitStream(inputBitStream1);
        problem.registerBitStream(outputBitStream1);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        assertTrue(model==null);
    }*/
}
