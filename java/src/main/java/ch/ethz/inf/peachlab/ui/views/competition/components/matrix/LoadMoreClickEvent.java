package ch.ethz.inf.peachlab.ui.views.competition.components.matrix;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;

import java.io.Serial;

@DomEvent("load-more-click")
public class LoadMoreClickEvent extends ComponentEvent<Component> {

    @Serial
    private static final long serialVersionUID = 1998300473303236681L;
    private final long currentSize;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public LoadMoreClickEvent(Component source, boolean fromClient, @EventData("event.detail") Integer currentSize) {
        super(source, fromClient);
        this.currentSize = currentSize;
    }

    public long getCurrentSize() {
        return currentSize;
    }
}
