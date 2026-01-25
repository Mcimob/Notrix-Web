package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.backend.service.KernelService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.ui.HasRender;
import ch.ethz.inf.peachlab.ui.components.StageChart;
import ch.ethz.inf.peachlab.ui.components.TextWithIcon;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import org.springframework.data.util.Pair;

import java.util.List;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_COLOR_GRAY;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_RADIUS_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_STYLE_SOLID;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_WIDTH_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_END;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_BETWEEN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_JUSTIFY_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_150PX;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_WRAP_NO;

public class CompetitionStatsPanel extends Div implements HasRender {

    public static final String DECIMAL_FORMAT = "%.2f";
    private final CompetitionEntity competition;

    public CompetitionStatsPanel(CompetitionEntity competition) {
        this.competition = competition;
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_ROW, STYLE_GAP_M, STYLE_BACKGROUND_WHITE, STYLE_PADDING_M, STYLE_FLEX_ALIGN_END);
    }

    private Component createNumStats() {
        KernelFilter filter = new KernelFilter();
        filter.setCompetition(competition);
        ServiceResponse<Long> countResponse = SpringContext.getBean(KernelService.class).count(filter);

        Div div = new Div();
        div.addClassNames(STYLE_FLEX_COLUMN, STYLE_GAP_S);

        div.add(new TextWithIcon(VaadinIcon.DATABASE.create(), "Stats"));

        Div container = new Div();
        container.addClassNames(STYLE_FLEX_COLUMN, STYLE_FLEX_JUSTIFY_CENTER, STYLE_GAP_M,
                STYLE_PADDING_M, STYLE_HEIGHT_150PX,
                STYLE_BORDER_WIDTH_S, STYLE_BORDER_COLOR_GRAY, STYLE_BORDER_RADIUS_S, STYLE_BORDER_STYLE_SOLID);

        List<Pair<String, String>> stats = List.of(
                Pair.of("Total Notebooks", countResponse.getEntity().orElse(0L).toString()),
                Pair.of("Avg. Cells", String.format (DECIMAL_FORMAT, competition.getAvgCellsPerKernel())),
                Pair.of("Avg. Votes", String.format (DECIMAL_FORMAT, competition.getAvgVotes()))
        );
        stats.stream()
                .map(p -> {
                    Div row = new Div(new Span(p.getFirst()), new Span(p.getSecond()));
                    row.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN, STYLE_GAP_M, STYLE_TEXT_WRAP_NO);
                    return row;
                }).forEach(container::add);
        div.add(container);

        return div;
    }

    private Component createChart() {
        StageChart chart = new StageChart();
        chart.setStageStats(competition.getMainLabelStats());
        chart.render();

        return chart;
    }




    @Override
    public void render() {
        add(createNumStats(), createChart());
    }
}
