package ch.ethz.inf.peachlab.ui.views.competition;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class KernelHoverEvent extends ComponentEvent<Component> {

    private final int index;
    public KernelHoverEvent(int index, Component source, boolean fromClient) {
        super(source, fromClient);
        this.index = index;
    }

    public int getIndex() {
        return index;
    }
}
