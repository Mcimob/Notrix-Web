package ch.ethz.inf.peachlab.ui.views.competition.matrix;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;
import com.vaadin.flow.component.DomEvent;
import com.vaadin.flow.component.EventData;

@DomEvent("kernel-click")
public class KernelClickEvent extends ComponentEvent<Component> {

    private final Long kernelId;

    /**
     * Creates a new event using the given source and indicator whether the
     * event originated from the client side or the server side.
     *
     * @param source     the source component
     * @param fromClient <code>true</code> if the event originated from the client
     *                   side, <code>false</code> otherwise
     */
    public KernelClickEvent(Component source, boolean fromClient, @EventData("event.detail") String kernelId) {
        super(source, fromClient);
        this.kernelId = Long.valueOf(kernelId);
    }

    public Long getKernelId() {
        return kernelId;
    }
}
