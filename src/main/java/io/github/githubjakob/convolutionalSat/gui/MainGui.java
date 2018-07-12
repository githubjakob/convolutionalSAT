package io.github.githubjakob.convolutionalSat.gui;

import io.github.githubjakob.convolutionalSat.Main;
import io.github.githubjakob.convolutionalSat.components.*;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

import javax.swing.*;

import java.awt.*;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by jakob on 11.06.18.
 */
public class MainGui {

    JFrame jFrame;

    private JPanel panel;

    private JTabbedPane tabbedPane;

    private JLabel legend;

    private JLabel solutions;

    private JComboBox comboBox;

    public List<Graph> graphs = new ArrayList<>();

    public MainGui() {
        jFrame = new JFrame("main/java/io/github/githubjakob/convolutionalSat/Gui");
        jFrame.setContentPane(getPanel());
        jFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        jFrame.setSize(1600, 1200);
        jFrame.setLocationRelativeTo(null);
        jFrame.setVisible(true);

        legend.setFont(new Font(Font.DIALOG, Font.BOLD, 25));
        solutions.setText("Maximale Lösungen: " + Main.MAX_NUMBER_OF_SOLUTIONS);
        solutions.setFont(new Font(Font.DIALOG, Font.BOLD, 25));

        System.setProperty("org.graphstream.ui.renderer", "org.graphstream.ui.j2dviewer.J2DGraphRenderer");

        tabbedPane.remove(0);
    }

    public void addPanel(Graph graph) {
        setupComboBox(graph);
        graphs.add(graph);
        Viewer viewer = new Viewer(graph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
        ViewPanel view = viewer.addDefaultView(false);
        Panel panel = new Panel();
        panel.setLayout(new BorderLayout());
        panel.add(view, BorderLayout.CENTER);
        viewer.enableAutoLayout();
        tabbedPane.add(panel);
        new Thread(new Runnable() {
            @Override
            public void run() {
                new Clicks(viewer, graph);
            }
        });

    }

    private void setupComboBox(Graph graph) {
        if (comboBox.getItemCount() > 0) {
            return;
        }
        List<BitStream> bitStreams = graph.getModel().getBitStreams();
        // listener
        for (BitStream bitstream : bitStreams) {
            comboBox.addItem("ui.bitstream" + bitstream.getId());
            comboBox.addItemListener(new ItemListener() {
                @Override
                public void itemStateChanged(ItemEvent e) {
                    String item = (String) e.getItem();
                    showBitStreamInLabel(item);
                }
            });
        }
        comboBox.addItem("ui.type");
    }

    public void showBitStreamInLabel(String attribute) {
        for (Graph graph : graphs) {
            graph.setLabel(attribute);
        }
    }

    public JPanel getPanel() {
        return this.panel;
    }
}
