package ch.ethz.inf.peachlab.ui.views.competition.matrix;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;

@DomEvent("cluster-click")
public class ClusterClickEvent extends ComponentEvent<Component> {

    private final Long localClusterId;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public ClusterClickEvent(Component source, boolean fromClient, @EventData("event.detail") String clusterId) {
        super(source, fromClient);
        this.localClusterId = Long.valueOf(clusterId);
    }

    public Long getLocalClusterId() {
        return localClusterId;
    }
}
