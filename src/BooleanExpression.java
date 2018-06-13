import com.google.errorprone.annotations.Var;
import components.Component;
import components.Connection;
import logic.Clause;
import logic.Clauses;
import logic.Model;
import logic.Variable;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.*;

/**
 * Created by jakob on 07.06.18.
 */
public class BooleanExpression {

    private final static int MAXVAR = 1000000;

    private final static int NBCLAUSES = 1000000;

    private static ISolver solver = SolverFactory.newDefault();

    static Reader reader = new DimacsReader( solver );

    private final List<Clauses> clauses;

    private final Circuit circuit;

    List<int[]> dimacs;

    private int[] modelDimacs = null;

    HashMap<Variable, Integer> dictionary = new HashMap<>();

    private List<Model> models = new ArrayList<>();

    BooleanExpression(Circuit circuit) {
        this.circuit = circuit;
        this.solver.newVar(MAXVAR);
        this.solver.setExpectedNumberOfClauses(NBCLAUSES);

        this.clauses = circuit.convertCircuitToCnf();
        this.dimacs = convertClausesToDimacs(this.clauses);
        addDimacsToSolver(this.dimacs);
    }

    private void addDimacsToSolver(List<int[]> dimacs) {
        for (int i=0; i<dimacs.size(); i++) {
            int [] clause = dimacs.get(i);
            try {
                solver.addClause(new VecInt(clause));
            } catch (ContradictionException e) {
                e.printStackTrace();
            }

            System.out.println(reader.decode(clause));
        }
    }

    private List<int[]> convertClausesToDimacs(List<Clauses> allClauses) {

        List<int[]> dimacsClauses = new ArrayList<>();

        for (Clauses clauses : allClauses) {

            for (Clause clause : clauses.getClauses()) {

                int numberOfVariablesInClause = clause.getVariables().size();

                int[] literalsOfClause = new int[numberOfVariablesInClause];

                int index = 0;

                for (Variable variable : clause.getVariables()) {

                    Integer literal = null;

                    boolean weight = variable.getWeight();

                    if (dictionary.containsKey(variable)) {
                        literal = dictionary.get(variable);
                    } else {
                        Integer nextLiteral = dictionary.size() + 1;
                        dictionary.put(variable, nextLiteral);
                        literal = nextLiteral;
                    }

                    if (!weight) {
                        literal = literal * -1;
                    }

                    variable.setLiteral(literal);
                    literalsOfClause[index] = literal;
                    index++;
                }
                dimacsClauses.add(literalsOfClause);
            }
        }

        return dimacsClauses;
    }

    public Model solveNext() {
        if (this.models.isEmpty()) {
            return null;
        }

        Model latestModel = models.get(models.size()-1);

        Clauses negatedModel = new Clauses(0);
        Clause clause = new Clause();
        negatedModel.addClause(clause);

        for (Connection connection : latestModel.getConnections()) {
            Variable variable = new Variable(false, connection);
            clause.addVariable(variable);
        }

        final List<int[]> dimacs = convertClausesToDimacs(Arrays.asList(negatedModel));
        addDimacsToSolver(dimacs);

        return solve();

    }

    public List<Model> solveAll() {
        solve();
        while(true) {
            Model anotherModel = solveNext();
            if (anotherModel == null) {
                break;
            }
        }
        return this.models;
    }

    public Model solve() {
        IProblem problem = solver;
        try {
            if (problem.isSatisfiable()) {
                modelDimacs = problem.model();
                System.out.println(reader.decode(modelDimacs));
                System.out.println("is Satisfiable");
                Model model = retranslate(modelDimacs);
                models.add(model);
                return model;

            } else {
                System.out.println("is not satisfiable");
                return null;
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return null;
    }

    private Model retranslate(int[] model) {
        List<Variable> translatedModel = new ArrayList<>();
        for (int i = 0; i < model.length; i++) {
            int literal = model[i];

            if (!(dictionary.containsValue(literal) || dictionary.containsValue(literal*-1))) {
                throw new RuntimeException("something is wrong");
            }

            for (Map.Entry<Variable, Integer> entry : dictionary.entrySet()) {
                if (literal == entry.getValue() || literal == entry.getValue() * -1) {
                    Variable variable = entry.getKey();
                    if (literal < 1) {
                        variable.setWeight(false);
                    } else {
                        variable.setWeight(true);
                    }
                    translatedModel.add(variable);
                }
            }
        }
        return new Model(translatedModel, circuit.getGates());

    }
}
