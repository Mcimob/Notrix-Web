package ch.ethz.inf.peachlab.ui.views.home;

import ch.ethz.inf.peachlab.backend.broadcaster.ProcessedNotebookBroadcaster;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.CompetitionService;
import ch.ethz.inf.peachlab.backend.service.db.UploadedCompetitionService;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingService;
import ch.ethz.inf.peachlab.model.dto.ProcessingCompetition;
import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import ch.ethz.inf.peachlab.model.entity.UploadedCompetitionEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import ch.ethz.inf.peachlab.model.filter.UploadedCompetitionFilter;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.UiAsyncUtils;
import ch.ethz.inf.peachlab.ui.components.TitleLink;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import ch.ethz.inf.peachlab.ui.webstorage.ManagesProcessingNotebooks;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;

import java.io.Serial;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

@Route(value = "/", layout = MainLayout.class)
public class HomeView extends AbstractView implements ManagesProcessingNotebooks {

    @Serial
    private static final long serialVersionUID = 8253760301668795920L;

    private final transient NotebookProcessingService processingService;
    private final transient CompetitionService competitionService;
    private final transient UploadedCompetitionService uploadedCompetitionService;

    private final CompetitionDescriptionBox competitionDescriptionBox = new CompetitionDescriptionBox();
    private final CompetitionSearchBar searchbar = new CompetitionSearchBar();

    private final Grid<HasCompetitionData<?, ?, ?>> grid = new Grid<>();

    private final CompetitionFilter filter = new CompetitionFilter();
    private final UploadedCompetitionFilter uploadedFilter = new UploadedCompetitionFilter();

    public HomeView(NotebookProcessingService processingService, CompetitionService competitionService, UploadedCompetitionService uploadedCompetitionService) {
        this.processingService = processingService;
        this.competitionService = competitionService;
        this.uploadedCompetitionService = uploadedCompetitionService;
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

        initData();
    }

    private void initData() {
        getUploadedCompetitions(comps -> {
            uploadedFilter.setIds(comps);

            UiAsyncUtils.<PageImpl<? extends HasCompetitionData<?, ?, ?>>>callServicesAsync(
                List.of(
                    () -> uploadedCompetitionService.fetch(Pageable.unpaged(), uploadedFilter),
                    () -> competitionService.fetch(Pageable.unpaged(), filter)
                ),
                UI.getCurrent(),
                this::onCompetitionData
            );
        });
    }

    private void onCompetitionData(List<? extends ServiceResponse<? extends PageImpl<? extends HasCompetitionData<?, ?, ?>>>> responses) {
        List<HasCompetitionData<?, ?, ?>> competitions = responses.stream()
            .map(ServiceResponse::getEntity)
            .flatMap(Optional::stream)
            .flatMap(PageImpl::stream)
            .map(o -> (HasCompetitionData<?, ?, ?>) o)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        grid.setItems(competitions);
        grid.getDataProvider().refreshAll();
    }

    private Component createSearchBar() {
        searchbar.addValueChangeListener(change -> {
            filter.setSearchString(change.getValue());
            uploadedFilter.setSearchString(change.getValue());
            initData();
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
        upload.setI18n(i18n);

        Div div = new Div(upload);
        div.addClassNames(STYLE_PADDING_M, STYLE_BACKGROUND_WHITE);
        return div;
    }

    private Component createGrid() {
        grid.addComponentColumn(TitleLink::ofCompetition)
            .setHeader("Competition Title")
            .setSortable(true)
            .setComparator(Comparator.comparing(HasCompetitionData::getTitle));
        grid.addColumn(HasCompetitionData::getTotalSubmissions)
            .setHeader("# Submissions")
            .setSortable(true);
        grid.addColumn(c -> "%.2f".formatted(c.getAvgLinesPerKernel()))
            .setHeader("Avg Notebook Lines")
            .setSortable(true)
            .setComparator(Comparator.comparing(HasCompetitionData::getAvgLinesPerKernel));
        grid.addColumn(c -> Optional.ofNullable(c)
                .map(HasCompetitionData::getDeadlineDate)
                .map(LocalDateTime::toLocalDate)
                .orElse(LocalDate.now()))
            .setHeader("Deadline Date")
            .setSortable(true);

        grid.addSelectionListener(event -> {
            competitionDescriptionBox.setCompetition(event.getFirstSelectedItem().orElse(null));
            competitionDescriptionBox.render();
        });

        grid.setPartNameGenerator(c -> c
            instanceof UploadedCompetitionEntity
                ? "uploaded"
                : "");

        grid.setHeightFull();
        grid.setEmptyStateText("Loading competitions...");

        return grid;
    }
}
