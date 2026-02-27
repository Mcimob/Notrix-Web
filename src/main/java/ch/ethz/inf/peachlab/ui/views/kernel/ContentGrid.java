package ch.ethz.inf.peachlab.ui.views.kernel;

import ch.ethz.inf.peachlab.model.entity.HasCellData;
import ch.ethz.inf.peachlab.model.enums.CellType;
import com.flowingcode.vaadin.addons.syntaxhighlighter.ShLanguage;
import com.flowingcode.vaadin.addons.syntaxhighlighter.ShStyle;
import com.flowingcode.vaadin.addons.syntaxhighlighter.SyntaxHighlighter;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.grid.GridVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.orderedlayout.Scroller;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BOX_SHADOW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FONT_SIZE_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FW_500;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_MARGIN_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_COLOR_GRAY;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

public class ContentGrid extends Grid<HasCellData> {

    public ContentGrid() {
        super();
        styleGrid();
        addComponentColumn(this::createCell);
    }

    private void styleGrid() {
        setHeightFull();
        addThemeVariants(GridVariant.LUMO_NO_ROW_BORDERS, GridVariant.LUMO_NO_BORDER);
        setSelectionMode(SelectionMode.SINGLE);
    }

    private Component createCell(HasCellData cell) {
        Span indexText = new Span("[%s]".formatted(cell.getCellId()));
        indexText.addClassNames(STYLE_TEXT_COLOR_GRAY);
        Div div = new Div(indexText);
        div.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_MARGIN_S);
        Component cellComponent = cell.getCellType() == CellType.CODE
            ? createCodeCell(cell)
            : createMdCell(cell);
        div.add(cellComponent);

        return div;
    }

    private Component createCodeCell(HasCellData cell) {
        Div div = new Div();
        div.addClassNames(STYLE_BOX_SHADOW, STYLE_PADDING_S, STYLE_FLEX_COLUMN, STYLE_GAP_S, STYLE_WIDTH_FULL);
        div.getStyle().setBorderLeft(".5rem solid %s".formatted(cell.getMainLabel().getColor()));
        div.getStyle().setBorderRadius("var(--size-s) 0 0 var(--size-s)");

        Span label = new Span(getTranslation(cell.getMainLabel().getTitleKey()).toUpperCase());
        label.addClassNames(STYLE_FW_500, STYLE_FONT_SIZE_S);
        label.getStyle().setColor(cell.getMainLabel().getColor());
        div.add(label);

        SyntaxHighlighter sh = new SyntaxHighlighter();
        sh.setShLanguage(ShLanguage.PYTHON);
        sh.setShStyle(ShStyle.GOOGLECODE);
        sh.setContent(cell.getSource());
        sh.setShowLineNumbers(true);
        div.add(sh);

        return div;
    }

    private Component createMdCell(HasCellData cell) {
        Scroller scroller = new Scroller(new Markdown(cell.getSource()));
        scroller.addClassNames(STYLE_BOX_SHADOW, STYLE_PADDING_S, STYLE_WIDTH_FULL);
        return scroller;
    }
}
