import org.junit.Assert;
import org.junit.Test;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by jakob on 07.06.18.
 */
public class MainTests {

    @Test
    public void oneXor_oneBit_connectionIsCorrect() {
        Circuit circuit = new Circuit();

        circuit.addInputBitStream(new int[] { 0 });
        circuit.addOutputBitStream(new int[] { 0 });

        Xor addedXor = circuit.addXor();

        BooleanExpression booleanExpression = new BooleanExpression(circuit);

        int[] model = booleanExpression.solve();

        Set<Integer> connections = booleanExpression.getConnectionsFromModel();

        Assert.assertEquals(new HashSet<>(Arrays.asList(1003, 1004, 5002)), connections);
    }

    @Test
    public void oneXor_moreBits_connectionIsCorrect() {
        Circuit circuit = new Circuit();

        circuit.addInputBitStream(new int[] { 0, 1, 0 });
        circuit.addOutputBitStream(new int[] { 0, 0, 0});

        Xor addedXor = circuit.addXor();

        BooleanExpression booleanExpression = new BooleanExpression(circuit);

        int[] model = booleanExpression.solve();

        Set<Integer> connections = booleanExpression.getConnectionsFromModel();

        Assert.assertEquals(new HashSet<>(Arrays.asList(1003, 1004, 5002)), connections);
    }

    @Test
    public void oneXor_oneRegister_moreBits_connectionIsCorrect() {
        Circuit circuit = new Circuit();

        circuit.addInputBitStream(new int[] { 1, 1, 0, 1 });
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
        Circuit circuit = new Circuit();

        circuit.addInputBitStream(new int[] { 0, 0, 0, 0 });
        circuit.addOutputBitStream(new int[] { 1, 1, 1, 1 });

        Xor addedXor = circuit.addXor();
        Register addedRegister = circuit.addRegister();

        BooleanExpression booleanExpression = new BooleanExpression(circuit);

        int[] model = booleanExpression.solve();

        Set<Integer> connections = booleanExpression.getConnectionsFromModel();

        Assert.assertEquals(Collections.emptySet(), connections);
    }
}
