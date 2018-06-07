import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.sat4j.core.VecInt;
import org.sat4j.minisat.SolverFactory;
import org.sat4j.reader.DimacsReader;
import org.sat4j.reader.Reader;
import org.sat4j.specs.ContradictionException;
import org.sat4j.specs.IProblem;
import org.sat4j.specs.ISolver;
import org.sat4j.specs.TimeoutException;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by jakob on 07.06.18.
 */
public class BooleanExpression {

    private final static int MAXVAR = 1000000;
    private final static int NBCLAUSES = 1000000;

    private static ISolver solver = SolverFactory.newDefault();

    static Reader reader = new DimacsReader( solver );

    private Circuit circuit;

    private int[] model = null;


    BooleanExpression(Circuit circuit) {
        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);

        this.circuit = circuit;

        List<int[]> clauses = circuit.toBoolean();

        // add clauses to solver
        for (int i=0; i<clauses.size(); i++) {
            int [] clause = clauses.get(i);
            try {
                solver.addClause(new VecInt(clause));
            } catch (ContradictionException e) {
                e.printStackTrace();
            }
            // todo nur die klauseln hinzufügen, die es noch nicht gibt, mit set checken, reihenfolge innerhalb der klauseln
            // todo spielt keine rolle
            System.out.println(reader.decode(clause));
        }
    }

    public int[] solve() {
        IProblem problem = solver;
        try {
            if (problem.isSatisfiable()) {
                model = problem.model();
                System.out.println(reader.decode(model));
                System.out.println("is Satisfiable");
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

    public Set<Integer> getConnectionsFromModel() {
        if (model == null) {
            return Collections.emptySet();
        }

        Set<Integer> connections = new HashSet<>();

        for (int i = 0; i < model.length; i++) {
            if (model[i] > 1000) {
                connections.add(model[i]);
            }
        }
        return connections;
    }

    public void plotCircuitForModel() {
        // DRAW
        MultiGraph graph = new MultiGraph("Network");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph.addAttribute("ui.stylesheet", "edge {text-alignment: along;}");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        Node input = graph.addNode("N1"); //INPUT
        input.addAttribute("ui.label", "1/INPUT");

        Node output = graph.addNode("N2"); //OUTPUT
        output.addAttribute("ui.label", "2/OUTPUT");

        for (Gate gate : circuit.gates) {
            if (gate instanceof Xor) {
                String nodeLabel = "N" + ((Xor) gate).in1 + ((Xor) gate).in2 + ((Xor) gate).out;
                Node node = graph.addNode(nodeLabel);
                node.addAttribute("ui.label", nodeLabel + "/XOR");
                node.setAttribute("ui.class", "xor");
            }

            if (gate instanceof Register) {
                String nodeLabel = "N" + ((Register) gate).in + ((Register) gate).out;
                Node node = graph.addNode(nodeLabel);
                node.addAttribute("ui.label", nodeLabel + "/REGISTER");
                node.setAttribute("ui.class", "register");
            }
        }

        for (int i = 0; i < model.length; i++) {
            if (model[i] > 1000) {
                int connection = model[i];
                int leftNode = connection / 1000;
                int rightNode = connection % 1000;

                String labelLeftNode = leftNode == 1 ? "N1" : "N" + circuit.getGateByOutput(leftNode);
                String labelRightNode = rightNode == 2? "N2" : "N" + circuit.getGateByInput(rightNode);

                Edge edge = graph.addEdge("E" + leftNode + rightNode, labelLeftNode ,labelRightNode, true);
                edge.setAttribute("ui.label", "E" + leftNode + rightNode);
            }
        }

        String stylesheet = readFile("/home/jakob/Projects/ConvolutionalSAT/src/stylesheet.css", StandardCharsets.UTF_8);
        graph.addAttribute("ui.stylesheet", stylesheet);

        graph.display();
    }

    private static String readFile(String path, Charset encoding) {
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(path));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded, encoding);
    }
}
