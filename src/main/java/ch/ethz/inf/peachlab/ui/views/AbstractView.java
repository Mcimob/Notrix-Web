package ch.ethz.inf.peachlab.ui.views;

import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_BG;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

public abstract class AbstractView extends Div implements AfterNavigationObserver, HasRender, HasLogger {

    public AbstractView() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_BACKGROUND_BG, STYLE_PADDING_M, STYLE_WIDTH_FULL, STYLE_HEIGHT_FULL, STYLE_GAP_M, STYLE_FLEX_COLUMN);
    }

    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        render();
    }
}
