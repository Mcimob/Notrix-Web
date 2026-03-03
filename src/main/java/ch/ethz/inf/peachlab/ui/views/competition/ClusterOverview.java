package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.model.entity.HasClusterData;
import ch.ethz.inf.peachlab.ui.HasRender;
import ch.ethz.inf.peachlab.ui.components.IconLabelContainer;
import ch.ethz.inf.peachlab.ui.components.StageChart;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_MIN_HEIGHT_150PX;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;

public class ClusterOverview extends Div implements HasRender {

    @Serial
    private static final long serialVersionUID = 2755752662617643984L;
    private HasClusterData<?, ?> cluster;

    public ClusterOverview() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_PADDING_M, STYLE_BACKGROUND_WHITE);
    }

    @Override
    public void render() {
        removeAll();
        if (cluster == null) {
            return;
        }
        add(createDescription(), createStats());
    }

    private Component createDescription() {
        IconLabelContainer container = new IconLabelContainer();
        container.setIcon(VaadinIcon.FILE.create());
        container.setTitleText("Summary");
        container.render();
        container.addToContainer(cluster.getSummary());
        container.addClassNamesToContainer(STYLE_MIN_HEIGHT_150PX);

        return container;
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
