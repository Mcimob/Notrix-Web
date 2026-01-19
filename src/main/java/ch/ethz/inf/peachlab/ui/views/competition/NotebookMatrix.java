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
import com.vaadin.flow.component.ClientCallable;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.shared.Registration;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Slice;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_CELL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;
import static java.lang.Math.min;

public class NotebookMatrix extends Scroller implements HasLogger, HasNotification, HasRender {

    private static final String JS = """
    let timer = null;
    
    this.addEventListener("mousemove", () => {
                      clearTimeout(timer);
                      timer = setTimeout(() => $0.onMouseStop(this.kernelIndex), 300);
                  });""";

    private final CompetitionEntity competition;

    public NotebookMatrix(CompetitionEntity competition) {
        this.competition = competition;
    }

    private Component createColumn(KernelEntity kernel, int index) {
        Div div = new Div();
        div.setWidth("1rem");
        div.addClassNames(STYLE_FLEX_COLUMN);
        div.getStyle().setFlexShrink("0");
        div.getStyle().setGap("1px");
        div.getElement().setProperty("kernelIndex", index);
        kernel.getCells().stream()
                .filter(Objects::nonNull)
                .filter(c -> c.getCellType() == CellType.CODE)
                .map(c -> new Cell(c, kernel))
                .forEach(div::add);

        return div;
    }

    @ClientCallable
    private void onMouseStop(int index) {
        fireEvent(new KernelHoverEvent(index, this, true));
    }

    private List<Component> createColumns() {
        KernelService service = SpringContext.getBean(KernelService.class);
        KernelFilter filter = new KernelFilter();
        filter.setCompetition(competition);
        ServiceResponse<Page<KernelEntity>> response =
                service.fetch(Pageable.unpaged(), filter, KernelLoadType.WITH_CELLS);
        response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
        List<KernelEntity> kernels = response.getEntity()
                .map(Slice::getContent)
                .orElse(List.of());

        List<Component> result = new ArrayList<>();
        for (int i = 0; i < kernels.size(); i++) {
            result.add(createColumn(kernels.get(i), i));
        }

        return result;
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

        // attach hover JS globally
        getElement().executeJs("""
        let timer = null;
        this.addEventListener('mousemove', e => {
            const target = e.composedPath()[0];
            const index = target.kernelIndex;
            if (index === undefined) return;
            clearTimeout(timer);
            timer = setTimeout(() => this.$server.onMouseStop(index), 300);
        });
    """);
    }

    public Registration addHoverListener(ComponentEventListener<KernelHoverEvent> listener) {
        return addListener(KernelHoverEvent.class, listener);
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
