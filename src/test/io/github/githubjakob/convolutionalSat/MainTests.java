package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.modules.Encoder;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


import java.util.*;

/**
 * Created by jakob on 07.06.18.
 */
public class MainTests {

    @Test
    public void oneXor_oneBit_connectionIsCorrect() {
        Encoder encoder = new Encoder();
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();

        BitStream inputBitStream = new BitStream(0, new int[] { 0 }, input);
        BitStream outputBitStream = new BitStream(0, new int[] { 0 }, output);

        encoder.addBitStream(inputBitStream);
        encoder.addBitStream(outputBitStream);

        Problem problem = new Problem(Arrays.asList(encoder), 1, 1);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(connections.size(), is(3));
    }

    @Test
    public void oneXor_moreBits_connectionIsCorrect() {
        Encoder encoder = new Encoder();
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();

        BitStream inputBitStream = new BitStream(0, new int[] { 0, 1, 0 }, input);
        BitStream outputBitStream = new BitStream(0, new int[] { 0, 0, 0}, output);

        encoder.addBitStream(inputBitStream);
        encoder.addBitStream(outputBitStream);

        Problem problem = new Problem(Arrays.asList(encoder), 1, 1);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(connections.size(), is(3));
    }

    @Test
    public void numberOfConnections_xorAndRegister() {
        Encoder encoder = new Encoder();
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();
        encoder.addRegister();

        BitStream inputBitStream = new BitStream(0, new int[] { 0, 1, 0 }, input);
        BitStream outputBitStream = new BitStream(0, new int[] { 0, 0, 0}, output);

        encoder.addBitStream(inputBitStream);
        encoder.addBitStream(outputBitStream);

        Problem problem = new Problem(Arrays.asList(encoder), 1, 1);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(problem.getConnections().size(), is(8));
    }

    @Test
    public void numberOfConnections_twoXorAndRegister() {
        Encoder encoder = new Encoder();
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();
        encoder.addXor();
        encoder.addRegister();

        BitStream inputBitStream = new BitStream(0, new int[] { 0, 1, 0 }, input);
        BitStream outputBitStream = new BitStream(0, new int[] { 0, 0, 0}, output);

        encoder.addBitStream(inputBitStream);
        encoder.addBitStream(outputBitStream);

        Problem problem = new Problem(Arrays.asList(encoder), 1, 1);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(problem.getConnections().size(), is(18));
    }

    @Test
    public void numberOfConnections_threeXorAndRegister() {
        Encoder encoder = new Encoder();
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();
        encoder.addXor();
        encoder.addXor();
        encoder.addRegister();

        BitStream inputBitStream = new BitStream(0, new int[] { 0, 1, 0 }, input);
        BitStream outputBitStream = new BitStream(0, new int[] { 0, 0, 0}, output);

        encoder.addBitStream(inputBitStream);
        encoder.addBitStream(outputBitStream);

        Problem problem = new Problem(Arrays.asList(encoder), 1, 1);

        assertThat(problem.getConnections().size(), is(32));
    }

    @Test
    public void oneXor_oneRegister_moreBits_connectionIsCorrect() {
        Encoder encoder = new Encoder();
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();
        encoder.addRegister();

        BitStream inputBitStream = new BitStream(0, new int[] { 0, 1, 0 }, input);
        BitStream outputBitStream = new BitStream(0, new int[] { 0, 0, 0}, output);

        encoder.addBitStream(inputBitStream);
        encoder.addBitStream(outputBitStream);

        Problem problem = new Problem(Arrays.asList(encoder), 1, 1);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(connections.size(), is(4));
    }

    @Test
    public void oneXor_oneRegister_moreBits_noSolution() {
        Encoder encoder = new Encoder();
        Input input = encoder.addInput();
        Output output = encoder.addOutput();
        encoder.addXor();

        BitStream inputBitStream = new BitStream(0, new int[] { 0, 0, 0 }, input);
        BitStream outputBitStream = new BitStream(0, new int[] { 1, 1, 1}, output);

        encoder.addBitStream(inputBitStream);
        encoder.addBitStream(outputBitStream);

        Problem problem = new Problem(Arrays.asList(encoder), 1, 1);

        BooleanExpression booleanExpression = new BooleanExpression(problem);

        Circuit model = booleanExpression.solve();

        assertTrue(model==null);
    }
}
