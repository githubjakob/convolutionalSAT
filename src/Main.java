import Gui.MainApp;
import logic.Model;

import java.util.List;

public class Main {

    public static void main(String[] args) {

        Circuit circuit = new Circuit();

        circuit.addInputBitStream(new int[] { 1, 1, 0, 1 });
        circuit.addOutputBitStream(new int[] { 0, 1, 0, 0 });

        circuit.addXor();
        circuit.addRegister();
        circuit.addRegister();

        BooleanExpression booleanExpression = new BooleanExpression(circuit);

        List<Model> models = booleanExpression.solveAll();

        //booleanExpression.plotCircuitForModel();
        System.out.println("done");

        MainApp mainApp = new MainApp(models);
    }

}
