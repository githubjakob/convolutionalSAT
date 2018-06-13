package io.github.githubjakob.convolutionalSat.Gui;

import io.github.githubjakob.convolutionalSat.Circuit;
import io.github.githubjakob.convolutionalSat.components.*;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;
import java.awt.*;
import java.io.File;
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

    public MainApp(java.util.List<Circuit> models) {
        jFrame = new JFrame("main/java/io/github/githubjakob/convolutionalSat/Gui");
        jFrame.setContentPane(getPanel());
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(1600, 1200);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

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


        Node inputBitStream = graph.addNode("INPUT"); //INPUT
        inputBitStream.addAttribute("ui.label", "1/INPUT");
        registerOutputPin(model.getInput(), inputBitStream, nodes);

        Node outputBitStream = graph.addNode("OUTPUT"); //OUTPUT
        outputBitStream.addAttribute("ui.label", "2/OUTPUT");
        registerInputPins(model.getOutput(), outputBitStream, nodes);

        for (Register register : model.getRegisters()) {
            String id = register.getInputPins().get(0) + "_" + register.getOutputPin();
            Node registerNode = graph.addNode(id);
            registerNode.addAttribute("ui.label", id);
            registerOutputPin(register, registerNode, nodes);
            registerInputPins(register, registerNode, nodes);
            registerNode.setAttribute("ui.class", "register");

        }

        for (Xor xor : model.getXors()) {
            String id = xor.getInputPins().get(0) + "_" + xor.getInputPins().get(1) + "_" + xor.getOutputPin();
            Node xorNode = graph.addNode(id);
            xorNode.addAttribute("ui.label", id);
            registerOutputPin(xor, xorNode, nodes);
            registerInputPins(xor, xorNode, nodes);
            xorNode.setAttribute("ui.class", "xor");
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
            graph.addEdge(from+to, nodeFrom, nodeTo);
        }

        ClassLoader classLoader = getClass().getClassLoader();
        File stylesheet = new File(classLoader.getResource("stylesheet.css").getFile());

        graph.addAttribute("ui.stylesheet", stylesheet);

        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        ViewPanel view = viewer.addDefaultView(false);

        Panel panel = new Panel();
        panel.setLayout(new BorderLayout());
        panel.add(view, BorderLayout.CENTER);
        viewer.enableAutoLayout();

        tabbedPane.add(panel);

    }

    private void registerInputPins(Gate gate, Node node, Map<String, Node> nodes) {
        for (InputPin inputPin : gate.getInputPins()) {
            nodes.put(inputPin.toString(), node);
        }
    }

    private void registerOutputPin(Gate gate, Node node, Map<String, Node> nodes) {
        nodes.put(gate.getOutputPin().toString(), node);
    }
}
