import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.ArrayList;
import java.util.List;

public class Main {

    final static int MAXVAR = 1000000;
    final static int NBCLAUSES = 1000000;

    static ISolver solver = SolverFactory.newDefault();

    public static void main(String[] args) throws ContradictionException, TimeoutException {

        Gate register = new Register(1, 2);
        Gate register2 = new Register(2, 3);

        Gate xor = new Xor(2, 3, 4);
        Gate xor2 = new Xor(1, 4, 5);

        Circuit circuit = new Circuit(new int[] {
                1, 0, 1, 1
        });

        circuit.addGate(xor);
        circuit.addGate(xor2);
        circuit.addGate(register);
        circuit.addGate(register2);
        List<int[]> clauses = circuit.toBoolean();

        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);

        Reader reader = new DimacsReader( solver );

        System.out.println("Adding clauses:");
        for (int i=0; i<clauses.size(); i++) {
            int [] clause = clauses.get(i);
            solver.addClause(new VecInt(clause));
            System.out.println(reader.decode(clause));
        }


        IProblem problem = solver;
        if (problem.isSatisfiable()) {
            System.out.println(reader.decode(problem.model()));
            System.out.println("success");
        }
    }
}
