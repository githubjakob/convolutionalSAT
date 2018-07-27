package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.Connection;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
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

    private List<Clause> clauses;

    private final Problem problem;

    List<int[]> dimacs;

    private int[] modelDimacs = null;

    static HashMap<Variable, Integer> dictionary = new HashMap<>();

    static Integer literalCount = 0;

    private List<Circuit> models = new ArrayList<>();

    int numbersOfModelsFound = 0;

    public BooleanExpression(Problem problem) {
        this.problem = problem;
    }

    private void convertProblemToDimacs() {
        this.clauses = problem.convertProblemToCnf();
        this.dimacs = convertClausesToDimacs(this.clauses);
        addDimacsToSolver(this.dimacs);
    }

    private void setupSolver() {
        this.solver.newVar(dictionary.size() + 1000);
        System.out.println("Number of vars " + dictionary.size());
        this.solver.setExpectedNumberOfClauses(dimacs.size() + 1000);
        System.out.println("Number of clauses " + dimacs.size() );
    }

    private void addDimacsToSolver(List<int[]> dimacs) {
        for (int i=0; i<dimacs.size(); i++) {
            int [] clause = dimacs.get(i);
            try {
                solver.addClause(new VecInt(clause));
            } catch (ContradictionException e) {
                System.err.println("Empty clause " + Arrays.toString(clause));
            }

            //System.out.println(reader.decode(clause));
        }
    }

    private List<int[]> convertClausesToDimacs(List<Clause> allClauses) {

        List<int[]> dimacsClauses = new ArrayList<>();

        for (Clause clause : allClauses) {

            int numberOfVariablesInClause = clause.getVariables().size();

            int[] literalsOfClause = new int[numberOfVariablesInClause];

            int index = 0;

            for (Variable variable : clause.getVariables()) {

                Integer literal = null;

                boolean weight = variable.getWeight();

                if (dictionary.containsKey(variable)) {
                    literal = dictionary.get(variable);
                } else {
                    Integer nextLiteral = ++literalCount;
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

        return dimacsClauses;
    }

    /**
     * Find another solution that is different from the last one
     * The connections of the model already found are negated and then added to the clauses
     *
     * @return
     */
    public Circuit solveNext() {
        if (this.models.isEmpty()) {
            return solve();
        }

        addLastModelNegated();

        return solve();

    }

    public Circuit solve() {
        convertProblemToDimacs();
        setupSolver();

        IProblem problem = solver;
        try {
            System.out.println("Solving...");
            if (problem.isSatisfiable()) {
                modelDimacs = problem.model();
                //System.out.println(reader.decode(modelDimacs));
                System.out.println("found model " + numbersOfModelsFound);
                numbersOfModelsFound++;
                Circuit model = retranslate(modelDimacs);
                model.setNumberOfBitsPerBitStream(this.problem.getNumberOfBits());
                model.setNumberOfBitStreams(this.problem.getNumberOfBitStreams());
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

    private Circuit retranslate(int[] model) {
        List<Variable> translatedModel = new ArrayList<>();
        for (int i = 0; i < model.length; i++) {
            int literal = model[i];

            if (!(dictionary.containsValue(literal) || dictionary.containsValue(literal*-1))) {
                throw new RuntimeException("something is wrong");
            }

            for (Map.Entry<Variable, Integer> entry : dictionary.entrySet()) {
                if (!(literal == entry.getValue() || literal == entry.getValue() * -1)) {
                    continue;
                }
                Variable variable = entry.getKey();
                if (literal < 0) {
                    variable.setWeight(false);
                } else {
                    variable.setWeight(true);
                }
                translatedModel.add(variable);

            }
        }
        return new Circuit(translatedModel, problem.getGates(), problem.getRequirements().getBitStreams());

    }

    public void addLastModelNegated() {
        Circuit latestModel = models.get(models.size()-1);

        List<Clause> negatedModel = new ArrayList<>();
        Clause clause = new Clause();
        negatedModel.add(clause);

        for (Connection connection : latestModel.getConnections()) {
            ConnectionVariable variable = new ConnectionVariable(false, connection);
            clause.addVariable(variable);
        }

        final List<int[]> dimacs = convertClausesToDimacs(negatedModel);
        addDimacsToSolver(dimacs);
    }
}
