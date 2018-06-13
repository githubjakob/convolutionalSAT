package io.github.githubjakob.convolutionalSat;

import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jakob on 07.06.18.
 */
public class CircuitTest {

    @Test
    public void numberOfConnections_xorAndRegister() {
        Problem circuit = new Problem();

        circuit.addXor();
        circuit.addRegister();

        Assert.assertEquals(8, circuit.getConnections().size());
    }

    @Test
    public void numberOfConnections_xor() {
        Problem circuit = new Problem();

        circuit.addXor();

        Assert.assertEquals(3, circuit.getConnections().size());
    }

    @Test
    public void numberOfConnections_twoXorAndRegister() {
        Problem circuit = new Problem();

        circuit.addXor();
        circuit.addXor();
        circuit.addRegister();

        Assert.assertEquals(18, circuit.getConnections().size());
    }

    @Test
    public void numberOfConnections_threeXorAndRegister() {
        Problem circuit = new Problem();

        circuit.addXor();
        circuit.addXor();
        circuit.addXor();
        circuit.addRegister();

        Assert.assertEquals(32, circuit.getConnections().size());
    }
}
