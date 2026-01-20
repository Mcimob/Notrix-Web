package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.model.enums.LabelCategory;
import ch.ethz.inf.peachlab.model.enums.MainLabel;
import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.svg.Svg;
import com.vaadin.flow.component.svg.elements.Rect;

import java.util.Map;
import java.util.stream.Collectors;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

public class TransitionSidebar extends Div implements HasRender {

    private static final int RECT_SPACING = 70;
    private static final int VIEWBOX_WIDTH = 400;
    private static final int RECT_WIDTH = 60;

    private Map<MainLabel, Integer> stageFrequencies;
    private Integer[][] transitionMatrix;

    public TransitionSidebar() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL, STYLE_FLEX_COLUMN, STYLE_FLEX_ALIGN_CENTER);
    }

    @Override
    public void render() {
        removeAll();
        if (stageFrequencies == null || transitionMatrix == null) {
            return;
        }
        Svg svg = new Svg();
        int count = stageFrequencies.size();
        float maxHeight = count * RECT_SPACING;
        svg.viewbox(0, 0, VIEWBOX_WIDTH, maxHeight);

        svg.addClassNames(STYLE_HEIGHT_FULL);
        Integer maxValue = stageFrequencies.values().stream()
                .max(Integer::compareTo)
                .orElseThrow();
        stageFrequencies.forEach((key, value) -> {
            Rect rect = new Rect("stage" + key, RECT_WIDTH, 15 + (value * 45F / maxValue));
            rect.move((VIEWBOX_WIDTH - RECT_WIDTH) / 2F, key.ordinal() * RECT_SPACING);
            rect.setFillColor(key.getColor());
            rect.setAttribute("rx", "3");
            rect.setAttribute("ry", "3");
            rect.setStroke(
                    key.getLabelCategory() == LabelCategory.DATA_ORIENTED
                            || key.getLabelCategory() == LabelCategory.MODEL_ORIENTED
                            ? "#666" : "none",
                    3);
            svg.add(rect);
        });

        add(svg);
    }

    public void setStageFrequencies(Map<MainLabel, Integer> stageFrequencies) {
        this.stageFrequencies = stageFrequencies.entrySet().stream()
                .filter(e -> e.getKey().ordinal() <= MainLabel.DATA_EXPORT.ordinal())
                .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    public void setTransitionMatrix(Integer[][] transitionMatrix) {
        this.transitionMatrix = transitionMatrix;
    }
}
