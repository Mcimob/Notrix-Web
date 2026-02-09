package ch.ethz.inf.peachlab.ui.views.competition.matrix;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class ClusterEvent extends ComponentEvent<Component> {

    private final boolean cluster;

    public ClusterEvent(boolean cluster, Component source, boolean fromClient) {
        super(source, fromClient);
        this.cluster = cluster;
    }

    public boolean isCluster() {
        return cluster;
    }
}
