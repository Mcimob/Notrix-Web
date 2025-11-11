package ch.ethz.inf.peachlab.ui.views;

import ch.ethz.inf.peachlab.ui.HasRender;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationEvent;
import com.vaadin.flow.router.AfterNavigationObserver;

public abstract class AbstractView extends VerticalLayout implements AfterNavigationObserver, HasRender {


    @Override
    public void afterNavigation(AfterNavigationEvent afterNavigationEvent) {
        render();
    }
}
