package ch.ethz.inf.peachlab.ui.views.home;

import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.provider.CompetitionProvider;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.router.Route;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

@Route(value = "/", layout = MainLayout.class)
public class HomeView extends AbstractView {

    private final CompetitionDescriptionBox competitionDescriptionBox = new CompetitionDescriptionBox();
    private final CompetitionSearchBar searchbar = new CompetitionSearchBar();

    private final ConfigurableFilterDataProvider<CompetitionEntity, Void, CompetitionFilter> provider =
            new CompetitionProvider().withConfigurableFilter();
    private final CompetitionFilter filter = new CompetitionFilter();

    @Override
    public void render() {
        removeAll();

        CompetitionCloud cloud = new CompetitionCloud();
        cloud.addClassNames(STYLE_WIDTH_FULL, STYLE_HEIGHT_FULL);
        cloud.render();

        Div right = new Div(createDescriptionDiv(), createGrid());
        right.addClassNames(STYLE_FLEX_COLUMN, STYLE_BACKGROUND_WHITE, STYLE_WIDTH_FULL, STYLE_HEIGHT_FULL);

        Div bottom = new Div(cloud, right);
        bottom.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_M, STYLE_WIDTH_FULL, STYLE_HEIGHT_FULL);

        add(createSearchBar(), bottom);
    }

    private Component createSearchBar() {
        searchbar.addValueChangeListener(change -> {
            filter.setSearchString(change.getValue());
            provider.refreshAll();
        });
        return searchbar;
    }

    private Component createDescriptionDiv() {
        competitionDescriptionBox.render();
        return competitionDescriptionBox;
    }

    private Component createGrid() {
        Grid<CompetitionEntity> grid = new Grid<>();
        grid.addColumn(CompetitionEntity::getTitle)
                .setHeader("Competition Title")
                .setSortable(true)
                .setSortProperty("title");
        grid.addColumn(CompetitionEntity::getTotalSubmissions)
                .setHeader("# Submissions")
                .setSortable(true)
                .setSortProperty("totalSubmissions");
        grid.addColumn(CompetitionEntity::getDeadlineDate)
                .setHeader("Deadline Date")
                .setSortable(true)
                .setSortProperty("deadlineDate");

        grid.addSelectionListener(event -> {
            competitionDescriptionBox.setCompetition(event.getFirstSelectedItem().orElse(null));
            competitionDescriptionBox.render();
        });

        provider.setFilter(filter);
        grid.setDataProvider(provider);

        grid.setHeightFull();

        return grid;
    }
}
