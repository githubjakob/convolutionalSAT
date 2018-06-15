package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.Connection;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by jakob on 07.06.18.
 */
public class BooleanExpression {

    private final static int MAXVAR = 1000000;

    private final static int NBCLAUSES = 1000000;

    private static ISolver solver = SolverFactory.newDefault();

    private final List<Clause> clauses;

    private final Problem problem;

    List<int[]> dimacs;

    private int[] modelDimacs = null;

    HashMap<Variable, Integer> dictionary = new HashMap<>();

    private List<Circuit> models = new ArrayList<>();

    int numbersOfModelsFound = 0;

    public BooleanExpression(Problem problem) {
        this.problem = problem;
        this.solver.newVar(MAXVAR);
        this.solver.setExpectedNumberOfClauses(NBCLAUSES);

        this.clauses = problem.convertProblemToCnf();
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

        return dimacsClauses;
    }

    public Circuit solveNext() {
        if (this.models.isEmpty()) {
            return null;
        }

        Circuit latestModel = models.get(models.size()-1);

        List<Clause> negatedModel = new ArrayList<>();
        Clause clause = new Clause();
        negatedModel.add(clause);

        for (Connection connection : latestModel.getConnections()) {
            Variable variable = new Variable(false, connection);
            clause.addVariable(variable);
        }

        final List<int[]> dimacs = convertClausesToDimacs(negatedModel);
        addDimacsToSolver(dimacs);

        return solve();

    }

    public List<Circuit> solveAll() {
        solve();
        while(true) {
            Circuit anotherModel = solveNext();
            if (anotherModel == null) {
                break;
            }

            if (numbersOfModelsFound > Main.MAX_NUMBER_OF_SOLUTIONS) {
                break;
            }
        }
        return this.models;
    }

    public Circuit solve() {
        IProblem problem = solver;
        try {
            if (problem.isSatisfiable()) {
                modelDimacs = problem.model();
                //System.out.println(reader.decode(modelDimacs));
                System.out.println("found model " + numbersOfModelsFound);
                numbersOfModelsFound++;
                Circuit model = retranslate(modelDimacs);
                model.setNumberOfBits(this.problem.getNumberOfBits());
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
        return new Circuit(translatedModel, problem.getGates());

    }
}
