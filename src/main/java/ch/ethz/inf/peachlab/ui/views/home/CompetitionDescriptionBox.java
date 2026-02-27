package ch.ethz.inf.peachlab.ui.views.home;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.ui.HasRender;
import ch.ethz.inf.peachlab.ui.components.OverviewBox;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;

import java.io.Serial;
import java.util.Optional;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_COLOR_GRAY;

public class CompetitionDescriptionBox extends Div implements HasRender {

    @Serial
    private static final long serialVersionUID = 1483883066371802213L;
    private CompetitionEntity competition;

    public CompetitionDescriptionBox() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_PADDING_S, STYLE_FLEX_COLUMN, STYLE_GAP_S);
    }

    private Component createTitle() {
        H2 title = new H2(Optional.ofNullable(competition).map(CompetitionEntity::getTitle).orElse("Competition Title"));
        if (competition == null) {
            title.addClassName(STYLE_TEXT_COLOR_GRAY);
        }
        return title;
    }

    private Component createSubtitle() {
        Span span = new Span(Optional.ofNullable(competition).map(CompetitionEntity::getSubtitle).orElse("Subtitle"));
        span.addClassName(STYLE_TEXT_COLOR_GRAY);
        return span;
    }

    @Override
    public void render() {
        removeAll();
        add(new Div(createTitle(), createSubtitle()));
        OverviewBox overviewBox = new OverviewBox(Optional.ofNullable(competition).map(CompetitionEntity::getOverview).orElse(""));
        overviewBox.render();
        add(overviewBox);
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
    }
}
