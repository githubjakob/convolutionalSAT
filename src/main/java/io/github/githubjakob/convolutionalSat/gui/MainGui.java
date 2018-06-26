package io.github.githubjakob.convolutionalSat.gui;

import io.github.githubjakob.convolutionalSat.Circuit;
import io.github.githubjakob.convolutionalSat.Enums;
import io.github.githubjakob.convolutionalSat.Main;
import io.github.githubjakob.convolutionalSat.components.*;
import io.github.githubjakob.convolutionalSat.components.Component;
import org.graphstream.graph.Edge;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.graph.implementations.MultiGraph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;

import java.awt.*;
import java.util.List;

/**
 * Created by jakob on 11.06.18.
 */
public class MainGui {

    JFrame jFrame;

    private JPanel panel;

    private JList modelList;

    private JTabbedPane tabbedPane;
    private JLabel label;
    private JLabel legend;
    private JLabel solutions;
    ;

    public MainGui(List<BitStream> bitStreams) {
        jFrame = new JFrame("main/java/io/github/githubjakob/convolutionalSat/Gui");
        jFrame.setContentPane(getPanel());
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(1600, 1200);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

        String inputBits = "Input Bit Stream: ";

        int numberOfBits = bitStreams.get(0).getLength();
        for (Bit bit : bitStreams.get(0)) {
            inputBits = inputBits + bit.toString();
        }
        legend.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        label.setText(inputBits);
        solutions.setText("Maximale Lösungen: " + Main.MAX_NUMBER_OF_SOLUTIONS);
        solutions.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        label.setFont(new Font(Font.DIALOG, Font.BOLD, 25));

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        // graphs
        tabbedPane.remove(0);

        // listener
        //Clicks clicks = new Clicks(viewer, graph, model);
    }

    public void addPanel(MultiGraph graph) {
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);

        ViewPanel view = viewer.addDefaultView(false);

        boolean fullyConnected = graph.getAttribute("fullyConnected");

        Panel panel = new Panel();
        panel.setName("panel" + (fullyConnected ? "*" : ""));
        panel.setLayout(new BorderLayout());
        panel.add(view, BorderLayout.CENTER);
        viewer.enableAutoLayout();
        tabbedPane.add(panel);
    }

    public JPanel getPanel() {
        return this.panel;
    }
}
