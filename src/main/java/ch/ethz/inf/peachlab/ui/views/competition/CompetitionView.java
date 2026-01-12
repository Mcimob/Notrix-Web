package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.backend.service.CompetitionService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.components.OverviewBox;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import ch.ethz.inf.peachlab.ui.views.home.HomeView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

@Route(value = "competitions", layout = MainLayout.class)
public class CompetitionView extends AbstractView implements HasUrlParameter<String> {

    private final transient CompetitionService competitionService;

    private CompetitionEntity competition;

    public CompetitionView(CompetitionService competitionService) {
        this.competitionService = competitionService;
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        addClassNames(STYLE_FLEX_ROW);
    }

    @Override
    public void render() {
        removeAll();
        Div left = new Div("Transitions");
        left.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_CENTER, STYLE_BACKGROUND_WHITE);
        left.setWidth("20rem");

        Div center = new Div(createTitleBox(), createDescriptionBox(), createNotebookMatrix());
        center.addClassNames(STYLE_FLEX_COLUMN, STYLE_WIDTH_FULL, STYLE_GAP_M);
        center.getStyle().setMinWidth("0");

        Div right = new Div();
        right.addClassNames(STYLE_FLEX_COLUMN, STYLE_WIDTH_FULL);
        right.add(new Div("Right"));

        add(left, center, right);
    }

    private Component createTitleBox() {
        Div div = new Div();
        div.addClassNames(STYLE_BACKGROUND_WHITE, STYLE_WIDTH_FULL, STYLE_FLEX_ROW, STYLE_PADDING_S, STYLE_GAP_S);

        div.add(new H2(competition.getTitle()));

        Button navigationButton = new Button(
                VaadinIcon.EXTERNAL_LINK.create(), click ->
                UI.getCurrent().getPage().setLocation("https://kaggle.com/competitions/" + competition.getSlug()));
        navigationButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE);
        div.add(navigationButton);

        return div;
    }

    private Component createDescriptionBox() {
        Div div = new Div();
        div.addClassNames(STYLE_BACKGROUND_WHITE, STYLE_WIDTH_FULL, STYLE_FLEX_COLUMN, STYLE_GAP_S, STYLE_PADDING_S);

        div.add(new H2("Competition description"));

        OverviewBox box = new OverviewBox(competition.getOverview());
        box.render();
        div.add(box);

        return div;
    }

    private Component createNotebookMatrix() {
        NotebookMatrix matrix = new NotebookMatrix(competition);
        matrix.render();
        Div div = new Div(matrix);
        div.addClassNames(STYLE_PADDING_S, STYLE_BACKGROUND_WHITE, STYLE_HEIGHT_FULL);
        return div;
    }

    @Override
    public void setParameter(BeforeEvent beforeEvent, String slug) {
        CompetitionFilter filter = new CompetitionFilter();
        filter.setSlug(slug);
        ServiceResponse<CompetitionEntity> response = competitionService.fetchOne(filter);

        if (response.getEntity().isEmpty() || response.hasErrorMessages()) {
            response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
            UI.getCurrent().navigate(HomeView.class);
            return;
        }
        competition = response.getEntity().get();
    }
}
