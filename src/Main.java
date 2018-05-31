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
    final static int NBCLAUSES = 3;

    static ISolver solver = SolverFactory.newDefault();

    public static void main(String[] args) throws ContradictionException, TimeoutException {

        Gate register = new Register(1, 2);

        Gate and = new And(1, 2, 3);

        Circuit circuit = new Circuit(new int[] {
                1, 0, 1, 1
        });

        circuit.addGate(and);
        List<int[]> clauses = circuit.toBoolean();

        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);

        Reader reader = new DimacsReader( solver );

        for (int i=0; i<clauses.size(); i++) {
            int [] clause = clauses.get(i);
            solver.addClause(new VecInt(clause)); // adapt Array to IVecInt
        }

        IProblem problem = solver;
        if (problem.isSatisfiable()) {
            System.out.println(reader.decode(problem.model()));
            System.out.println("success");

        }
    }
}
