package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import org.springframework.data.util.Pair;

import java.io.Serial;
import java.util.List;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_BETWEEN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_JUSTIFY_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_150PX;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_WRAP_NO;

public class TripleStats extends IconLabelContainer implements HasRender {
    @Serial
    private static final long serialVersionUID = 1484326520506977797L;
    private List<Pair<String, Object>> stats;

    public TripleStats() {
        initStyles();
    }

    private void initStyles() {
        addClassNamesToContainer(STYLE_FLEX_COLUMN, STYLE_FLEX_JUSTIFY_CENTER, STYLE_GAP_M, STYLE_HEIGHT_150PX);
    }

    private void addStats() {
        stats.stream()
            .map(p -> {
                Div row = new Div(new Span(p.getFirst()), new Span(p.getSecond().toString()));
                row.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN, STYLE_GAP_M, STYLE_TEXT_WRAP_NO);
                return row;
            }).forEach(this::addToContainer);
    }

    @Override
    public void render() {
        super.render();
        if (stats == null || stats.size() > 3) {
            return;
        }
        addStats();
    }

    public void setStats(List<Pair<String, Object>> stats) {
        this.stats = stats;
    }
}
