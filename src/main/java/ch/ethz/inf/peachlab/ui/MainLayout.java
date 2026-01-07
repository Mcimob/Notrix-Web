package ch.ethz.inf.peachlab.ui;

import ch.ethz.inf.peachlab.app.AppConfiguration;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;

/**
 * The main view is a top-level placeholder for other views.
 */
@UIScope
@Component
public class MainLayout extends AppLayout {

    private final AppConfiguration appConfiguration;

    public MainLayout(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        setPrimarySection(Section.NAVBAR);
        addHeaderContent();
    }

    private void addHeaderContent() {
        SideNavItem home = new SideNavItem(
                getTranslation(appConfiguration.getApplicationTitle()),
                "",
                VaadinIcon.HOME.create());

        Div layout = new Div(home);
        layout.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_ALIGN_CENTER, STYLE_PADDING_M, STYLE_GAP_M);

        addToNavbar(true, layout);
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
    }
}
