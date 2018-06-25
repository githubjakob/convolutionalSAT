package io.github.githubjakob.convolutionalSat.gui;

import io.github.githubjakob.convolutionalSat.Circuit;
import io.github.githubjakob.convolutionalSat.Main;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.Component;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.io.IOException;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by jakob on 11.06.18.
 */
public class MainApp {

    JFrame jFrame;

    private JPanel panel;

    private JList modelList;

    private JTabbedPane tabbedPane;
    private JLabel label;
    private JLabel legend;
    private JLabel solutions;

    public MainApp(java.util.List<Circuit> models) {
        jFrame = new JFrame("main/java/io/github/githubjakob/convolutionalSat/Gui");
        jFrame.setContentPane(getPanel());
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(1600, 1200);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

        String inputBits = "Input Bit Stream: ";
        int numberOfBits = models.get(0).getNumberOfBitsPerBitStream();
        for (int i = 0; i < numberOfBits; i++) {
            inputBits = inputBits + Main.inputBits1[i];
        }
        legend.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        label.setText(inputBits);
        solutions.setText("Maximale Lösungen: " + Main.MAX_NUMBER_OF_SOLUTIONS);
        solutions.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        label.setFont(new Font(Font.DIALOG, Font.BOLD, 25));

        // graphs
        tabbedPane.remove(0);
        for (Circuit model : models) {
            plotCircuitForModel(model);
        }
    }

    public JPanel getPanel() {
        return this.panel;
    }

    public void plotCircuitForModel(Circuit model) {
        // DRAW
        MultiGraph graph = new MultiGraph("Network");
        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");
        graph.addAttribute("ui.stylesheet", "edge {text-alignment: along;}");
        graph.addAttribute("ui.quality");
        graph.addAttribute("ui.antialias");

        Map<String, Node> nodes = new HashMap<>();

        final Map<Component, int[][]> bitsAtNodes = model.getBitsAtNodes();

        for (Gate gate : model.getGates()) {
            Node inputBitStream = graph.addNode(gate.toString());
            int[][] bitsStreams = bitsAtNodes.get(gate.getOutputPin());

            String bitsStreamsOfNode = "";
            for (int[] bits : bitsStreams) {
                bitsStreamsOfNode = bitsStreamsOfNode +":" + Arrays.toString(bits);
            }

            inputBitStream.addAttribute("ui.label", gate.getType() + bitsStreamsOfNode);
            inputBitStream.addAttribute("ui.class", gate.getModule().toString());
            registerOutputPin(gate, inputBitStream, nodes);
            registerInputPins(gate, inputBitStream, nodes);
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
            Edge edge = graph.addEdge(from + to, nodeFrom, nodeTo, true);
            edge.addAttribute("ui.label", connection.toString());
            edge.addAttribute("layout.weight", 2);
        }

        String stylesheet = readFile("stylesheet.css", Charset.forName("utf-8"));
        graph.addAttribute("ui.stylesheet", stylesheet);

        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        ViewPanel view = viewer.addDefaultView(false);

        Panel panel = new Panel();
        panel.setLayout(new BorderLayout());
        panel.add(view, BorderLayout.CENTER);
        viewer.enableAutoLayout();
        tabbedPane.add(panel);

        // listener
        //Clicks clicks = new Clicks(viewer, graph, model);

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
