package io.github.githubjakob.convolutionalSat.graph;

import io.github.githubjakob.convolutionalSat.Circuit;
import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.connection.Connection;
import io.github.githubjakob.convolutionalSat.components.gates.Gate;
import io.github.githubjakob.convolutionalSat.components.gates.GlobalOutput;
import lombok.Getter;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;

import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

/**
 * Created by jakob on 26.06.18.
 */
public class Graph extends MultiGraph {

    @Getter
    Circuit circuit;

    Node root;

    private String stylesheet = readFile("stylesheet.css", Charset.forName("utf-8"));

    public Graph(Circuit circuit) {
        super("Network");
        this.circuit = circuit;
        createGraph();
    }

    public boolean isValid() {
        if (!isGraphFullyConnected()) return false;
        return true;
    }

    public boolean isGraphFullyConnected() {

        Iterator<Node> it = this.root.getBreadthFirstIterator(true);

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

        final Map<Component, int[][]> bitsAtNodes = circuit.getBitsAtNodes();
        final Map<Component, Integer> microticksAtNodes = circuit.getMicrotickAsDecimal();

        for (Gate gate : circuit.getGates()) {
            Node node = this.addNode(gate.toString());

            int[][] bitsStreams = bitsAtNodes.get(gate.getOutputPin());

            if (gate.getType().equals("input") && gate.getModule().getType().equals(Enums.Module.ENCODER)) {
                root = node;
                node.addAttribute("ui.class", "globalinput");
            }

            String microtick = "gate null";
            if (gate != null) {
                microtick = microticksAtNodes.get(gate) != null ? microticksAtNodes.get(gate) + "" : "";
            }


            int count = 0;
            for (int[] bits : bitsStreams) {
                if (count == 0) {
                    node.addAttribute("ui.label", gate.toString() + "[" + microtick + "]" + Arrays.toString(bits));
                }
                node.addAttribute("ui.bitstream" + count, gate.getType() + Arrays.toString(bits));
                count++;
            }

            node.addAttribute("ui.type", gate.getType());
            node.addAttribute("ui.class", gate.getModule().getType().toString());
            //node.addAttribute("ui.class", gate.getType());
            registerOutputPin(gate, node, nodes);
            registerInputPins(gate, node, nodes);
        }

        for (Connection connection : circuit.getConnections()) {
            if (connection.getFrom() == null) {
                continue;
            }
            if (connection.getTo() == null) {
                continue;
            }
            OutputPin from = connection.getFrom();
            String fromId = from.toString();
            InputPin to = connection.getTo();
            String toId = to.toString();
            Node nodeFrom = nodes.get(fromId);
            Node nodeTo = nodes.get(toId);
            if (to.getGate().getOutputPin().getConnections().isEmpty() && !(to.getGate() instanceof GlobalOutput)) {
                //System.out.println(nodeTo.toString() + " does not have any connections ");
            }
            Edge edge = this.addEdge(fromId + toId, nodeFrom, nodeTo, true);
            edge.addAttribute("layout.weight", 2);



            //edge.addAttribute("ui.label", connections.toString());

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

    public void setLabel(String attribute) {
        for (Node node : this.getEachNode()) {

            if (attribute.equals("ui.type")) {
                String value = node.getAttribute("ui.type");
                node.setAttribute("ui.label", value);
            } else if (attribute.contains("bitstream")) {
                String value = node.getAttribute(attribute);
                node.setAttribute("ui.label", value);
            }

        }
    }
}
