package ch.ethz.inf.peachlab.ui.views.competition.matrix;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

public class ShowHeightEvent extends ComponentEvent<Component> {

    private final boolean show;

    public ShowHeightEvent(boolean show, Component source, boolean fromClient) {
        super(source, fromClient);
        this.show = show;
    }

    public boolean getShow() {
        return show;
    }
}
