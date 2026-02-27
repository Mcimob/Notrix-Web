package ch.ethz.inf.peachlab.ui.views.competition.matrix;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

import java.io.Serial;

public class ShowHeightEvent extends ComponentEvent<Component> {

    @Serial
    private static final long serialVersionUID = -3409173764922082526L;
    private final boolean show;

    public ShowHeightEvent(boolean show, Component source, boolean fromClient) {
        super(source, fromClient);
        this.show = show;
    }

    public boolean getShow() {
        return show;
    }
}
