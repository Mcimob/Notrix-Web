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
import ch.ethz.inf.peachlab.ui.components.DivWithTooltip;
import ch.ethz.inf.peachlab.ui.views.HasNotification;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_CELL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;
import static java.lang.Math.min;

public class NotebookMatrix extends Scroller implements HasLogger, HasNotification, HasRender {

    private final CompetitionEntity competition;

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

    private Stream<Component> createColumns() {
        KernelService service = SpringContext.getBean(KernelService.class);
        KernelFilter filter = new KernelFilter();
        filter.setCompetition(competition);
        ServiceResponse<Page<KernelEntity>> response =
                service.fetch(Pageable.unpaged(), filter, KernelLoadType.WITH_CELLS);
        response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
        return response.getEntity()
                .map(Slice::getContent)
                .map(List::stream).stream()
                .flatMap(l -> l.map(this::createColumn));
    }

    private Component createContent() {
        DivWithTooltip content = new DivWithTooltip(".cell");
        content.render();
        content.addClassNames(STYLE_FLEX_ROW, STYLE_HEIGHT_FULL);
        content.getStyle().setGap("2px");
        content.getStyle().setFlexShrink("0");

        createColumns().forEach(content::add);

        return content;
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
            setHeight(".5rem");
            getStyle().setFlexShrink("0");
            if (cell.getCellType() == CellType.CODE) {
                getElement().setAttribute("data-tooltip", "Stage: %s<br/>Title: %s<br/>Lines: %s".formatted(
                        getTranslation("entity.cell.mainLabel." + min(11, cell.getMainLabel())),
                        kernel.getTitle(),
                        cell.getSourceLinesCount()));
            }
        }
    }
}
