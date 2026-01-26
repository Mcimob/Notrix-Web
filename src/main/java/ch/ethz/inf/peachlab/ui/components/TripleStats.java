package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.springframework.data.util.Pair;

import java.util.List;
import java.util.Optional;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_COLOR_GRAY;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_RADIUS_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_STYLE_SOLID;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_WIDTH_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BOX_SHADOW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_BETWEEN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_JUSTIFY_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_150PX;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_WRAP_NO;

public class TripleStats extends Div implements HasRender {

    private List<Pair<String, Object>> stats;
    private Icon icon;
    private String titleText;

    public TripleStats() {
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
        Div container = new Div();
        container.addClassNames(STYLE_FLEX_COLUMN, STYLE_FLEX_JUSTIFY_CENTER, STYLE_GAP_M,
            STYLE_PADDING_M, STYLE_HEIGHT_150PX, STYLE_BOX_SHADOW,
            STYLE_BORDER_WIDTH_S, STYLE_BORDER_COLOR_GRAY, STYLE_BORDER_RADIUS_S, STYLE_BORDER_STYLE_SOLID);

        stats.stream()
            .map(p -> {
                Div row = new Div(new Span(p.getFirst()), new Span(p.getSecond().toString()));
                row.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN, STYLE_GAP_M, STYLE_TEXT_WRAP_NO);
                return row;
            }).forEach(container::add);

        return container;
    }

    @Override
    public void render() {
        removeAll();
        if (stats == null || stats.size() > 3) {
            return;
        }

        add(createHeader());
        add(createContainer());
    }

    public void setStats(List<Pair<String, Object>> stats) {
        this.stats = stats;
    }

    public void setIcon(Icon icon) {
        this.icon = icon;
    }

    public void setTitleText(String titleText) {
        this.titleText = titleText;
    }
}
