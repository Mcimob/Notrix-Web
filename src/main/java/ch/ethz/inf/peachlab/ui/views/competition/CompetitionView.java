package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.backend.service.CompetitionService;
import ch.ethz.inf.peachlab.backend.service.KernelService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.dto.KernelDTO;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.UiAsyncUtils;
import ch.ethz.inf.peachlab.ui.components.ComponentWithLink;
import ch.ethz.inf.peachlab.ui.components.DivWithTooltip;
import ch.ethz.inf.peachlab.ui.components.OverviewBox;
import ch.ethz.inf.peachlab.ui.components.TitleLink;
import ch.ethz.inf.peachlab.ui.components.sidebar.TransitionSidebar;
import ch.ethz.inf.peachlab.ui.provider.KernelProvider;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.Filterbar;
import ch.ethz.inf.peachlab.ui.views.competition.matrix.NotebookMatrix;
import ch.ethz.inf.peachlab.ui.views.home.HomeView;
import ch.ethz.inf.peachlab.ui.views.kernel.KernelView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.data.provider.ConfigurableFilterDataProvider;
import com.vaadin.flow.data.provider.SortDirection;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;

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
public class CompetitionView extends AbstractView implements HasUrlParameter<String> {

    private final transient CompetitionService competitionService;
    private final transient KernelService kernelService;

    private CompetitionEntity competition;

    private final NotebookMatrix matrix = new NotebookMatrix();
    private final Grid<KernelEntity> grid = new Grid<>();
    private final KernelFilter filter = new KernelFilter();
    private final ConfigurableFilterDataProvider<KernelEntity, Void, KernelFilter> provider =
            new KernelProvider().withConfigurableFilter();

    public CompetitionView(CompetitionService competitionService, KernelService kernelService) {
        this.competitionService = competitionService;
        this.kernelService = kernelService;
    }

    private void initProvider() {
        filter.setCompetition(competition);
        provider.setFilter(filter);
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        addClassNames(STYLE_FLEX_ROW);
    }

    @Override
    public void render() {
        removeAll();

        Div center = new Div(createTitleBox(), createDescriptionBox(), createNotebookMatrix());
        center.addClassNames(STYLE_FLEX_COLUMN, STYLE_WIDTH_FULL, STYLE_GAP_M);
        center.getStyle().setMinWidth("0");

        Div right = new Div(createStats(), createGrid());
        right.addClassNames(STYLE_FLEX_COLUMN, STYLE_WIDTH_FULL, STYLE_GAP_M);

        add(createSidebar(), center, right);
    }

    private Component createSidebar() {
        TransitionSidebar sidebar = new TransitionSidebar();
        sidebar.setStageFrequencies(competition.getMainLabelStats());
        sidebar.setTransitionMatrix(competition.getTransitionMatrix());
        sidebar.setOpacityTargets(new String[]{"notebook-matrix .cell"});
        sidebar.setWidth("50%");
        sidebar.render();

        return sidebar;
    }

    private Component createTitleBox() {
        Div div = new Div(new ComponentWithLink(
            new H2(competition.getTitle()),
            "https://kaggle.com/competitions/" + competition.getSlug()
        ));
        div.addClassNames(STYLE_BACKGROUND_WHITE, STYLE_WIDTH_FULL, STYLE_PADDING_M);

        return div;
    }

    private Component createDescriptionBox() {
        Div div = new Div();
        div.addClassNames(STYLE_BACKGROUND_WHITE, STYLE_WIDTH_FULL, STYLE_FLEX_COLUMN, STYLE_GAP_S, STYLE_PADDING_M);

        div.add(new H2("Competition description"));

        OverviewBox box = new OverviewBox(competition.getOverview());
        box.render();
        div.add(box);

        return div;
    }

    private Component createNotebookMatrix() {
        matrix.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);
        matrix.addClickedListener(this::onKernelClicked);
        UiAsyncUtils.callServiceAsync(
            () -> kernelService.fetch(Pageable.unpaged(), filter, KernelLoadType.WITH_CELLS),
            UI.getCurrent(),
            this::onNewMatrixData);

        Filterbar bar = new Filterbar();
        bar.render();
        bar.addMarkdownButtonListener(event ->
            matrix.getStyle().set("--display-md", event.getShow() ? "block" : "none"));
        bar.addHeightButtonListener(event -> {
            if (event.getShow()) {
                matrix.getStyle().set("--cell-height", "initial");
            } else {
                matrix.getStyle().set("--cell-height", "5px");
            }
        });

        DivWithTooltip div = new DivWithTooltip(".cell");
        div.addClassNames(STYLE_PADDING_S, STYLE_BACKGROUND_WHITE, STYLE_HEIGHT_FULL, STYLE_MIN_HEIGHT_0,
                STYLE_FLEX_COLUMN, STYLE_GAP_S);
        div.render();
        div.add(bar);
        div.add(matrix);
        return div;
    }

    private void onKernelClicked(Long kernelId) {
        ServiceResponse<KernelEntity> response = kernelService.fetchById(kernelId);
        if (response.hasErrorMessages() || response.getEntity().isEmpty()) {
            response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
            return;
        }
        KernelEntity kernel = response.getEntity().get();
        UI.getCurrent().navigate(KernelView.class, kernel.getUrlParameter());
    }

    private void onNewMatrixData(ServiceResponse<PageImpl<KernelEntity>> response) {
        matrix.setItems(
            response.getEntity()
                .map(PageImpl::stream)
                .orElse(Stream.empty())
                .map(KernelDTO::ofKernel)
                .toList());
    }

    private Component createStats() {
        CompetitionStatsPanel stats = new CompetitionStatsPanel(competition);
        stats.render();
        return stats;
    }

    private Component createGrid() {
        grid.setId("kernel-grid"); // unique DOM id

        grid.addComponentColumn(TitleLink::new)
                .setHeader("Title")
                .setSortable(true)
                .setSortProperty("title")
                .setKey("title")
                .setFlexGrow(1);
        grid.addColumn(KernelEntity::getTotalVotes)
                .setHeader("# Votes")
                .setSortable(true)
                .setSortProperty("totalVotes")
                .setKey("totalVotes")
                .setFlexGrow(0);
        grid.addColumn(KernelEntity::getTotalViews)
                .setHeader("# Views")
                .setSortable(true)
                .setSortProperty("totalViews")
                .setKey("totalViews")
                .setFlexGrow(0);
        grid.addColumn(KernelEntity::getCellCount)
                .setHeader("# Cells")
                .setSortable(true)
                .setSortProperty("cellCount")
                .setKey("cellCount")
                .setFlexGrow(0);
        grid.addColumn(KernelEntity::getNumLines)
                .setHeader("# Lines")
                .setSortable(true)
                .setSortProperty("numLines")
                .setKey("numLines")
                .setFlexGrow(0);

        grid.setHeightFull();
        grid.setSelectionMode(Grid.SelectionMode.SINGLE);
        filter.setCompetition(competition);

        grid.setDataProvider(provider);
        grid.addSortListener(sort -> {
            Sort sortOrders = Sort.by(sort.getSortOrder()
                .stream()
                    .map(s -> new Sort.Order(
                        s.getDirection() == SortDirection.ASCENDING
                            ? Sort.Direction.ASC
                            : Sort.Direction.DESC,
                        s.getSorted().getKey()))
                        .toList());

            UiAsyncUtils.callServiceAsync(
                () -> kernelService.fetch(Pageable.unpaged(sortOrders), filter, KernelLoadType.WITH_CELLS),
                UI.getCurrent(),
                this::onNewMatrixData);
        });

        Div div = new Div(grid);
        div.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);

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
        initProvider();
    }
}
