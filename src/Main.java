import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.graph.implementations.SingleGraph;
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
import java.util.HashSet;
import java.util.List;

public class Main {

    final static int MAXVAR = 1000000;
    final static int NBCLAUSES = 1000000;

    public static ISolver solver = SolverFactory.newDefault();

    public static Reader reader = new DimacsReader( solver );

    public static void main(String[] args) throws ContradictionException, TimeoutException, IOException {

        // ADD input and output bits
        Circuit circuit = new Circuit(
                new int[] { 0, 1, 0, 1, 0 },
                new int[] { 0, 1, 1, 0, 0 }
                );

        //circuit.addGate(xor);
        circuit.add("xor");
        circuit.add("register");

        List<int[]> clauses = circuit.toBoolean();

        solver.newVar(MAXVAR);
        solver.setExpectedNumberOfClauses(NBCLAUSES);

        System.out.println("Adding clauses:");
        for (int i=0; i<clauses.size(); i++) {
            int [] clause = clauses.get(i);
            solver.addClause(new VecInt(clause));
            System.out.println(reader.decode(clause));
        }

        System.out.println("checking satisfiability");

        IProblem problem = solver;


        if (problem.isSatisfiable()) {
            int[] model = problem.model();
            System.out.println(reader.decode(model));
            System.out.println("is Satisfiable");

            // DRAW
            plotCircuit(model, circuit);

        } else {
            System.out.println("is not satisfiable");
        }
    }

    private static void plotCircuit(int[] model, Circuit circuit) throws IOException {
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

                String labelLeftNode = leftNode == 1 ? "N1" : "N" + circuit.getGateIdByOutput(leftNode);
                String labelRightNode = rightNode == 2? "N2" : "N" + circuit.getGateIdByInput(rightNode);

                Edge edge = graph.addEdge("E" + leftNode + rightNode, labelLeftNode ,labelRightNode, true);
                edge.setAttribute("ui.label", "E" + leftNode + rightNode);
            }
        }

        String stylesheet = readFile("/home/jakob/Projects/ConvolutionalSAT/src/stylesheet.css", StandardCharsets.UTF_8);
        graph.addAttribute("ui.stylesheet", stylesheet);

        graph.display();
    }

    static String readFile(String path, Charset encoding) throws IOException {
        byte[] encoded = Files.readAllBytes(Paths.get(path));
        return new String(encoded, encoding);
    }
}
