public class Main {

    public static void main(String[] args) {

        Circuit circuit = new Circuit();

        circuit.addInputBitStream(new int[] { 0, 1, 0, 1 });
        circuit.addOutputBitStream(new int[] { 0, 0, 1, 1 });

        //circuit.addGate(xor);
        circuit.addXor();
        circuit.addRegister();

        BooleanExpression booleanExpression = new BooleanExpression(circuit);

        int[] model = booleanExpression.solve();

        booleanExpression.plotCircuitForModel();

    }

}
