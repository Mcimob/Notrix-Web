package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.BaseService;
import ch.ethz.inf.peachlab.model.dto.ClusterDTO;
import ch.ethz.inf.peachlab.model.dto.KernelDTO;
import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.entity.HasBaseStats;
import ch.ethz.inf.peachlab.model.entity.HasClusterData;
import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.filter.AbstractClusterFilter;
import ch.ethz.inf.peachlab.model.filter.AbstractCompetitionFilter;
import ch.ethz.inf.peachlab.model.filter.AbstractKernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.ClusterLoadType;
import ch.ethz.inf.peachlab.model.loadtype.HasLoadType;
import ch.ethz.inf.peachlab.ui.UiAsyncUtils;
import ch.ethz.inf.peachlab.ui.components.ComponentWithLink;
import ch.ethz.inf.peachlab.ui.components.DivWithTooltip;
import ch.ethz.inf.peachlab.ui.components.OverviewBox;
import ch.ethz.inf.peachlab.ui.components.TitleLink;
import ch.ethz.inf.peachlab.ui.components.sidebar.TransitionSidebar;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.ClusterClickEvent;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.ClusterMatrix;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.Filterbar;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.KernelClickEvent;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.NotebookMatrix;
import ch.ethz.inf.peachlab.ui.views.home.HomeView;
import ch.ethz.inf.peachlab.ui.views.kernel.KernelView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
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
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_COLOR_GRAY;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

public abstract class AbstractCompetitionView<
    T extends HasCompetitionData<ID, K, C>,
    K extends HasKernelData<ID, ?, T>,
    C extends HasClusterData<K, T>,
    KF extends AbstractKernelFilter<K, ID, T>,
    CF extends AbstractClusterFilter<C, K, T>,
    COF extends AbstractCompetitionFilter<T, ID>,
    ID>
    extends AbstractView implements HasUrlParameter<String> {
    @Serial
    private static final long serialVersionUID = 3416371623163271785L;

    protected final transient BaseService<T, COF, ID> competitionService;
    protected final transient BaseService<K, KF, ID> kernelService;
    private final transient BaseService<C, CF, ?> clusterService;

    protected T competition;

    private final H2 title = new H2();
    private final Div competitionOverview = new Div();
    private final ClusterOverview clusterOverview = new ClusterOverview();

    private final Div matrixDiv = new Div();
    private final Div gridPlaceholder = new Div("Loading notebooks...");
    private final NotebookMatrix matrix = new NotebookMatrix();
    private final ClusterMatrix clusterMatrix = new ClusterMatrix();
    private final Grid<HasKernelData<?, ?, ?>> grid = new Grid<>();
    private final TreeGrid<HasBaseStats> clusterGrid = new TreeGrid<>();
    private final KF kernelFilter;
    private final CF clusterFilter;

    protected AbstractCompetitionView(BaseService<T, COF, ID> competitionService, BaseService<K, KF, ID> kernelService, BaseService<C, CF, ?> clusterService, KF kernelFilter, CF clusterFilter) {
        this.competitionService = competitionService;
        this.kernelService = kernelService;
        this.clusterService = clusterService;
        this.kernelFilter = kernelFilter;
        this.clusterFilter = clusterFilter;
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        addClassNames(STYLE_FLEX_ROW);
    }

    private void initFilters() {
        clusterFilter.setCompetition(competition);
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
    }

    @SafeVarargs
    protected final void initData(Supplier<ServiceResponse<? extends PageImpl<? extends HasKernelData<?, ?, ?>>>>... suppliers) {
        List<Supplier<ServiceResponse<? extends PageImpl<? extends HasKernelData<?, ?, ?>>>>> localSuppliers = new ArrayList<>(List.of(suppliers));
        localSuppliers.add(() -> kernelService.fetch(Pageable.unpaged(), kernelFilter, getKernelLoadType()));
        UiAsyncUtils.<PageImpl<? extends HasKernelData<?, ?, ?>>>callServicesAsync(
            localSuppliers,
            UI.getCurrent(),
            this::onKernelData
        );
    }

    protected abstract HasLoadType getKernelLoadType();

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

        clusterMatrix.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);
        clusterMatrix.addKernelClickedListener(this::onKernelClicked);
        clusterMatrix.addClusterClickedListener(this::onClusterClicked);
        clusterMatrix.setVisible(false);

        UiAsyncUtils.<PageImpl<C>>callServiceAsync(
            () -> clusterService.fetch(Pageable.unpaged(Sort.by("LocalClusterId")), clusterFilter, ClusterLoadType.WITH_KERNELS_AND_CELLS),
            UI.getCurrent(),
            this::onNewClusterMatrixData
        );

        Filterbar bar = new Filterbar();
        bar.render();
        bar.addMarkdownButtonListener(event -> {
            clusterMatrix.getStyle().set("--display-md", event.getShow() ? "block" : "none");
            matrix.getStyle().set("--display-md", event.getShow() ? "block" : "none");
        });
        bar.addHeightButtonListener(event -> {
            if (event.getShow()) {
                matrix.getStyle().set("--cell-height", "initial");
                clusterMatrix.getStyle().set("--cell-height", "initial");
            } else {
                matrix.getStyle().set("--cell-height", "5px");
                clusterMatrix.getStyle().set("--cell-height", "5px");
            }
        });
        bar.addClusterListener(event -> {
            grid.setVisible(!event.isCluster());
            matrix.setVisible(!event.isCluster());
            clusterMatrix.setVisible(event.isCluster());
            clusterGrid.setVisible(event.isCluster());
        });

        DivWithTooltip div = new DivWithTooltip(".cell");
        div.addClassNames(STYLE_PADDING_S, STYLE_BACKGROUND_WHITE, STYLE_HEIGHT_FULL, STYLE_MIN_HEIGHT_0,
            STYLE_FLEX_COLUMN, STYLE_GAP_S);
        div.render();
        div.add(bar);

        matrixDiv.add(matrix, clusterMatrix);
        matrixDiv.setHeightFull();
        matrixDiv.setVisible(false);

        gridPlaceholder.addClassNames(STYLE_TEXT_COLOR_GRAY);
        div.add(gridPlaceholder, matrixDiv);
        return div;
    }

    private void onKernelClicked(KernelClickEvent e) {
        String stringId = e.getKernelId();
        ServiceResponse<? extends HasKernelData<?, ?, ?>> response = getKernelResponse(stringId);
        if (response.hasErrorMessages() || response.getEntity().isEmpty()) {
            response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
            return;
        }
        HasKernelData<?, ?, ?> kernel = response.getEntity().get();
        UI.getCurrent().navigate(KernelView.class, kernel.getUrlParameter());
    }

    protected ServiceResponse<? extends HasKernelData<?, ?, ?>> getKernelResponse(String stringId) {
        return kernelService.fetchById(parseId(stringId));
    }

    protected abstract ID parseId(String stringId);

    private void onClusterClicked(ClusterClickEvent event) {
        Long localClusterId = event.getLocalClusterId();
        if (localClusterId == -1) {
            clusterOverview.setVisible(false);
            competitionOverview.setVisible(true);
            title.setText(competition.getTitle());
            return;
        }
        CF filter = (CF) AbstractClusterFilter.copyFilter(clusterFilter);
        filter.setLocalClusterId(localClusterId);

        UiAsyncUtils.callServiceAsync(() -> clusterService.fetch(Pageable.unpaged(), filter),
            UI.getCurrent(),
            this::onClusterResponse);
    }

    private <R extends ServiceResponse<? extends PageImpl<? extends HasClusterData<?, ?>>>> void onClusterResponse(R response) {
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

    private void onKernelData(List<? extends ServiceResponse<? extends PageImpl<? extends HasKernelData<?, ?, ?>>>> responses) {
        List<HasKernelData<?, ?, ?>> kernels = responses.stream()
            .map(ServiceResponse::getEntity)
            .flatMap(Optional::stream)
            .flatMap(PageImpl::stream)
            .map(o -> (HasKernelData<?, ?, ?>) o)
            .collect(ArrayList::new, ArrayList::add, ArrayList::addAll);

        matrix.setItems(kernels.stream()
            .map(KernelDTO::ofKernel)
            .toList());
        grid.setItems(kernels);

        gridPlaceholder.setVisible(false);
        matrixDiv.setVisible(true);
    }

    private <R extends ServiceResponse<? extends PageImpl<C>>> void onNewClusterMatrixData(R response) {
        clusterMatrix.setItems(
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
        CompetitionStatsPanel<T, KF> stats = new CompetitionStatsPanel<>(competition, kernelFilter, kernelService);
        stats.render();
        return stats;
    }

    protected Component createUpload() {
        return new Div();
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

    protected abstract ServiceResponse<T> getInitResponse(String parameter);

    @Override
    public void setParameter(BeforeEvent beforeEvent, String parameter) {
        ServiceResponse<T> response = getInitResponse(parameter);

        if (response.getEntity().isEmpty() || response.hasErrorMessages()) {
            response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
            UI.getCurrent().navigate(HomeView.class);
            return;
        }
        competition = response.getEntity().get();
        initFilters();
    }
}
