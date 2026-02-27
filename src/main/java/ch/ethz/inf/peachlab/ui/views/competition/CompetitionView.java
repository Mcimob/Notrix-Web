package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.backend.ProcessedNotebookBroadcaster;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.ClusterService;
import ch.ethz.inf.peachlab.backend.service.db.CompetitionService;
import ch.ethz.inf.peachlab.backend.service.db.KernelService;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingService;
import ch.ethz.inf.peachlab.model.dto.ClusterDTO;
import ch.ethz.inf.peachlab.model.dto.KernelDTO;
import ch.ethz.inf.peachlab.model.dto.ProcessingNotebook;
import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.HasBaseStats;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.filter.ClusterFilter;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.model.filter.UploadedKernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.ClusterLoadType;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import ch.ethz.inf.peachlab.model.loadtype.UploadedKernelLoadType;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.UiAsyncUtils;
import ch.ethz.inf.peachlab.ui.components.ComponentWithLink;
import ch.ethz.inf.peachlab.ui.components.DivWithTooltip;
import ch.ethz.inf.peachlab.ui.components.OverviewBox;
import ch.ethz.inf.peachlab.ui.components.TitleLink;
import ch.ethz.inf.peachlab.ui.components.sidebar.TransitionSidebar;
import ch.ethz.inf.peachlab.ui.provider.ClusterProvider;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.ClusterClickEvent;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.ClusterMatrix;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.Filterbar;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.KernelClickEvent;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.NotebookMatrix;
import ch.ethz.inf.peachlab.ui.views.home.HomeView;
import ch.ethz.inf.peachlab.ui.views.kernel.KernelView;
import ch.ethz.inf.peachlab.ui.webstorage.ManagesProcessingNotebooks;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.stream.Stream;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_MIN_HEIGHT_0;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

@Route(value = "competitions", layout = MainLayout.class)
public class CompetitionView extends AbstractView implements HasUrlParameter<String>, ManagesProcessingNotebooks {

    @Serial
    private static final long serialVersionUID = 8520612132652236410L;

    private final transient CompetitionService competitionService;
    private final transient KernelService kernelService;
    private final transient UploadedKernelService uploadedKernelService;
    private final transient ClusterService clusterService;
    private final transient NotebookProcessingService nbProcessingService;

    private CompetitionEntity competition;
    private byte[] uploadedData;

    private final H2 title = new H2();
    private final Div competitionOverview = new Div();
    private final ClusterOverview clusterOverview = new ClusterOverview();

    private final NotebookMatrix matrix = new NotebookMatrix();
    private final ClusterMatrix cLusterMatrix = new ClusterMatrix();
    private final Grid<HasKernelData<?, ?>> grid = new Grid<>();
    private final TreeGrid<HasBaseStats> clusterGrid = new TreeGrid<>();
    private final KernelFilter kernelFilter = new KernelFilter();
    private final ClusterFilter clusterFilter = new ClusterFilter();
    private final ConfigurableFilterDataProvider<ClusterEntity, Void, ClusterFilter> clusterProvider =
        new ClusterProvider().withConfigurableFilter();

    private final List<UploadedKernelEntity> uploadedKernels = new ArrayList<>();

    public CompetitionView(CompetitionService competitionService,
                           KernelService kernelService, UploadedKernelService uploadedKernelService,
                           ClusterService clusterService,
                           NotebookProcessingService nbProcessingService) {
        this.competitionService = competitionService;
        this.kernelService = kernelService;
        this.uploadedKernelService = uploadedKernelService;
        this.clusterService = clusterService;
        this.nbProcessingService = nbProcessingService;
    }

    private void initProvider() {
        kernelFilter.setCompetition(competition);

        clusterFilter.setCompetition(competition);
        clusterProvider.setFilter(clusterFilter);
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        addClassNames(STYLE_FLEX_ROW);
    }

    @Override
    public void render() {
        removeAll();

        Div center = new Div(createTitleBox(), createClusterOverview(), createDescriptionBox(), createNotebookMatrix());
        center.addClassNames(STYLE_FLEX_COLUMN, STYLE_WIDTH_FULL, STYLE_GAP_M);
        center.getStyle().setMinWidth("0");

        Div right = new Div(createStats(), createUpload(), createGrids());
        right.addClassNames(STYLE_FLEX_COLUMN, STYLE_WIDTH_FULL, STYLE_GAP_M);

        add(createSidebar(), center, right);

        initData();
    }

    private void initData() {
        getUploadedNotebooks(nbs -> {
            UploadedKernelFilter uploadedKernelFilter = new UploadedKernelFilter();
            uploadedKernelFilter.setIds(nbs.get(competition.getId()));

            UiAsyncUtils.<PageImpl<? extends HasKernelData<?, ?>>>callServicesAsync(
                List.of(
                    () -> uploadedKernelService.fetch(Pageable.unpaged(), uploadedKernelFilter, UploadedKernelLoadType.WITH_CELLS),
                    () -> kernelService.fetch(Pageable.unpaged(), kernelFilter, KernelLoadType.WITH_CELLS)
                ),
                UI.getCurrent(),
                this::onKernelData
            );
        });
    }

    private Component createSidebar() {
        TransitionSidebar sidebar = new TransitionSidebar();
        sidebar.setStageFrequencies(competition.getMainLabelStats());
        sidebar.setTransitionMatrix(competition.getTransitionMatrix());
        sidebar.setOpacityTargets(new String[]{"notebook-matrix .cell", "cluster-matrix .cell"});
        sidebar.setWidth("50%");
        sidebar.render();

        return sidebar;
    }

    private Component createTitleBox() {
        title.setText(competition.getTitle());
        Div div = new Div(new ComponentWithLink(
            title,
            "https://kaggle.com/competitions/" + competition.getSlug()
        ));
        div.addClassNames(STYLE_BACKGROUND_WHITE, STYLE_WIDTH_FULL, STYLE_PADDING_M);

        return div;
    }

    private Component createDescriptionBox() {
        competitionOverview.addClassNames(STYLE_BACKGROUND_WHITE, STYLE_WIDTH_FULL, STYLE_FLEX_COLUMN, STYLE_GAP_S, STYLE_PADDING_M);
        competitionOverview.add(new H2("Competition description"));

        OverviewBox box = new OverviewBox(competition.getOverview());
        box.render();
        competitionOverview.add(box);

        return competitionOverview;
    }

    private Component createClusterOverview() {
        clusterOverview.setVisible(false);
        return clusterOverview;
    }

    private Component createNotebookMatrix() {
        matrix.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);
        matrix.addKernelClickedListener(this::onKernelClicked);

        cLusterMatrix.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);
        cLusterMatrix.addKernelClickedListener(this::onKernelClicked);
        cLusterMatrix.addClusterClickedListener(this::onClusterClicked);
        cLusterMatrix.setVisible(false);
        UiAsyncUtils.callServiceAsync(
            () -> clusterService.fetch(Pageable.unpaged(Sort.by("LocalClusterId")), clusterFilter, ClusterLoadType.WITH_KERNELS_AND_CELLS),
            UI.getCurrent(),
            this::onNewClusterMatrixData
        );

        Filterbar bar = new Filterbar();
        bar.render();
        bar.addMarkdownButtonListener(event -> {
            cLusterMatrix.getStyle().set("--display-md", event.getShow() ? "block" : "none");
            matrix.getStyle().set("--display-md", event.getShow() ? "block" : "none");
        });
        bar.addHeightButtonListener(event -> {
            if (event.getShow()) {
                matrix.getStyle().set("--cell-height", "initial");
                cLusterMatrix.getStyle().set("--cell-height", "initial");
            } else {
                matrix.getStyle().set("--cell-height", "5px");
                cLusterMatrix.getStyle().set("--cell-height", "5px");
            }
        });
        bar.addClusterListener(event -> {
            grid.setVisible(!event.isCluster());
            matrix.setVisible(!event.isCluster());
            cLusterMatrix.setVisible(event.isCluster());
            clusterGrid.setVisible(event.isCluster());
        });

        DivWithTooltip div = new DivWithTooltip(".cell");
        div.addClassNames(STYLE_PADDING_S, STYLE_BACKGROUND_WHITE, STYLE_HEIGHT_FULL, STYLE_MIN_HEIGHT_0,
                STYLE_FLEX_COLUMN, STYLE_GAP_S);
        div.render();
        div.add(bar);
        div.add(matrix, cLusterMatrix);
        return div;
    }

    private void onKernelClicked(KernelClickEvent e) {
        String stringId = e.getKernelId();
        ServiceResponse<? extends HasKernelData<?, ?>> response;
        try {
            long longId = Long.parseLong(stringId);
            response = kernelService.fetchById(longId);
        } catch (NumberFormatException ex) {
            response = uploadedKernelService.fetchById(stringId);
        }
        if (response.hasErrorMessages() || response.getEntity().isEmpty()) {
            response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
            return;
        }
        HasKernelData<?, ?> kernel = response.getEntity().get();
        UI.getCurrent().navigate(KernelView.class, kernel.getUrlParameter());
    }

    private void onClusterClicked(ClusterClickEvent event) {
        Long localClusterId = event.getLocalClusterId();
        if (localClusterId == -1) {
            clusterOverview.setVisible(false);
            competitionOverview.setVisible(true);
            title.setText(competition.getTitle());
            return;
        }
        ClusterFilter filter = new ClusterFilter();
        filter.setCompetition(competition);
        filter.setLocalClusterId(localClusterId);
        UiAsyncUtils.callServiceAsync(() -> clusterService.fetch(Pageable.unpaged(), filter),
            UI.getCurrent(),
            this::onClusterResponse);
    }

    private void onClusterResponse(ServiceResponse<PageImpl<ClusterEntity>> response) {
        response.getEntity()
            .map(PageImpl::toList)
            .map(List::getFirst)
            .ifPresent(c -> {
            competitionOverview.setVisible(false);
            clusterOverview.setCluster(c);
            clusterOverview.render();
            clusterOverview.setVisible(true);
            clusterGrid.expand(c);
            clusterGrid.scrollToIndex(c.getLocalClusterId().intValue() - 1, 0);
            title.setText("Cluster " + c.getLocalClusterId());
        });
    }

    private void onKernelData(List<? extends ServiceResponse<? extends PageImpl<? extends HasKernelData<?, ?>>>> responses) {
        List<HasKernelData<?, ?>> kernels = responses.stream()
            .map(ServiceResponse::getEntity)
            .flatMap(Optional::stream)
            .flatMap(PageImpl::stream)
            .map(o -> (HasKernelData<?, ?>) o)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        matrix.setItems(kernels.stream()
                .map(KernelDTO::ofKernel)
                .toList());
        grid.setItems(kernels);
    }

    private void onNewClusterMatrixData(ServiceResponse<PageImpl<ClusterEntity>> response) {
        cLusterMatrix.setItems(
            response.getEntity()
                .map(PageImpl::stream)
                .orElse(Stream.empty())
                .map(ClusterDTO::ofCluster)
                .toList()
        );
        response.getEntity()
            .map(PageImpl::stream)
            .ifPresent(list -> clusterGrid.setItems(list.map(o -> (HasBaseStats) o).toList(), HasBaseStats::getChildren));
    }

    private Component createStats() {
        CompetitionStatsPanel stats = new CompetitionStatsPanel(competition);
        stats.render();
        return stats;
    }

    private Component createUpload() {
        Dialog titleDialog = new Dialog();
        titleDialog.setHeaderTitle("Upload own solution");
        TextField titleField = new TextField("Title");
        titleDialog.add(new Div("Please give your submission a title"), titleField);

        Dialog.DialogFooter footer = titleDialog.getFooter();
        Button cancel = new Button("Cancel", e -> titleDialog.close());
        cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        Button confirm = new Button("Confirm", e -> {
            titleDialog.close();
            ServiceResponse<String> response = nbProcessingService.startNotebookProcessing(uploadedData);
            response.getErrorMessages().forEach(this::showErrorNotification);
            response.getEntity().ifPresent(identifier -> {
                ProcessedNotebookBroadcaster.register(this::onNotebooksProcessingDone, identifier, UI.getCurrent());
                getProcessingNotebooks(processingNotebooks -> {
                    processingNotebooks.put(identifier,
                        new ProcessingNotebook(titleField.getValue(), competition.getId()));
                    setProcessingNotebooks(processingNotebooks);
                    showSuccessNotification("Your notebook {0} was sent to processing. You can check its status on the 'Saved' page and will be notified, when processing completes", titleField.getValue());
                });
            });
        });
        footer.add(cancel, confirm);

        InMemoryUploadHandler inMemoryHandler = UploadHandler
            .inMemory((metadata, data) -> {
                this.uploadedData = data;
                titleDialog.open();
            });
        Upload upload = new Upload(inMemoryHandler);

        upload.setAcceptedFileTypes("application/json", ".ipynb");
        upload.addFileRejectedListener(event ->
            showErrorNotification(event.getErrorMessage()));
        upload.setDropAllowed(true);
        upload.setMaxFiles(1);
        UploadI18N i18n = new UploadI18N();
        i18n.setAddFiles(new UploadI18N.AddFiles()
            .setOne("Upload own Solution..."));
        i18n.setDropFiles(new UploadI18N.DropFiles()
            .setOne("Drop Notebook here"));
        i18n.setError(new UploadI18N.Error()
            .setIncorrectFileType("The provided file does not have the correct format (Python notebook)"));

        upload.setI18n(i18n);

        Div div = new Div(upload);
        div.addClassNames(STYLE_PADDING_M, STYLE_BACKGROUND_WHITE);
        return div;
    }

    private Component createGrids() {
        Div div = new Div(createKernelGrid(), createClusterGrid());
        div.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);

        return div;
    }

    private Component createKernelGrid() {
        grid.setId("kernel-grid"); // unique DOM id

        grid.addComponentColumn(TitleLink::new)
                .setHeader("Title")
                .setSortable(true)
                .setComparator(Comparator.comparing(HasKernelData::getTitle))
                .setKey("title")
                .setFlexGrow(1);
        grid.addColumn(HasKernelData::getTotalVotes)
                .setHeader("# Votes")
                .setSortable(true)
                .setSortProperty("totalVotes")
                .setKey("totalVotes")
                .setFlexGrow(0);
        grid.addColumn(HasKernelData::getTotalViews)
                .setHeader("# Views")
                .setSortable(true)
                .setSortProperty("totalViews")
                .setKey("totalViews")
                .setFlexGrow(0);
        grid.addColumn(HasKernelData::getCellCount)
                .setHeader("# Cells")
                .setSortable(true)
                .setSortProperty("cellCount")
                .setKey("cellCount")
                .setFlexGrow(0);
        grid.addColumn(HasKernelData::getNumLines)
                .setHeader("# Lines")
                .setSortable(true)
                .setSortProperty("numLines")
                .setKey("numLines")
                .setFlexGrow(0);

        grid.setPartNameGenerator(k -> k instanceof UploadedKernelEntity ? "uploaded" : "");

        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        kernelFilter.setCompetition(competition);

        grid.setEmptyStateText("Loading Notebooks...");

        grid.addSortListener(sort -> {
            matrix.setItems(grid.getListDataView().getItems().map(KernelDTO::ofKernel).toList());
        });

        return grid;
    }

    private Component createClusterGrid() {
        clusterGrid.addComponentHierarchyColumn(this::createTitleElement)
                .setHeader("Title")
            .setFlexGrow(1);
        clusterGrid.addColumn(k -> "%.2f".formatted(k.getVotes()))
            .setHeader("# Votes")
            .setFlexGrow(0);
        clusterGrid.addColumn(k -> "%.2f".formatted(k.getNumCells()))
            .setHeader("# Cells")
            .setFlexGrow(0);
        clusterGrid.addColumn(k -> "%.2f".formatted(k.getLines()))
            .setHeader("# Lines")
            .setFlexGrow(0);
        clusterGrid.setHeightFull();

        clusterGrid.setVisible(false);
        return clusterGrid;
    }

    private Component createTitleElement(HasBaseStats kernelData) {
        if (kernelData instanceof KernelEntity kernel) {
            return new TitleLink(kernel);
        } else if (kernelData instanceof ClusterEntity cluster) {
            return new Text("Cluster " + cluster.getLocalClusterId());
        }
        return new Div();
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
        initProvider();
    }
}
