package ch.ethz.inf.peachlab.ui.views.competition.matrix;

import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;

import java.io.Serial;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_BETWEEN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;

public class Filterbar extends Div implements HasRender {

    @Serial
    private static final long serialVersionUID = 4053307060539567671L;

    public Filterbar() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN);
    }

    private Component createLeftButtons() {
        Div div = new Div();
        div.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_FLEX_ALIGN_CENTER);

        div.add(createClusterButton());
        return div;
    }

    private Component createClusterButton() {
        ToggleButton button = new ToggleButton("Cluster");
        button.addValueChangeListener(change ->
            fireEvent(new ClusterEvent(change.getValue(), this, change.isFromClient())));

        return button;
    }

    private Component createRightButtons() {
        Div div = new Div();
        div.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_FLEX_ALIGN_CENTER);

        div.add(new Text("Show:"));
        div.add(createHeightButton());
        div.add(createMarkdownButton());
        return div;
    }

    private Component createHeightButton() {
        ToggleButton button = new ToggleButton("Height");
        button.addValueChangeListener(change ->
                fireEvent(new ShowHeightEvent(change.getValue(), this, change.isFromClient())));

        return button;
    }

    private Component createMarkdownButton() {
        ToggleButton button = new ToggleButton("Markdown");
        button.addValueChangeListener(change ->
                fireEvent(new ShowMarkdownEvent(change.getValue(), this, change.isFromClient())));

        return button;
    }

    @Override
    public void render() {
        removeAll();
        add(createLeftButtons(), createRightButtons());
    }

    public Registration addMarkdownButtonListener(ComponentEventListener<ShowMarkdownEvent> listener) {
        return addListener(ShowMarkdownEvent.class, listener);
    }

    public Registration addHeightButtonListener(ComponentEventListener<ShowHeightEvent> listener) {
        return addListener(ShowHeightEvent.class, listener);
    }

    public Registration addClusterListener(ComponentEventListener<ClusterEvent> listener) {
        return addListener(ClusterEvent.class, listener);
    }
}
