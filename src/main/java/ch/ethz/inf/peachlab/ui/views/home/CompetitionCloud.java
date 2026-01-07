package ch.ethz.inf.peachlab.ui.views.home;

import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.html.Div;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;

public class CompetitionCloud extends Div implements HasRender {

    public CompetitionCloud() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_BACKGROUND_WHITE, STYLE_FLEX_ROW, STYLE_FLEX_CENTER);
    }

    @Override
    public void render() {
        removeAll();
        add("Point Cloud");
    }
}
