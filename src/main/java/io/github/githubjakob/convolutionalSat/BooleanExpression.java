package io.github.githubjakob.convolutionalSat;

import io.github.githubjakob.convolutionalSat.components.connections.Connection;
import io.github.githubjakob.convolutionalSat.logic.Clause;
import io.github.githubjakob.convolutionalSat.logic.ConnectionVariable;
import io.github.githubjakob.convolutionalSat.logic.Variable;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.time.Instant;
import java.util.*;

/**
 * Created by jakob on 07.06.18.
 */
public class BooleanExpression {

    private ISolver satSolver = createNewSolver();

    private List<Clause> clauses;

    private final Problem problem;

    private HashMap<Variable, Integer> dictionary = new HashMap<>();

    private Integer literalCount = 0;

    private Circuit lastModel = null;

    private Logger logger = LogManager.getLogger();

    public BooleanExpression(Problem problem) {
        this.problem = problem;
    }

    private void setSolverParameters(List<int[]> dimacs) {
        this.satSolver.newVar(dictionary.size());
        logger.info("Number of vars {}", dictionary.size());
        this.satSolver.setExpectedNumberOfClauses(dimacs.size());
        logger.info("Number of clauses {}", dimacs.size());
    }

    private void addDimacsToSolver(List<int[]> dimacs) {
        for (int i=0; i<dimacs.size(); i++) {
            int [] clause = dimacs.get(i);
            try {
                satSolver.addClause(new VecInt(clause));
            } catch (ContradictionException e) {
                logger.warn("Empty clause {}", Arrays.toString(clause));
            }
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

    public Circuit solve() {
        ArrayList<Clause> backedUpClauses = backupCurrentClauses();

        this.clauses = problem.convertProblemToCnf();
        List<int[]> dimacs = convertClausesToDimacs(this.clauses);
        createNewSolver();
        addDimacsToSolver(dimacs);
        setSolverParameters(dimacs);

        IProblem satProblem = satSolver;

        try {
            logger.info("Solving...");
            Instant start = Instant.now();
            if (satProblem.isSatisfiable()) {
                Instant end = Instant.now();
                long millis = (end.toEpochMilli() - start.toEpochMilli());
                logger.info("Found model! Solving took {} ms", millis);
                return getCircuitFromModel(satProblem.model());
            } else {
                logger.warn("Not satisfiable!");
                resetDimacs(backedUpClauses);
                return null;
            }
        } catch (TimeoutException e) {
            e.printStackTrace();
        }

        return null;
    }

    private ISolver createNewSolver() {
        this.satSolver = SolverFactory.newDefault();
        return this.satSolver;
    }

    private void resetDimacs(ArrayList<Clause> currentClauses) {
        this.clauses = currentClauses;
        this.satSolver = SolverFactory.newDefault();
    }

    private Circuit getCircuitFromModel(int[] solution) {
        Circuit circuit = retranslate(solution);
        circuit.setNumberOfBitsPerBitStream(this.problem.getNumberOfBits());
        circuit.setNumberOfBitStreams(this.problem.getNumberOfBitStreams());
        lastModel = circuit;
        return circuit;
    }

    private ArrayList<Clause> backupCurrentClauses() {
        if (lastModel != null && !this.clauses.isEmpty()) {
            return new ArrayList<>(this.clauses);
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
        return new Circuit(translatedModel, problem.getRequirements());

    }

    public void addLastModelNegated() {
        List<Clause> negatedModel = new ArrayList<>();
        Clause clause = new Clause();
        negatedModel.add(clause);

        for (Connection connection : lastModel.getConnections()) {
            ConnectionVariable variable = new ConnectionVariable(false, connection);
            clause.addVariable(variable);
        }

        final List<int[]> dimacs = convertClausesToDimacs(negatedModel);
        addDimacsToSolver(dimacs);
    }
}
