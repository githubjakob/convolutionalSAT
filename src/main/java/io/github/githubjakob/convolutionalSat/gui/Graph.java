package io.github.githubjakob.convolutionalSat.gui;

import io.github.githubjakob.convolutionalSat.Circuit;
import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.Component;
import io.github.githubjakob.convolutionalSat.components.Connection;
import io.github.githubjakob.convolutionalSat.components.Gate;
import io.github.githubjakob.convolutionalSat.components.InputPin;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * Created by jakob on 26.06.18.
 */
public class Graph extends MultiGraph {

    Circuit model;

    Node root;

    private String stylesheet = readFile("stylesheet.css", Charset.forName("utf-8"));

    public Graph(Circuit model) {
        super("Network");
        this.model = model;
        createGraph();
    }

    public boolean isGraphFullyConnected() {

        Iterator<Node> it = this.root.getBreadthFirstIterator();

        int reachableNodes = 0;
        while(it.hasNext()) {
            Node next = it.next();
            reachableNodes++;
        }

        return this.getNodeCount() == reachableNodes;
    }

    private void createGraph() {
        // DRAW
        this.addAttribute("ui.stylesheet", "edge {text-alignment: along;}");
        this.addAttribute("ui.quality");
        this.addAttribute("ui.antialias");

        Map<String, Node> nodes = new HashMap<>();

        final Map<Component, int[][]> bitsAtNodes = model.getBitsAtNodes();

        for (Gate gate : model.getGates()) {
            Node node = this.addNode(gate.toString());
            int[][] bitsStreams = bitsAtNodes.get(gate.getOutputPin());

            if (gate.getType().equals("input") && gate.getModule().equals(Enums.Module.ENCODER)) {
                root = node;
            }

            String bitsStreamsOfNode = "";
            for (int[] bits : bitsStreams) {
                bitsStreamsOfNode = bitsStreamsOfNode +":" + Arrays.toString(bits);
            }

            node.addAttribute("ui.label", gate.getType() + bitsStreamsOfNode);
            node.addAttribute("ui.class", gate.getModule().toString());
            registerOutputPin(gate, node, nodes);
            registerInputPins(gate, node, nodes);
        }

        for (Connection connection : model.getConnections()) {
            if (connection.getFrom() == null) {
                continue;
            }
            if (connection.getTo() == null) {
                continue;
            }
            String from = connection.getFrom().toString();
            String to = connection.getTo().toString();
            Node nodeFrom = nodes.get(from);
            Node nodeTo = nodes.get(to);
            Edge edge = this.addEdge(from + to, nodeFrom, nodeTo, true);
            edge.addAttribute("ui.label", connection.toString());
            edge.addAttribute("layout.weight", 2);
        }

        this.addAttribute("ui.stylesheet", stylesheet);

        boolean connected = isGraphFullyConnected();
        this.setAttribute("fullyConnected", connected);

    }



    private void registerInputPins(Gate gate, Node node, Map<String, Node> nodes) {
        for (InputPin inputPin : gate.getInputPins()) {
            nodes.put(inputPin.toString(), node);
        }
    }

    private void registerOutputPin(Gate gate, Node node, Map<String, Node> nodes) {
        nodes.put(gate.getOutputPin().toString(), node);
    }

    private String readFile(String filename, Charset encoding) {
        ClassLoader classLoader = getClass().getClassLoader();
        String stylesheet = classLoader.getResource(filename).getPath();
        byte[] encoded = new byte[0];
        try {
            encoded = Files.readAllBytes(Paths.get(stylesheet));
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new String(encoded, encoding);
    }
}
