package ch.ethz.inf.peachlab.ui.views.competition.matrix;

import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.shared.Registration;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_BETWEEN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;

public class Filterbar extends Div implements HasRender {

    public Filterbar() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN);
    }

    private Component createRightButtons() {
        Div div = new Div();
        div.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_FLEX_ALIGN_CENTER);

        div.add(new Text("Show:"));
        div.add(createMarkdownButton());
        return div;
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
        add(new Div());
        add(createRightButtons());
    }

    public Registration addMarkdownButtonListener(ComponentEventListener<ShowMarkdownEvent> listener) {
        return addListener(ShowMarkdownEvent.class, listener);
    }
}
