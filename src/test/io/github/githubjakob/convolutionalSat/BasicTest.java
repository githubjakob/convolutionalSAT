package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.connection.Connection;
import io.github.githubjakob.convolutionalSat.components.connection.NoiseFreeConnection;
import io.github.githubjakob.convolutionalSat.components.gates.Xor;
import io.github.githubjakob.convolutionalSat.logic.BitAtComponentVariable;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import io.github.githubjakob.convolutionalSat.modules.Encoder;
import io.github.githubjakob.convolutionalSat.modules.Module;
import org.junit.Test;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import static org.junit.Assert.*;
import static org.hamcrest.CoreMatchers.*;

/**
 * Created by jakob on 07.06.18.
 */
public class BasicTest {

    @Test
    public void testEquals() {
        Xor xor = new Xor(new Encoder());
        Xor xor2 = xor;

        assertThat(xor, is(xor2));

        assertThat(xor.inputPin1, is(xor.inputPin1));
        assertThat(xor.inputPin1, not(xor.inputPin2));

        BitAtComponentVariable variable = new BitAtComponentVariable(0, 0, true, xor);
        BitAtComponentVariable variable2 = new BitAtComponentVariable(0, 0, true, xor);
        BitAtComponentVariable variable3 = new BitAtComponentVariable(0, 0, false, xor);
        BitAtComponentVariable variable4 = new BitAtComponentVariable(0, 0, true, xor2);
        BitAtComponentVariable variable5 = new BitAtComponentVariable(0, 1, true, xor2);
        BitAtComponentVariable variable6 = new BitAtComponentVariable(1, 0, true, xor2);
        BitAtComponentVariable variable7 = new BitAtComponentVariable(1, 1, true, xor2);

        assertThat(variable, is(variable2));
        assertThat(variable, is(variable4));
        assertThat(variable2, is(variable4));
        assertNotSame(variable, variable2);
        assertThat(variable5, not(variable4));
        assertThat(variable5, not(variable6));
        assertThat(variable5, not(variable7));

        assertThat(variable, is(variable3));
    }

    @Test
    public void testHashMap() {
        Map<ConnectionVariable, Integer> map = new HashMap<>();

        Xor xor = new Xor(new Encoder());
        ConnectionVariable variable = new ConnectionVariable(false, xor);
        ConnectionVariable sameVariable = new ConnectionVariable(false, xor);

        assertThat(variable, is(sameVariable));

        map.put(variable, 1);

        assertThat(map.containsKey(variable), is(true));
        assertThat(map.containsKey(sameVariable), is(true));
    }

    @Test
    public void testHashMapTimeDependentVariable() {
        Map<Variable, Integer> map = new HashMap<>();

        Xor xor = new Xor(new Encoder());
        BitAtComponentVariable variable = new BitAtComponentVariable(0, 0, false, xor);
        BitAtComponentVariable same = new BitAtComponentVariable(0, 0, false, xor);
        BitAtComponentVariable notSame = new BitAtComponentVariable(0, 1, false, xor);

        assertThat(variable, is(same));
        assertThat(variable, not(notSame));

        map.put(variable, 1);

        assertThat(map.containsKey(variable), is(true));
        assertThat(map.containsKey(same), is(true));
        assertThat(map.containsKey(notSame), is(false));
    }

    @Test
    public void connectionEquals() {
        Xor xor = new Xor(new Encoder());
        Xor xor2 = new Xor(new Encoder());

        Connection connection = new NoiseFreeConnection(xor.getOutputPin(), xor2.getInputPins().get(0));
        Connection connection2 = new NoiseFreeConnection(xor.getOutputPin(), xor2.getInputPins().get(1));

        assertThat(connection, not(connection2));

        HashSet<Connection> set = new HashSet<>();
        set.add(connection);
        set.add(connection2);

        assertThat(set.size(), is(2));
    }

    @Test
    public void circuitEquals() {
        Xor xor = new Xor(new Encoder());
        Xor xor2 = new Xor(new Encoder());

        Connection connection = new NoiseFreeConnection(xor.getOutputPin(), xor2.getInputPins().get(0));
        Connection connection2 = new NoiseFreeConnection(xor.getOutputPin(), xor2.getInputPins().get(1));

        Circuit circuit = new Circuit(Arrays.asList(connection, connection2),
                Arrays.asList(xor, xor2));

        Connection sameConnection = new NoiseFreeConnection(xor.getOutputPin(), xor2.getInputPins().get(0));
        Connection sameConnection2 = new NoiseFreeConnection(xor.getOutputPin(), xor2.getInputPins().get(1));

        Circuit sameCircuit = new Circuit(Arrays.asList(sameConnection, sameConnection2),
                Arrays.asList(xor, xor2));

        assertThat(circuit, is(sameCircuit));
        assertThat(connection, not(sameConnection));

        HashSet<Circuit> set = new HashSet<>();
        set.add(circuit);
        set.add(sameCircuit);

        assertThat(set.size(), is(1));
    }
}
