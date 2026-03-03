package ch.ethz.inf.peachlab.ui.views.competition.components.matrix;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

import java.io.Serial;

public class ClusterEvent extends ComponentEvent<Component> {

    @Serial
    private static final long serialVersionUID = 6191397875277510789L;
    private final boolean cluster;

    public ClusterEvent(boolean cluster, Component source, boolean fromClient) {
        super(source, fromClient);
        this.cluster = cluster;
    }

    public boolean isCluster() {
        return cluster;
    }
}
