package ch.ethz.inf.peachlab.ui.components;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;

import java.io.Serial;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;

public class TextWithIcon extends Div {

    @Serial
    private static final long serialVersionUID = -8975418284435953659L;

    public TextWithIcon(Icon icon, String text) {
        addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_FLEX_ALIGN_CENTER);
        icon.setSize("20px");
        add(icon, new Span(text));
    }

}
