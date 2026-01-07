package ch.ethz.inf.peachlab.ui.views.home;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextArea;

import java.util.Optional;

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

    private Component createDescription() {
        TextArea description = new TextArea();
        description.setValue(Optional.ofNullable(competition).map(CompetitionEntity::getSubtitle).orElse(""));
        description.setLabel("Description");
        description.setReadOnly(true);

        return description;
    }

    @Override
    public void render() {
        removeAll();
        add(createTitle());
        add(createDescription());
    }

    public void setCompetition(CompetitionEntity competition) {
        this.competition = competition;
    }
}
