package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.backend.service.KernelService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.model.entity.CellEntity;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.enums.CellType;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import ch.ethz.inf.peachlab.ui.DesignConstants;
import ch.ethz.inf.peachlab.ui.HasRender;
import ch.ethz.inf.peachlab.ui.views.HasNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.data.provider.DataProvider;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_CELL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;
import static java.lang.Math.min;

public class NotebookMatrix extends Scroller implements HasLogger, HasNotification, HasRender {

    public static final String[] LABELS = {
            "Environment",
            "Data Extraction",
            "Data Transform",
            "EDA",
            "Visualization",
            "Feature Engineering",
            "Hyperparam Tuning",
            "Model Train",
            "Model Evaluation",
            "Data Export",
            "Commented",
            "Other"
    };

    private static final String CONTENT_JS = """
      const container = this;
      const tooltip = container.querySelector('.matrix-tooltip');
    
      container.addEventListener('mousemove', e => {
        const cell = e.target.closest('.cell');
        if (!cell) {
          tooltip.style.display = 'none';
          return;
        }
    
        tooltip.innerHTML = cell.dataset.tooltip;
        tooltip.style.display = 'block';
    
        const cellRect = cell.getBoundingClientRect();
    
        let left = cellRect.right + 6;
        let top = cellRect.top;
    
        tooltip.style.left = left + 'px';
        tooltip.style.top = top + 'px';
      });
    
      container.addEventListener('mouseleave', () => {
        tooltip.style.display = 'none';
      });
    """;

    private static final int PAGE_SIZE = 100;
    private final AtomicInteger loaded = new AtomicInteger(0);

    private final CompetitionEntity competition;

    private DataProvider<KernelEntity, KernelFilter> provider;

    private final Div content = new Div();

    public NotebookMatrix(CompetitionEntity competition) {
        this.competition = competition;
    }

    private Component createColumn(KernelEntity kernel) {
        Div div = new Div();
        div.setWidth("1rem");
        div.addClassNames(STYLE_FLEX_COLUMN);
        div.getStyle().setFlexShrink("0");
        div.getStyle().setGap("1px");
        kernel.getCells().stream()
                .filter(Objects::nonNull)
                .filter(c -> c.getCellType() == CellType.CODE)
                .map(c -> new Cell(c, kernel))
                .forEach(div::add);

        return div;
    }

    private void createColumns() {
        KernelService service = SpringContext.getBean(KernelService.class);
        KernelFilter filter = new KernelFilter();
        filter.setCompetition(competition);
        ServiceResponse<Page<KernelEntity>> response =
                service.fetch(Pageable.unpaged(), filter, KernelLoadType.WITH_CELLS);
        response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
        response.getEntity()
                .map(Slice::getContent)
                .ifPresent(l -> l.stream()
                    .map(this::createColumn)
                    .forEach(content::add));
    }

    private Component createContent() {
        content.add(createTooltip());

        content.addClassNames(STYLE_FLEX_ROW, STYLE_HEIGHT_FULL);
        content.getStyle().setGap("2px");
        content.getStyle().setFlexShrink("0");
        content.getElement().executeJs(CONTENT_JS);

        return content;
    }

    private Component createTooltip() {
        Div tooltip = new Div();
        tooltip.addClassName("matrix-tooltip");
        tooltip.getStyle()
                .set("position", "absolute")
                .set("display", "none")
                .set("pointer-events", "none")
                .set("z-index", "1000");

        return tooltip;
    }

    @Override
    public void render() {
        setScrollDirection(ScrollDirection.BOTH);
        setSizeFull();
        setContent(createContent());

        createColumns();
    }

    private static class Cell extends Div {
        public Cell(CellEntity cell, KernelEntity kernel) {
            addClassNames(STYLE_WIDTH_FULL, STYLE_CELL);
            getStyle().setBackgroundColor(Optional.ofNullable(cell.getMainLabel())
                    .map(i -> DesignConstants.StageColors.COLORS[i])
                    .orElse("white"));
            setHeight(".5rem" +
                    "");
            getStyle().setFlexShrink("0");
            if (cell.getCellType() == CellType.CODE) {
                getElement().setAttribute("data-tooltip", "Stage: %s<br/>Title: %s<br/>Lines: %s".formatted(
                        getTranslation("entity.cell.mainLabel." + min(11, cell.getMainLabel())),
                        kernel.getTitle(),
                        cell.getSource().split("\\n").length));
            }
        }
    }
}
