package ch.ethz.inf.peachlab.ui.components.sidebar;

import ch.ethz.inf.peachlab.model.enums.LabelCategory;
import ch.ethz.inf.peachlab.model.enums.MainLabel;
import ch.ethz.inf.peachlab.ui.components.DivWithTooltip;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_COLOR_GRAY;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_RADIUS_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_STYLE_SOLID;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_WIDTH_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BOX_SHADOW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FW_500;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

public class TransitionSidebar extends DivWithTooltip {

    private Map<MainLabel, Integer> stageFrequencies;
    private Integer[][] transitionMatrix;
    private String[] opacityTargets;

    public TransitionSidebar() {
        super(".with-hover");
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_COLUMN, STYLE_FLEX_CENTER, STYLE_BACKGROUND_WHITE, STYLE_GAP_S);
    }

    private Component createReactComponent() {
        TransitionSidebarReact sidebar = new TransitionSidebarReact();
        sidebar.setData(stageFrequencies, transitionMatrix);
        sidebar.setOpacityTargets(opacityTargets);
        sidebar.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);

        return sidebar;
    }

    private Component createLegend() {
        Div div = new Div();
        div.addClassNames("legend-box");
        List<LabelCategory> bigCategories = List.of(LabelCategory.DATA_ORIENTED, LabelCategory.MODEL_ORIENTED);
        bigCategories.forEach(cat -> {
            List<MainLabel> labels = Arrays.stream(MainLabel.values())
                .filter(l -> l.getLabelCategory() == cat)
                .filter(l -> stageFrequencies.containsKey(l))
                .toList();
            if (labels.isEmpty())
                return;
            div.add(createLegendBox(cat, labels));
        });
        stageFrequencies.keySet().stream()
            .filter(l -> l.ordinal() <= MainLabel.DATA_EXPORT.ordinal())
            .filter(l -> !bigCategories.contains(l.getLabelCategory()))
            .map(this::createLegendBullet)
            .forEach(div::add);
        return div;
    }

    private Component createLegendBullet(MainLabel label) {
        Div colorDiv = new Div();
        colorDiv.addClassNames(STYLE_BOX_SHADOW);
        colorDiv.setWidth("12px");
        colorDiv.setHeight("12px");
        colorDiv.getStyle().setBackgroundColor(label.getColor());
        if (label.getLabelCategory() == LabelCategory.DATA_ORIENTED
            || label.getLabelCategory() == LabelCategory.MODEL_ORIENTED) {
            colorDiv.getStyle()
                .setBorder("2px %s %s".formatted(
                    label.getLabelCategory() == LabelCategory.MODEL_ORIENTED ? "dashed" : "solid",
                    "#666"));
        }

        Div div = new Div(colorDiv, new Text(getTranslation(label.getTitleKey())));
        div.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_FLEX_ALIGN_CENTER);

        return div;
    }

    private Component createLegendBox(LabelCategory category, List<MainLabel> labels) {
        Span title = new Span(getTranslation(category.getTitleKey()));
        title.addClassNames(STYLE_FW_500);

        Div box = new Div(title);
        box.addClassNames(STYLE_FLEX_COLUMN, STYLE_GAP_S, STYLE_PADDING_S, STYLE_BOX_SHADOW,
            STYLE_BORDER_WIDTH_S, STYLE_BORDER_COLOR_GRAY, STYLE_BORDER_RADIUS_S, STYLE_BORDER_STYLE_SOLID);
        labels.stream()
            .map(this::createLegendBullet)
            .forEach(box::add);

        return box;
    }

    @Override
    public void render() {
        super.render();
        if (stageFrequencies == null
            || transitionMatrix == null
            || opacityTargets == null) {
            return;
        }
        add(createReactComponent());
        add(createLegend());
    }

    public void setStageFrequencies(Map<MainLabel, Integer> stageFrequencies) {
        this.stageFrequencies = stageFrequencies;
    }

    public void setTransitionMatrix(Integer[][] transitionMatrix) {
        this.transitionMatrix = transitionMatrix;
    }

    public void setOpacityTargets(String[] opacityTargets) {
        this.opacityTargets = opacityTargets;
    }
}
