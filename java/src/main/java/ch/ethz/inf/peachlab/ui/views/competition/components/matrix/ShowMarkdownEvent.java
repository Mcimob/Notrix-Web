package ch.ethz.inf.peachlab.ui.views.competition.components.matrix;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEvent;

import java.io.Serial;

public class ShowMarkdownEvent extends ComponentEvent<Component> {

    @Serial
    private static final long serialVersionUID = -905683705772231614L;
    private final boolean show;

    public ShowMarkdownEvent(boolean show, Component source, boolean fromClient) {
        super(source, fromClient);
        this.show = show;
    }

    public boolean getShow() {
        return show;
    }
}
