package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Optional;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_COLOR_GRAY;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_RADIUS_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_STYLE_SOLID;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_WIDTH_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BOX_SHADOW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;

public class IconLabelContainer extends Div implements HasRender {

    private Icon icon;
    private String titleText;
    private final Div container = new Div();

    public IconLabelContainer() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_COLUMN, STYLE_GAP_S);
    }

    private Component createHeader() {
        return new TextWithIcon(
            Optional.ofNullable(icon).orElse(VaadinIcon.DATABASE.create()),
            Optional.ofNullable(titleText).orElse("Stats"));
    }

    private Component createContainer() {
        container.removeAll();
        container.addClassNames(STYLE_PADDING_M, STYLE_BOX_SHADOW,
            STYLE_BORDER_WIDTH_S, STYLE_BORDER_COLOR_GRAY, STYLE_BORDER_RADIUS_S, STYLE_BORDER_STYLE_SOLID);

        return container;
    }

    public void addToContainer(Component... components) {
        container.add(components);
    }

    public void addToContainer(String text) {
        container.add(text);
    }

    public void addClassNamesToContainer(String... classes) {
        container.addClassNames(classes);
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }

    @Override
    public void render() {
        removeAll();
        add(createHeader(), createContainer());
    }
}
