package ch.ethz.inf.peachlab.ui.components;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.AnchorTarget;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;

import java.io.Serial;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;

public class ComponentWithLink extends Div{

    @Serial
    private static final long serialVersionUID = -8605913676418169762L;

    public ComponentWithLink(Component component, String href) {
        add(component, createAnchor(href));
        addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S, STYLE_FLEX_ALIGN_CENTER);
    }

    private Component createAnchor(String href) {
        Anchor anchor = new Anchor();
        anchor.setHref(href);
        anchor.setTarget(AnchorTarget.BLANK);
        Icon icon = VaadinIcon.EXTERNAL_LINK.create();
        icon.setSize("16px");
        anchor.add(icon);
        return anchor;
    }

}
