package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.BaseService;
import ch.ethz.inf.peachlab.model.dto.ClusterDTO;
import ch.ethz.inf.peachlab.model.entity.HasBaseStats;
import ch.ethz.inf.peachlab.model.entity.HasClusterData;
import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.filter.AbstractClusterFilter;
import ch.ethz.inf.peachlab.model.filter.AbstractCompetitionFilter;
import ch.ethz.inf.peachlab.model.filter.AbstractKernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.HasLoadType;
import ch.ethz.inf.peachlab.ui.UiAsyncUtils;
import ch.ethz.inf.peachlab.ui.components.ComponentWithLink;
import ch.ethz.inf.peachlab.ui.components.DivWithTooltip;
import ch.ethz.inf.peachlab.ui.components.OverviewBox;
import ch.ethz.inf.peachlab.ui.components.StageChart;
import ch.ethz.inf.peachlab.ui.components.TitleLink;
import ch.ethz.inf.peachlab.ui.components.TripleStats;
import ch.ethz.inf.peachlab.ui.components.sidebar.TransitionSidebar;
import ch.ethz.inf.peachlab.ui.provider.KernelProvider;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import ch.ethz.inf.peachlab.ui.views.competition.components.ClusterOverview;
import ch.ethz.inf.peachlab.ui.views.competition.components.KernelGrid;
import ch.ethz.inf.peachlab.ui.views.competition.components.matrix.ClusterClickEvent;
import ch.ethz.inf.peachlab.ui.views.competition.components.matrix.ClusterMatrix;
import ch.ethz.inf.peachlab.ui.views.competition.components.matrix.Filterbar;
import ch.ethz.inf.peachlab.ui.views.competition.components.matrix.KernelClickEvent;
import ch.ethz.inf.peachlab.ui.views.competition.components.matrix.LoadMoreClickEvent;
import ch.ethz.inf.peachlab.ui.views.competition.components.matrix.NotebookMatrix;
import ch.ethz.inf.peachlab.ui.views.home.HomeView;
import ch.ethz.inf.peachlab.ui.views.kernel.KernelView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.splitlayout.SplitLayout;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.data.provider.QuerySortOrder;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.util.Pair;

import java.io.Serial;
import java.util.List;
import java.util.Optional;
import java.util.function.Supplier;
import java.util.stream.Stream;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_BG;
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
    public static final String DECIMAL_FORMAT = "%.2f";
    public static final int KERNEL_PAGE_SIZE = 50;

    protected final transient BaseService<T, COF, ID> competitionService;
    protected final transient BaseService<K, KF, ID> kernelService;
    private final transient BaseService<C, CF, ?> clusterService;

    protected T competition;

    private final H2 title = new H2();
    private final Div competitionOverview = new Div();
    private final ClusterOverview clusterOverview = new ClusterOverview();

    private final Div matrixDiv = new Div();
    private final Div clusterMatrixDiv = new Div();
    private final Div gridPlaceholder = new Div("Loading notebooks...");
    private final Div clusterGridPlaceholder = new Div("Loading clusters....");
    private final NotebookMatrix matrix = new NotebookMatrix();
    private final ClusterMatrix clusterMatrix = new ClusterMatrix();
    private final KernelGrid grid = new KernelGrid();
    private final TreeGrid<HasBaseStats> clusterGrid = new TreeGrid<>();
    private final KF kernelFilter;
    private final CF clusterFilter;

    protected AbstractCompetitionView(BaseService<T, COF, ID> competitionService, BaseService<K, KF, ID> kernelService, BaseService<C, CF, Long> clusterService, KF kernelFilter, CF clusterFilter) {
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
        kernelFilter.setCompetition(competition);
    }

    @Override
    public void render() {
        removeAll();

        Div top = new Div(createTitleBox(), createClusterOverview(), createDescriptionBox());
        top.addClassNames(STYLE_FLEX_COLUMN, STYLE_GAP_M, STYLE_BACKGROUND_BG, STYLE_HEIGHT_FULL);
        SplitLayout center = new SplitLayout(top, createMatrices(), SplitLayout.Orientation.VERTICAL);

        Div right = new Div(createTopRight(), createChart(), createGrids());
        right.addClassNames(STYLE_FLEX_COLUMN, STYLE_WIDTH_FULL, STYLE_GAP_M);

        SplitLayout rightLayout = new SplitLayout(center, right);
        rightLayout.setSplitterPosition(66);
        SplitLayout layout = new SplitLayout(createSidebar(), rightLayout);
        layout.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);
        layout.setSplitterPosition(25);
        add(layout);
    }

    protected final void initData() {
        onKernelData(new ServiceResponse<>());
    }

    protected final void initData(Supplier<ServiceResponse<? extends PageImpl<HasKernelData<?, ?, ?>>>> localSupplier) {
        UiAsyncUtils.<PageImpl<HasKernelData<?, ?, ?>>>callServiceAsync(
            localSupplier,
            UI.getCurrent(),
            this::onKernelData
        );
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

        Component titleComponent = Optional.ofNullable(competition.getSlug())
            .map(slug -> new ComponentWithLink(
                title,
                "https://kaggle.com/competitions/" + slug
            ))

            .map(Component.class::cast)
            .orElse(title);
        titleComponent.addClassNames(STYLE_BACKGROUND_WHITE, STYLE_WIDTH_FULL, STYLE_PADDING_M);

        return titleComponent;
    }

    private Component createDescriptionBox() {
        competitionOverview.addClassNames(STYLE_BACKGROUND_WHITE, STYLE_WIDTH_FULL, STYLE_FLEX_COLUMN, STYLE_GAP_S, STYLE_PADDING_M, STYLE_MIN_HEIGHT_0, STYLE_HEIGHT_FULL);
        competitionOverview.add(new H2("Competition description"));

        OverviewBox box = new OverviewBox(competition.getOverview());
        box.addClassNames(STYLE_HEIGHT_FULL);
        box.render();
        competitionOverview.add(box);

        return competitionOverview;
    }

    private Component createClusterOverview() {
        clusterOverview.setVisible(false);
        clusterOverview.setCluster(null);
        clusterOverview.render();
        return clusterOverview;
    }

    private Component createMatrices() {
        matrix.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);
        matrix.addKernelClickedListener(this::onKernelClicked);
        matrix.setVisible(false);
        matrix.addLoadMoreClickedListener(this::onMoreKernelsRequested);

        clusterMatrix.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);
        clusterMatrix.addKernelClickedListener(this::onKernelClicked);
        clusterMatrix.addClusterClickedListener(this::onClusterClicked);
        clusterMatrix.setVisible(false);

        UiAsyncUtils.callServiceAsync(
            () -> clusterService.fetch(Pageable.unpaged(Sort.by("LocalClusterId")), clusterFilter),
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
            matrixDiv.setVisible(!event.isCluster());
            competitionOverview.setVisible(!event.isCluster());
            clusterMatrixDiv.setVisible(event.isCluster());
            clusterGrid.setVisible(event.isCluster());
            clusterOverview.setVisible(event.isCluster());
            if (!event.isCluster()) {
                title.setText(competition.getTitle());
            }
        });

        DivWithTooltip div = new DivWithTooltip(".cell");
        div.addClassNames(STYLE_PADDING_S, STYLE_BACKGROUND_WHITE, STYLE_HEIGHT_FULL, STYLE_MIN_HEIGHT_0,
            STYLE_FLEX_COLUMN, STYLE_GAP_S);
        div.render();
        div.add(bar);


        gridPlaceholder.addClassNames(STYLE_TEXT_COLOR_GRAY);
        matrixDiv.add(matrix, gridPlaceholder);
        matrixDiv.setHeightFull();

        clusterGridPlaceholder.addClassNames(STYLE_TEXT_COLOR_GRAY);
        clusterMatrixDiv.add(clusterMatrix, clusterGridPlaceholder);
        clusterMatrixDiv.setHeightFull();
        clusterMatrixDiv.setVisible(false);

        div.add(matrixDiv, clusterMatrixDiv);
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

    private void onMoreKernelsRequested(LoadMoreClickEvent event) {
        Stream<HasKernelData<?, ?, ?>> fetch = grid.getDataProvider().fetch(new Query<>((int) event.getCurrentSize(), KERNEL_PAGE_SIZE, grid.getSortOrder().stream()
            .map(s -> new QuerySortOrder(s.getSorted().getKey(), s.getDirection()))
            .toList(), null, null));
        matrix.addItems(fetch.toList());

    }

    protected abstract ID parseId(String stringId);

    private void onClusterClicked(ClusterClickEvent event) {
        Long localClusterId = event.getLocalClusterId();
        if (localClusterId == -1) {
            clusterOverview.setCluster(null);
            clusterOverview.render();
            title.setText(competition.getTitle());
            return;
        }
        @SuppressWarnings("unchecked")
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
                clusterOverview.setCluster(c);
                clusterOverview.render();
                clusterGrid.expand(c);
                clusterGrid.scrollToIndex(c.getLocalClusterId().intValue() - 1, 0);
                title.setText("Cluster " + c.getLocalClusterId());
            });
    }

    private void onKernelData(ServiceResponse<? extends PageImpl<HasKernelData<?, ?, ?>>> response) {
        KernelProvider<K, KF> provider = new KernelProvider<>(kernelService, response.getEntity().map(PageImpl::toList).orElse(List.of()));
        ConfigurableFilterDataProvider<HasKernelData<?, ?, ?>, Void, AbstractKernelFilter<HasKernelData<?, ?, ?>, ?, ?>> providerWithFilter =
            provider.withConfigurableFilter();
        //noinspection unchecked
        providerWithFilter.setFilter((AbstractKernelFilter<HasKernelData<?, ?, ?>, ?, ?>) kernelFilter);
        grid.setDataProvider(providerWithFilter);

        int numItems = providerWithFilter.size(new Query<>());
        matrix.setTotalItems(numItems);
        onMoreKernelsRequested(new LoadMoreClickEvent(this, false, 0));

        gridPlaceholder.setVisible(false);
        matrix.setVisible(true);
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

        clusterGridPlaceholder.setVisible(false);
        clusterMatrix.setVisible(true);
    }

    private Component createTopRight() {
        ServiceResponse<Long> countResponse = kernelService.count(kernelFilter);;

        TripleStats stats = new TripleStats();
        stats.setStats(List.of(
            Pair.of("Total Notebooks", countResponse.getEntity().orElse(0L).toString()),
            Pair.of("Avg. Cells", String.format (DECIMAL_FORMAT, competition.getAvgCellsPerKernel())),
            Pair.of("Avg. Votes", String.format (DECIMAL_FORMAT, competition.getAvgVotes()))
        ));
        stats.render();
        stats.addClassNames(STYLE_WIDTH_FULL);

        Div div = new Div(stats, createUpload());
        div.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_PADDING_M, STYLE_BACKGROUND_WHITE);

        return div;
    }

    private Component createChart() {
        StageChart chart = new StageChart();
        chart.addClassNames(STYLE_BACKGROUND_WHITE, STYLE_PADDING_M);
        chart.setStageStats(competition.getMainLabelStats());
        chart.render();

        return chart;
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
        grid.setHeightFull();

        grid.addSortListener(sort -> {
            matrix.clearItems();
            UI.getCurrent().push();
            onMoreKernelsRequested(new LoadMoreClickEvent(this, sort.isFromClient(), 0));
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
        clusterGrid.setEmptyStateText("Loading clusters....");

        clusterGrid.setVisible(false);
        return clusterGrid;
    }

    private Component createTitleElement(HasBaseStats kernelData) {
        if (kernelData instanceof HasKernelData<?, ?, ?> kernel) {
            return TitleLink.ofKernel(kernel);
        }
        if (kernelData instanceof HasClusterData<?, ?> cluster) {
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
