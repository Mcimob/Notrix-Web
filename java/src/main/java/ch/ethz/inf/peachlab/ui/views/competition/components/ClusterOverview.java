package ch.ethz.inf.peachlab.ui.views.competition.components;

import ch.ethz.inf.peachlab.model.entity.HasClusterData;
import ch.ethz.inf.peachlab.ui.HasRender;
import ch.ethz.inf.peachlab.ui.components.OverviewBox;
import ch.ethz.inf.peachlab.ui.components.StageChart;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_MIN_HEIGHT_0;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;

public class ClusterOverview extends Div implements HasRender {

    @Serial
    private static final long serialVersionUID = 2755752662617643984L;
    private HasClusterData<?, ?> cluster;

    public ClusterOverview() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_COLUMN, STYLE_GAP_S, STYLE_PADDING_M, STYLE_BACKGROUND_WHITE, STYLE_MIN_HEIGHT_0, STYLE_HEIGHT_FULL);
    }

    @Override
    public void render() {
        removeAll();
        if (cluster == null) {
            OverviewBox box = new OverviewBox("Select cluster to show description");
            box.addClassNames(STYLE_HEIGHT_FULL);
            box.render();
            add(box);
            return;
        }
        add(createStats(), createDescription());
    }

    private Component createDescription() {
        String clusterSummary = cluster.getSummary();
        if (StringUtils.isBlank(clusterSummary)) {
            clusterSummary = "No summary was generated for this cluster";
        }

        OverviewBox description = new OverviewBox(clusterSummary);
        description.addClassNames(STYLE_HEIGHT_FULL);
        description.render();

        return description;
    }

    private Component createStats() {
        StageChart chart = new StageChart();
        chart.setStageStats(cluster.getMainLabelStats());
        chart.render();

        return chart;
    }

    public void setCluster(HasClusterData<?, ?> cluster) {
        this.cluster = cluster;
    }
}
