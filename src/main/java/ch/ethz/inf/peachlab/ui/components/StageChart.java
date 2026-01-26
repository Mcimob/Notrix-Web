package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.model.enums.MainLabel;
import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.stream.Collectors;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_COLOR_GRAY;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_RADIUS_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_STYLE_SOLID;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_WIDTH_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BOX_SHADOW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_JUSTIFY_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_JUSTIFY_END;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FONT_SIZE_XS;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_150PX;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

public class StageChart extends Div implements HasRender {

    private Map<MainLabel, Integer> stageStats;

    public StageChart() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_COLUMN, STYLE_GAP_S, STYLE_WIDTH_FULL);
    }

    @Override
    public void render() {
        removeAll();
        add(createTop(), createChart());
    }

    private Component createTop() {
        return new TextWithIcon(VaadinIcon.BAR_CHART_H.create(), "Stage Frequency Distribution");
    }

    private Component createChart() {
        DivWithTooltip chartContainer = new DivWithTooltip(".bar");
        chartContainer.addClassNames(STYLE_PADDING_M, STYLE_FLEX_ROW, STYLE_FLEX_JUSTIFY_CENTER,
            STYLE_GAP_S, STYLE_HEIGHT_150PX, STYLE_BOX_SHADOW,
            STYLE_BORDER_WIDTH_S, STYLE_BORDER_COLOR_GRAY, STYLE_BORDER_RADIUS_S, STYLE_BORDER_STYLE_SOLID);
        chartContainer.render();

        LinkedHashMap<MainLabel, Integer> sortedByValue =
            stageStats.entrySet().stream()
                .filter(e -> e.getKey().ordinal() <= MainLabel.DATA_EXPORT.ordinal())
                .sorted(Map.Entry.comparingByValue())   // ascending
                .collect(Collectors.collectingAndThen(
                    Collectors.toList(), list -> {
                        Collections.reverse(list);
                        return list.stream().collect(Collectors.toMap(
                            Map.Entry::getKey,
                            Map.Entry::getValue,
                            (a, b) -> a,
                            LinkedHashMap::new
                        ));
                    }
                ));

        sortedByValue.entrySet()
            .stream()
            .map(e -> createBar(e, sortedByValue.values().stream().max(Integer::compareTo).orElse(1)))
            .forEach(chartContainer::add);

        return chartContainer;
    }

    private Component createBar(Map.Entry<MainLabel, Integer> entry, int maxValue) {
        Div div = new Div();
        div.addClassNames(STYLE_HEIGHT_FULL, STYLE_FLEX_COLUMN, STYLE_FLEX_ALIGN_CENTER, STYLE_FLEX_JUSTIFY_END);

        Div bar = new Div();
        bar.getElement().setAttribute("data-tooltip", "%s: %s".formatted(getTranslation(entry.getKey().getTitleKey()), entry.getValue()));
        bar.addClassNames(STYLE_BORDER_RADIUS_S, "bar", STYLE_BOX_SHADOW);
        float percent = entry.getValue() * 90f / maxValue;
        bar.getStyle()
            .setHeight(percent + "%")
            .setWidth("22px")
            .setBackgroundColor(entry.getKey().getColor());

        Span label = new Span(entry.getValue().toString());
        label.addClassNames(STYLE_FONT_SIZE_XS);
        label.getStyle().set("margin-bottom", "2px"); // small gap above bar

        div.add(label);
        div.add(bar);

        return div;
    }

    public void setStageStats(Map<MainLabel, Integer> stageStats) {
        this.stageStats = stageStats;
    }
}
