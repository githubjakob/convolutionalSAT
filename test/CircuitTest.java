import org.junit.Assert;
import org.junit.Test;

/**
 * Created by jakob on 07.06.18.
 */
public class CircuitTest {

    @Test
    public void numberOfConnections_xorAndRegister() {
        Circuit circuit = new Circuit();

        circuit.addXor();
        circuit.addRegister();

        Assert.assertEquals(8, circuit.getConnections().size());
    }

    @Test
    public void numberOfConnections_xor() {
        Circuit circuit = new Circuit();

        circuit.addXor();

        Assert.assertEquals(3, circuit.getConnections().size());
    }

    @Test
    public void numberOfConnections_twoXorAndRegister() {
        Circuit circuit = new Circuit();

        circuit.addXor();
        circuit.addXor();
        circuit.addRegister();

        Assert.assertEquals(18, circuit.getConnections().size());
    }

    @Test
    public void numberOfConnections_threeXorAndRegister() {
        Circuit circuit = new Circuit();

        circuit.addXor();
        circuit.addXor();
        circuit.addXor();
        circuit.addRegister();

        Assert.assertEquals(32, circuit.getConnections().size());
    }
}
