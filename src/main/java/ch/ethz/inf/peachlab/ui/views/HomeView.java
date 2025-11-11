package ch.ethz.inf.peachlab.ui.views;

import ch.ethz.inf.peachlab.ui.MainLayout;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.AfterNavigationObserver;
import com.vaadin.flow.router.Route;
import jakarta.annotation.security.PermitAll;

@Route(value = "/", layout = MainLayout.class)
@PermitAll
public class HomeView extends AbstractView {

    public HomeView() {

    }

    @Override
    public void render() {
        add(createTitle());
    }

    private Component createTitle() {
        return new H1("Home");
    }
}
