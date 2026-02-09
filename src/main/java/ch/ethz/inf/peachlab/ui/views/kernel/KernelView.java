package ch.ethz.inf.peachlab.ui.views.kernel;

import ch.ethz.inf.peachlab.backend.service.ClusterService;
import ch.ethz.inf.peachlab.backend.service.KernelService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.Notebook;
import ch.ethz.inf.peachlab.model.entity.CellEntity;
import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.enums.CellType;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.UiAsyncUtils;
import ch.ethz.inf.peachlab.ui.components.CellColumn;
import ch.ethz.inf.peachlab.ui.components.ComponentWithLink;
import ch.ethz.inf.peachlab.ui.components.DivWithTooltip;
import ch.ethz.inf.peachlab.ui.components.StageChart;
import ch.ethz.inf.peachlab.ui.components.TextWithIcon;
import ch.ethz.inf.peachlab.ui.components.TitleLink;
import ch.ethz.inf.peachlab.ui.components.TripleStats;
import ch.ethz.inf.peachlab.ui.components.sidebar.TransitionSidebar;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.componentfactory.ToggleButton;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.ListItem;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.html.UnorderedList;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;
import org.springframework.data.util.Pair;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_COLOR_GRAY;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_RADIUS_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_STYLE_SOLID;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_WIDTH_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BOX_SHADOW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_BETWEEN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FONT_SIZE_L;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_MAX_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_MIN_HEIGHT_0;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_OVERFLOW_SCROLL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_LINK;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_200;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

@Route(value = "code", layout = MainLayout.class)
public class KernelView extends AbstractView implements HasUrlParameter<String> {

    private static final Pattern PATTERN = Pattern.compile("^(#+) ([^\\n]*)\\n*");
    private static final String HTML_PATTERN = "<\\s*[^>]*>";
    private final KernelService kernelService;
    private final ClusterService clusterService;
    private final ObjectMapper objectMapper;

    private final ContentGrid grid = new ContentGrid();

    private KernelEntity kernel;

    public KernelView(KernelService kernelService, ClusterService clusterService, ObjectMapper objectMapper) {
        this.kernelService = kernelService;
        this.clusterService = clusterService;
        this.objectMapper = objectMapper;
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        addClassNames(STYLE_FLEX_ROW);
    }

    @Override
    public void render() {
        removeAll();
        Div center = new Div(createHeader(), createGrid());
        center.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL, STYLE_FLEX_COLUMN, STYLE_GAP_M);

        Div right = new Div(createStats(), createClusterGrid());
        right.addClassNames(STYLE_HEIGHT_FULL, STYLE_FLEX_COLUMN, STYLE_GAP_M);
        right.setWidth("50%");

        add(createSidebar(), center, right);
    }


    private Component createSidebar() {
        TransitionSidebar sidebar = new TransitionSidebar();
        sidebar.setStageFrequencies(kernel.getMainLabelStats());
        sidebar.setTransitionMatrix(kernel.getTransitionMatrix());
        sidebar.setOpacityTargets(new String[]{"cell-column-element .cell"});
        sidebar.render();
        sidebar.setWidth("50%");

        return sidebar;
    }

    private Component createHeader() {
        Div textDiv = new Div();
        textDiv.addClassNames(STYLE_FLEX_COLUMN, STYLE_GAP_S);
        H2 title = new H2(kernel.getTitle());
        textDiv.add(kernel.getUrlParameter().equals(kernel.getId().toString())
            ? title
            : new ComponentWithLink(title, "https://kaggle.com/code/" + kernel.getUrlParameter()));
        if (kernel.getAuthorUserName() != null) {
            textDiv.add(new ComponentWithLink(
                new Text("By " + kernel.getAuthorDisplayName()),
                "https://kaggle.com/%s".formatted(kernel.getAuthorUserName())
            ));
        }

        Icon download = VaadinIcon.DOWNLOAD.create();
        download.setSize("32px");
        Anchor downloadLink = new Anchor();
        downloadLink.setHref(event -> {
            event.setFileName(kernel.getId() + ".ipynb");
            event.getOutputStream().write(objectMapper.writeValueAsBytes(Notebook.ofKernel(kernel)));
        });
        downloadLink.add(download);

        Icon bookmark = VaadinIcon.BOOKMARK_O.create();
        bookmark.setSize("32px");

        Div iconsDiv = new Div(downloadLink, bookmark);
        iconsDiv.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S);

        Div div = new Div(textDiv, iconsDiv);
        div.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN, STYLE_FLEX_ALIGN_CENTER,
            STYLE_WIDTH_FULL, STYLE_BACKGROUND_WHITE, STYLE_PADDING_M);

        return div;
    }

    private Component createGrid() {
        grid.setItems(kernel.getCells());
        
        CellColumn cellColumn = new CellColumn();
        cellColumn.setKernel(kernel);
        cellColumn.addCellClickListener(idx -> {
            CellEntity cell = kernel.getCells().get(idx);
            grid.scrollToItem(cell);
            grid.select(cell);
        });
        DivWithTooltip columnDiv = new DivWithTooltip(".cell");
        columnDiv.addClassNames(STYLE_OVERFLOW_SCROLL, STYLE_MAX_HEIGHT_FULL);
        columnDiv.render();
        columnDiv.add(cellColumn);

        Div div = new Div();
        div.addClassNames(STYLE_BACKGROUND_WHITE, STYLE_PADDING_M, STYLE_FLEX_COLUMN, STYLE_GAP_S,
            STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);

        Div container = new Div();
        createToc().ifPresent(toc -> {
            container.add(toc);
            toc.setVisible(false);

            ToggleButton button = new ToggleButton();
            button.addValueChangeListener(toggle -> toc.setVisible(toggle.getValue()));
            Div toggleDiv = new Div(button, new TextWithIcon(VaadinIcon.LIST.create(), "Table of Contents"));
            toggleDiv.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_FLEX_ALIGN_CENTER);

            div.add(toggleDiv);
        });
        container.add(columnDiv, grid);
        container.addClassNames(STYLE_HEIGHT_FULL, STYLE_MIN_HEIGHT_0, STYLE_WIDTH_FULL,
            STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_FLEX_ALIGN_CENTER);
        div.add(container);

        return div;
    }
    
    private Optional<Component> createToc() {
        List<TocElement> tocElements = kernel.getCells().stream()
            .filter(c -> c.getCellType() != CellType.CODE)
            .map(c ->
                Arrays.stream(c.getSource().split("\\n")).map(source -> {
                        Matcher matcher = PATTERN.matcher(source);
                        if (!matcher.find() || matcher.groupCount() < 2) {
                            return null;
                        }
                        return matcher;
                    })
                    .filter(Objects::nonNull)
                    .map(matcher -> new TocElement(
                        matcher.group(1).length(),
                        matcher.group(2).replaceAll(HTML_PATTERN, ""),
                        c
                    )))
            .flatMap(Function.identity())
            .filter(Objects::nonNull)
            .toList();

        List<Toc> tocParts = Toc.buildTocTree(tocElements);
        if (tocParts.isEmpty()) {
            return Optional.empty();
        }
        Component list = createTocRecursive(tocParts);
        list.addClassNames(STYLE_MAX_HEIGHT_FULL, STYLE_WIDTH_200, STYLE_OVERFLOW_SCROLL, STYLE_BOX_SHADOW,
            STYLE_PADDING_S, STYLE_BORDER_STYLE_SOLID, STYLE_BORDER_RADIUS_S, STYLE_BORDER_COLOR_GRAY, STYLE_BORDER_WIDTH_S);


        return Optional.of(list);
    }

    private UnorderedList createTocRecursive(List<Toc> tocParts) {
        UnorderedList list = new UnorderedList();
        tocParts.forEach(part -> {
            Span span = new Span(part.title());
            span.addClassNames(STYLE_TEXT_LINK);
            span.addClickListener(click -> {
                grid.scrollToItem(part.cell());
                grid.select(part.cell());
            });
            list.add(new ListItem(span));
            if (!part.children().isEmpty())
                list.add(createTocRecursive(part.children()));
        });

        return list;
    }

    private Component createStats() {
        Div div = new Div(createNumStats(), createBarStats());
        div.addClassNames(STYLE_WIDTH_FULL, STYLE_PADDING_M, STYLE_BACKGROUND_WHITE,
            STYLE_FLEX_COLUMN, STYLE_GAP_M);

        return div;
    }

    private Component createNumStats() {
        TripleStats kernelStats = new TripleStats();
        kernelStats.setStats(List.of(
            Pair.of("Creation Date", kernel.getCreationDate().toLocalDate()),
            Pair.of("Total Lines", kernel.getNumLines()),
            Pair.of("Total Votes", kernel.getTotalVotes())
        ));
        kernelStats.setIcon(VaadinIcon.NOTEBOOK.create());
        kernelStats.setTitleText("Notebook Stats");
        kernelStats.addClassNames(STYLE_WIDTH_FULL);
        kernelStats.render();

        long codeCells = kernel.getCells().stream()
            .filter(c -> c.getCellType() == CellType.CODE)
            .count();
        TripleStats cellStats = new TripleStats();
        cellStats.setStats(List.of(
            Pair.of("Total Cells", kernel.getCellCount()),
            Pair.of("Code Cells", codeCells),
            Pair.of("Md Cells", kernel.getCellCount() - codeCells)
        ));
        cellStats.setIcon(VaadinIcon.CODE.create());
        cellStats.setTitleText("Cell Stats");
        cellStats.addClassNames(STYLE_WIDTH_FULL);
        cellStats.render();

        Div div = new Div(kernelStats, cellStats);
        div.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_M, STYLE_WIDTH_FULL);

        return div;
    }

    private Component createBarStats() {
        StageChart chart = new StageChart();
        chart.setStageStats(kernel.getMainLabelStats());
        chart.render();
        return chart;
    }

    private Component createClusterGrid() {
        Span gridTitle = new Span("Similar Notebooks");
        gridTitle.addClassNames(STYLE_FONT_SIZE_L, STYLE_PADDING_S);
        Grid<KernelEntity> grid = new Grid<>();
        grid.addComponentColumn(TitleLink::new)
            .setHeader("Title")
            .setSortable(true)
            .setFlexGrow(1);
        grid.setEmptyStateText("Loading kernels in same cluster...");
        grid.setHeightFull();

        UiAsyncUtils.callServiceAsync(
            () -> clusterService.fetchById(kernel.getClusterId()),
            UI.getCurrent(),
            res -> res.getEntity()
                .map(ClusterEntity::getKernels)
                .ifPresentOrElse(items -> {
                    items.remove(kernel);
                    grid.setItems(items);
                    },
                    () -> grid.setEmptyStateText("Could not find any kernels in the same cluster"))
        );

        Div div = new Div(gridTitle, grid);
        div.addClassNames(STYLE_FLEX_COLUMN, STYLE_BACKGROUND_WHITE, STYLE_HEIGHT_FULL);
        return div;
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (parameter.isEmpty()) {
            add(new Text("Invalid!"));
            return;
        }
        String[] parts = parameter.split("/");
        KernelFilter filter = new KernelFilter();
        if (parts.length == 1) {
            filter.setId(Long.valueOf(parts[0]));
        } else {
            String user = parts[0];
            String slug = parts[1];
            filter.setUser(user);
            filter.setSlug(slug);
        }

        ServiceResponse<KernelEntity> response = kernelService.fetchOne(filter, KernelLoadType.WITH_CELLS);

        if (response.getEntity().isEmpty() || response.hasErrorMessages()) {
            response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
            UI.getCurrent().getPage().getHistory().back();
            return;
        }

        kernel = response.getEntity().get();
    }
}
