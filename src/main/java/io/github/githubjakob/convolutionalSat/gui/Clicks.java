package io.github.githubjakob.convolutionalSat.gui;

import io.github.githubjakob.convolutionalSat.Circuit;
import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerListener;
import org.graphstream.ui.view.ViewerPipe;

import java.util.HashMap;
import java.util.Map;

public class Clicks implements ViewerListener {


    private final Viewer viewer;
    private final Graph graph;
    protected boolean loop = true;

    Map<String, String> cache = new HashMap<>();

    public Clicks(Viewer viewer, Graph graph) {
        this.viewer = viewer;
        this.graph = graph;

        viewer.setCloseFramePolicy(Viewer.CloseFramePolicy.HIDE_ONLY);

        ViewerPipe fromViewer = viewer.newViewerPipe();
        fromViewer.addViewerListener(this);
        fromViewer.addSink(graph);

        while(loop) {
            fromViewer.pump();
        }
    }

    public void viewClosed(String id) {
        loop = false;
    }

    public void buttonPushed(String id) {
        Node node = graph.getNode(id);
        System.out.println((String)node.getAttribute("ui.type"));
    }

    public void buttonReleased(String id) {
        //Node node = graph.getNode(id);
        //System.out.println((String)node.getAttribute("ui.type"));
    }
}