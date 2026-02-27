package ch.ethz.inf.peachlab.ui;

import ch.ethz.inf.peachlab.app.AppConfiguration;
import ch.ethz.inf.peachlab.backend.ProcessedNotebookBroadcaster;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.ui.webstorage.ManagesProcessingNotebooks;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.spring.annotation.UIScope;
import org.springframework.stereotype.Component;

import java.io.Serial;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;

/**
 * The main view is a top-level placeholder for other views.
 */
@UIScope
@Component
public class MainLayout extends AppLayout implements ManagesProcessingNotebooks {

    @Serial
    private static final long serialVersionUID = 1794503673546931577L;
    private final transient AppConfiguration appConfiguration;
    private final UploadedKernelService kernelService;

    public MainLayout(AppConfiguration appConfiguration, UploadedKernelService kernelService) {
        this.appConfiguration = appConfiguration;
        this.kernelService = kernelService;
        setPrimarySection(Section.NAVBAR);
        addHeaderContent();
    }

    private void addHeaderContent() {
        SideNavItem home = new SideNavItem(
                getTranslation(appConfiguration.getApplicationTitle()),
                "",
                VaadinIcon.HOME.create());

        SideNavItem saved = new SideNavItem(
            "Saved",
            "save",
            VaadinIcon.BOOKMARK.create()
        );

        Div layout = new Div(home, saved);
        layout.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_ALIGN_CENTER, STYLE_PADDING_M, STYLE_GAP_M);

        addToNavbar(true, layout);
    }

    @Override
    protected void afterNavigation() {
        super.afterNavigation();
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);
        UI ui = attachEvent.getUI();
        getProcessingNotebooks(p -> p
            .forEach((identifier, nb) ->
                UiAsyncUtils.callServiceAsync(
                    () -> kernelService.existsById(identifier),
                    ui, res -> res.getEntity()
                        .filter(Boolean::booleanValue)
                        .ifPresentOrElse(
                            c -> onNotebooksProcessingDone(identifier),
                            () -> ProcessedNotebookBroadcaster.register(this::onNotebooksProcessingDone, identifier, ui))
                )
            )
        );
    }
}
