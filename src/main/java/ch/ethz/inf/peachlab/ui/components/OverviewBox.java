package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.markdown.Markdown;
import com.vaadin.flow.component.orderedlayout.Scroller;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_COLOR_GRAY;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_RADIUS_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_STYLE_DASHED;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BORDER_WIDTH_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_S;

public class OverviewBox extends Scroller implements HasRender {

    private String content;

    public OverviewBox(String content) {
        this.content = content;
        initStyles();
    }

    public void initStyles() {
        setHeight("20rem");
        addClassNames(STYLE_BORDER_RADIUS_S,
                STYLE_BORDER_WIDTH_S, STYLE_BORDER_STYLE_DASHED, STYLE_BORDER_COLOR_GRAY,
                STYLE_PADDING_S);
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public void render() {
        setContent(new Markdown(content));
    }
}
