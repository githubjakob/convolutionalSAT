package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.Connection;
import io.github.githubjakob.convolutionalSat.components.Xor;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;


import java.util.*;

/**
 * Created by jakob on 07.06.18.
 */
public class MainTests {
/*
    @Test
    public void oneXor_oneBit_connectionIsCorrect() {
        Problem circuit = new Problem();

        circuit.setInptBitStream(new int[] { 0 });
        circuit.addOutputBitStream(new int[] { 0 });

        Xor addedXor = circuit.addXor();

        BooleanExpression booleanExpression = new BooleanExpression(circuit);

        Circuit model = booleanExpression.solve();

        Set<Connection> connections = model.getConnections();

        assertThat(connections.size(), is(3));
    }

    /*@Test
    public void oneXor_moreBits_connectionIsCorrect() {
        Problem circuit = new Problem();

        circuit.setInptBitStream(new int[] { 0, 1, 0 });
        circuit.addOutputBitStream(new int[] { 0, 0, 0});

        Xor addedXor = circuit.addXor();

        BooleanExpression booleanExpression = new BooleanExpression(circuit);

        int[] model = booleanExpression.solve();

        Set<Integer> connections = booleanExpression.getConnectionsFromModel();

        Assert.assertEquals(new HashSet<>(Arrays.asList(1003, 1004, 5002)), connections);
    }

    @Test
    public void oneXor_oneRegister_moreBits_connectionIsCorrect() {
        Problem circuit = new Problem();

        circuit.setInptBitStream(new int[] { 1, 1, 0, 1 });
        circuit.addOutputBitStream(new int[] { 1, 0, 1, 1 });

        Xor addedXor = circuit.addXor();
        Register addedRegister = circuit.addRegister();

        BooleanExpression booleanExpression = new BooleanExpression(circuit);

        int[] model = booleanExpression.solve();

        Set<Integer> connections = booleanExpression.getConnectionsFromModel();

        Assert.assertEquals(new HashSet<>(Arrays.asList(5002, 7003, 1004, 1006)), connections);
    }

    @Test
    public void oneXor_oneRegister_moreBits_noSolution() {
        Problem circuit = new Problem();

        circuit.setInptBitStream(new int[] { 0, 0, 0, 0 });
        circuit.addOutputBitStream(new int[] { 1, 1, 1, 1 });

        Xor addedXor = circuit.addXor();
        Register addedRegister = circuit.addRegister();

        BooleanExpression booleanExpression = new BooleanExpression(circuit);

        int[] model = booleanExpression.solve();

        Set<Integer> connections = booleanExpression.getConnectionsFromModel();

        Assert.assertEquals(Collections.emptySet(), connections);
    }*/
}
