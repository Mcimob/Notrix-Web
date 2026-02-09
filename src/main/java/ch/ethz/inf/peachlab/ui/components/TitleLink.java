package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.ui.views.kernel.KernelView;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Span;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_LINK;

public class TitleLink extends Span {

    public TitleLink(KernelEntity kernel) {
        super(kernel.getTitle());
        addClassNames(STYLE_TEXT_LINK);
        addClickListener(click ->
            UI.getCurrent().navigate(KernelView.class, kernel.getUrlParameter()));
    }
}
