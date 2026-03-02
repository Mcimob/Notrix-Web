package ch.ethz.inf.peachlab.ui.views.home;

import ch.ethz.inf.peachlab.backend.broadcaster.ProcessedNotebookBroadcaster;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingService;
import ch.ethz.inf.peachlab.model.dto.ProcessingCompetition;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.provider.CompetitionProvider;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import ch.ethz.inf.peachlab.ui.views.competition.CompetitionView;
import ch.ethz.inf.peachlab.ui.webstorage.ManagesProcessingNotebooks;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import org.springframework.data.util.Pair;

import java.io.Serial;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_LINK;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

@Route(value = "/", layout = MainLayout.class)
public class HomeView extends AbstractView implements ManagesProcessingNotebooks {

    @Serial
    private static final long serialVersionUID = 8253760301668795920L;

    private final transient NotebookProcessingService processingService;

    private final CompetitionDescriptionBox competitionDescriptionBox = new CompetitionDescriptionBox();
    private final CompetitionSearchBar searchbar = new CompetitionSearchBar();

    private final ConfigurableFilterDataProvider<CompetitionEntity, Void, CompetitionFilter> provider =
            new CompetitionProvider().withConfigurableFilter();
    private final CompetitionFilter filter = new CompetitionFilter();

    public HomeView(NotebookProcessingService processingService) {
        this.processingService = processingService;
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        addClassNames(STYLE_FLEX_COLUMN);
    }

    @Override
    public void render() {
        removeAll();

        CompetitionCloud cloud = new CompetitionCloud();
        cloud.addClassNames(STYLE_WIDTH_FULL, STYLE_HEIGHT_FULL);
        cloud.render();

        Div right = new Div(createDescriptionDiv(), createUpload(), createGrid());
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

    private Component createUpload() {
        InMemoryUploadHandler handler = UploadHandler
            .inMemory((metadata, data) -> {
                String filename = metadata.fileName().split("\\.")[0];
                ServiceResponse<Pair<String, String>> processingResponse =
                    processingService.startCompetitionProcessing(metadata, data);
                processingResponse.getErrorMessages().forEach(this::showErrorNotification);
                processingResponse.getEntity().ifPresent(result -> {
                    ProcessedNotebookBroadcaster.registerCompetitionListener(this::onCompetitionProcessingDone, result.getFirst(), UI.getCurrent());
                    getProcessingCompetitions(processingComps -> {
                        processingComps.put(result.getFirst(),
                            new ProcessingCompetition(filename, result.getSecond()));
                        setProcessingCompetitions(processingComps);
                        showSuccessNotification("Your competition {0} was sent to processing. You can check its status on the 'Saved' page and will be notified, when processing completes", filename);
                    });
                });
            });
        Upload upload = new Upload(handler);
        upload.addFileRejectedListener(e -> showErrorNotification(e.getErrorMessage()));
        upload.setDropAllowed(true);
        upload.setMaxFiles(1);
        UploadI18N i18n = new UploadI18N();
        i18n.setAddFiles(new UploadI18N.AddFiles()
            .setOne("Upload own solution set..."));
        i18n.setDropFiles(new UploadI18N.DropFiles()
            .setOne("Drop zip here"));


        Div div = new Div(upload);
        div.addClassNames(STYLE_PADDING_M, STYLE_BACKGROUND_WHITE);
        return div;
    }

    private Component createGrid() {
        Grid<CompetitionEntity> grid = new Grid<>();
        grid.addComponentColumn(this::createTitleLink)
            .setHeader("Competition Title")
            .setSortable(true)
            .setSortProperty("title");
        grid.addColumn(CompetitionEntity::getTotalSubmissions)
            .setHeader("# Submissions")
            .setSortable(true)
            .setSortProperty("totalSubmissions");
        grid.addColumn(c -> "%.2f".formatted(c.getAvgLinesPerKernel()))
                .setHeader("Avg Notebook Lines")
            .setSortable(true)
            .setSortProperty("avgLinesPerKernel");
        grid.addColumn(c -> c.getDeadlineDate().toLocalDate())
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

    private Component createTitleLink(CompetitionEntity competition) {
        Span span = new Span(competition.getTitle());
        span.addClassNames(STYLE_TEXT_LINK);
        span.addClickListener(e -> UI.getCurrent().navigate(
            CompetitionView.class,
            competition.getSlug()));

        return span;
    }

    private Component createCompetitionNavigation(CompetitionEntity competition) {
        Button button = new Button(VaadinIcon.ENTER_ARROW.create());
        button.addThemeVariants(ButtonVariant.LUMO_TERTIARY_INLINE, ButtonVariant.LUMO_ICON);
        button.addClickListener(e -> UI.getCurrent().navigate(
                CompetitionView.class,
                competition.getSlug()));

        return button;
    }
}
