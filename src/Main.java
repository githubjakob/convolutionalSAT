import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
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
                new int[] { 0, 1, 0, 1 },
                new int[] { 0, 1, 1, 0 }
                );

        //circuit.addGate(xor);
        circuit.add("xor");
        circuit.add("register");
        //circuit.add("register");

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
            //plotCircuit(model, circuit);

        } else {
            System.out.println("is not satisfiable");
        }
    }

    private static void plotCircuit(int[] model, Circuit circuit) throws IOException {
        Graph graph = new SingleGraph("Tutorial 1");

        //graph.addNode("1"); //INPUT
        //graph.addNode("2"); //OUTPUT

        /*HashSet<Integer> nodesCreated = new HashSet<>();
        for (int i = 0; i < model.length; i++) {
            if (model[i] > 1000) {
                int edge = model[i];
                int leftNode = edge / 1000;
                int rightNode = edge % 1000;
                /*if (!nodesCreated.contains(leftNode)) {
                    Node node = graph.addNode("N" + leftNode);
                    node.addAttribute("ui.label", "N" + leftNode);
                    nodesCreated.add(leftNode);
                }
                if (!nodesCreated.contains(rightNode)) {
                    Node node = graph.addNode("N" + rightNode + "");
                    node.addAttribute("ui.label", "N" + rightNode);
                    nodesCreated.add(rightNode);

                }

                graph.addEdge("N" + leftNode + rightNode, "N" + leftNode, "N" + rightNode);
            }
        }*/


        for (Gate gate : circuit.gates) {
            if (gate instanceof Xor) {

                String nodeLabel = "N" + ((Xor) gate).in1 + ((Xor) gate).in2 + ((Xor) gate).out;
                Node node = graph.addNode(nodeLabel);
                node.addAttribute("ui.label", "XOR");

                String edgeLabel1 = "N" + ((Xor) gate).in1 + "" + ((Xor) gate).out + "";
                String edgeLabel2 = "N" + ((Xor) gate).in2 + "" +  ((Xor) gate).out + "";
                //graph.addEdge(edgeLabel1, "N" + ((Xor) gate).in1, "N" + ((Xor) gate).out);
                //graph.addEdge(edgeLabel2, "N" + ((Xor) gate).in2, "N" + ((Xor) gate).out);

            }
            if (gate instanceof Register) {

                String nodeLabel = "N" + ((Register) gate).in + ((Register) gate).out;
                Node node = graph.addNode(nodeLabel);
                node.addAttribute("ui.label", "REGISTER");

                String label = "N" + ((Register) gate).in + "" + ((Register) gate).out + "";
                int in = ((Register) gate).in;
                int out = ((Register) gate).out;
                //graph.addEdge(label, "N" + in, "N" + out);

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
