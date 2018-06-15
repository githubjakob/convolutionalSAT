package io.github.githubjakob.convolutionalSat.Gui;

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
    private final Circuit model;
    protected boolean loop = true;

    Map<String, String> cache = new HashMap<>();

    public Clicks(Viewer viewer, Graph graph, Circuit model) {
        this.model = model;
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

        if (cache.containsKey(id)) {
            node.setAttribute("ui.label", cache.get(id));
            cache.remove(id);
        } else {

        }



        System.out.println("Button pushed on node "+id);
    }

    public void buttonReleased(String id) {



        System.out.println("Button released on node "+id);
    }
}