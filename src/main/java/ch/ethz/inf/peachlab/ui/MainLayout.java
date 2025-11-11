package ch.ethz.inf.peachlab.ui;

import ch.ethz.inf.peachlab.app.AppConfiguration;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Footer;
import com.vaadin.flow.component.html.Header;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.Scroller;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.annotation.UIScope;
import com.vaadin.flow.theme.lumo.LumoUtility;
import org.springframework.stereotype.Component;

/**
 * The main view is a top-level placeholder for other views.
 */
@UIScope
@Component
public class MainLayout extends AppLayout {

    private final AppConfiguration appConfiguration;

    public MainLayout(AppConfiguration appConfiguration) {
        this.appConfiguration = appConfiguration;
        setPrimarySection(Section.DRAWER);
        addDrawerContent();
        addHeaderContent();
    }

    private void addHeaderContent() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.setAriaLabel("Menu toggle");

        HorizontalLayout leftLayout = new HorizontalLayout(toggle);
        leftLayout.setAlignItems(FlexComponent.Alignment.BASELINE);
        HorizontalLayout title = new HorizontalLayout(new Div(appConfiguration.getApplicationTitle()));
        title.addClassName(DesignConstants.STYLE_FW_700);
        title.setAlignItems(FlexComponent.Alignment.CENTER);

        HorizontalLayout layout = new HorizontalLayout(leftLayout, title);
        layout.setMargin(true);
        layout.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        layout.setWidthFull();

        addToNavbar(true, layout);
    }

    private void addDrawerContent() {
        Span appName = new Span(appConfiguration.getApplicationTitle());
        appName.addClassNames(LumoUtility.FontWeight.SEMIBOLD, LumoUtility.FontSize.LARGE);
        Header header = new Header(appName);

        Scroller scroller = new Scroller(createNavigation());

        addToDrawer(header, scroller, createFooter());
    }

    private SideNav createNavigation() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem(
                getTranslation("view.home.title"),
                "",
                VaadinIcon.HOME.create()));

        return nav;
    }

    private Footer createFooter() {
        Footer layout = new Footer();

        return layout;
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
    }
}
