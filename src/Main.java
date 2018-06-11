import components.Connection;
import logic.Clauses;
import logic.Model;
import logic.Variable;

import java.util.List;
import java.util.Set;

public class Main {

    public static void main(String[] args) {

        Circuit circuit = new Circuit();

        circuit.addInputBitStream(new int[] { 1, 1 });
        circuit.addOutputBitStream(new int[] { 0, 1 });

        circuit.addXor();
        circuit.addRegister();

        List<Clauses> clauses = circuit.convertCircuitToCnf();

        BooleanExpression booleanExpression = new BooleanExpression(clauses);

        List<Model> models = booleanExpression.solveAll();

        //booleanExpression.plotCircuitForModel();
        System.out.println("done");
    }

}
