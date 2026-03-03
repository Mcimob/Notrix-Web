package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.BaseService;
import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import ch.ethz.inf.peachlab.model.filter.AbstractKernelFilter;
import ch.ethz.inf.peachlab.ui.HasRender;
import ch.ethz.inf.peachlab.ui.components.StageChart;
import ch.ethz.inf.peachlab.ui.components.TripleStats;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import org.springframework.data.util.Pair;

import java.io.Serial;
import java.util.List;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_END;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;

public class CompetitionStatsPanel<T extends HasCompetitionData<?, ?, ?>, F extends AbstractKernelFilter<?, ?, T>> extends Div implements HasRender {

    public static final String DECIMAL_FORMAT = "%.2f";
    @Serial
    private static final long serialVersionUID = 5662163264707841558L;

    private final T competition;
    private final F filter;
    private final BaseService<?, F, ?> kernelService;

    public CompetitionStatsPanel(T competition, F filter, BaseService<?, F, ?> kernelService) {
        this.competition = competition;
        this.filter = filter;
        this.kernelService = kernelService;
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_ROW, STYLE_GAP_M, STYLE_BACKGROUND_WHITE, STYLE_PADDING_M, STYLE_FLEX_ALIGN_END);
    }

    private Component createNumStats() {
        ServiceResponse<Long> countResponse = kernelService.count(filter);;

        TripleStats stats = new TripleStats();
        stats.setStats(List.of(
            Pair.of("Total Notebooks", countResponse.getEntity().orElse(0L).toString()),
            Pair.of("Avg. Cells", String.format (DECIMAL_FORMAT, competition.getAvgCellsPerKernel())),
            Pair.of("Avg. Votes", String.format (DECIMAL_FORMAT, competition.getAvgVotes()))
        ));
        stats.render();

        return stats;
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
