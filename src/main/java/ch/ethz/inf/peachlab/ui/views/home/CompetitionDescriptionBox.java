package ch.ethz.inf.peachlab.ui.views.home;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.orderedlayout.Scroller;

import java.util.Optional;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_COLOR_GRAY;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_RADIUS_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_STYLE_DASHED;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_WIDTH_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_COLOR_GRAY;

public class CompetitionDescriptionBox extends Div implements HasRender {

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

    private Component createDescription() {
        Markdown md = new Markdown(Optional.ofNullable(competition).map(CompetitionEntity::getOverview).orElse(""));

        Scroller scroller = new Scroller(md);
        scroller.setHeight("20rem");
        scroller.addClassNames(STYLE_BORDER_RADIUS_S,
                STYLE_BORDER_WIDTH_S, STYLE_BORDER_STYLE_DASHED, STYLE_BORDER_COLOR_GRAY,
                STYLE_PADDING_S);
        return scroller;
    }

    @Override
    public void render() {
        removeAll();
        add(new Div(createTitle(), createSubtitle()));
        add(createDescription());
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
    }
}
