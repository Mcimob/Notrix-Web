package ch.ethz.inf.peachlab.ui.views.kernel;

import ch.ethz.inf.peachlab.backend.service.KernelService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.components.ComponentWithLink;
import ch.ethz.inf.peachlab.ui.components.TransitionSidebarReact;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.Text;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.WildcardParameter;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_BETWEEN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_GAP_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

@Route(value = "code", layout = MainLayout.class)
public class KernelView extends AbstractView implements HasUrlParameter<String> {

    private final KernelService kernelService;

    private KernelEntity kernel;

    public KernelView(KernelService kernelService) {
        this.kernelService = kernelService;
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        addClassNames(STYLE_FLEX_ROW);
    }

    @Override
    public void render() {
        removeAll();
        Div center = new Div(createHeader(), createGrid());
        center.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL, STYLE_FLEX_COLUMN, STYLE_GAP_M);

        Div right = new Div();
        right.addClassNames(STYLE_HEIGHT_FULL, STYLE_BACKGROUND_WHITE);
        right.setWidth("50%");

        add(createSidebar(), center, right);
    }


    private Component createSidebar() {
        TransitionSidebarReact sidebar = new TransitionSidebarReact();
        sidebar.setData(kernel.getMainLabelStats(), kernel.getTransitionMatrix());
        sidebar.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);
        Div div = new Div(sidebar);
        div.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_CENTER, STYLE_BACKGROUND_WHITE);
        div.setWidth("50%");

        return div;
    }

    private Component createHeader() {
        Div textDiv = new Div();
        textDiv.addClassNames(STYLE_FLEX_COLUMN, STYLE_GAP_S);
        textDiv.add(new ComponentWithLink(
            new H2(kernel.getTitle()),
            "https://kaggle.com/code/%s/%s".formatted(kernel.getAuthorUserName(), kernel.getCurrentUrlSlug())
        ));
        textDiv.add(new ComponentWithLink(
            new Text("By " + kernel.getAuthorDisplayName()),
                "https://kaggle.com/%s".formatted(kernel.getAuthorUserName())
        ));

        Div iconsDiv = new Div();
        iconsDiv.addClassNames(STYLE_FLEX_ROW, STYLE_GAP_S);
        Icon download = VaadinIcon.DOWNLOAD.create();
        download.setSize("32px");
        Icon bookmark = VaadinIcon.BOOKMARK_O.create();
        bookmark.setSize("32px");
        iconsDiv.add(download, bookmark);

        Div div = new Div(textDiv, iconsDiv);
        div.addClassNames(STYLE_FLEX_ROW, STYLE_FLEX_BETWEEN, STYLE_FLEX_ALIGN_CENTER,
            STYLE_WIDTH_FULL, STYLE_BACKGROUND_WHITE, STYLE_PADDING_M);

        return div;
    }

    private Component createGrid() {
        ContentGrid grid = new ContentGrid();

        grid.setItems(kernel.getCells());

        Div div = new Div(grid);
        div.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL);

        return div;
    }

    @Override
    public void setParameter(BeforeEvent event, @WildcardParameter String parameter) {
        if (parameter.isEmpty()) {
            add(new Text("Invalid!"));
            return;
        }
        String[] parts = parameter.split("/");
        if (parts.length < 2) {
            add(new Text("Invalid!"));
            return;
        }
        String user = parts[0];
        String slug = parts[1];
        KernelFilter filter = new KernelFilter();
        filter.setUser(user);
        filter.setSlug(slug);

        ServiceResponse<KernelEntity> response = kernelService.fetchOne(filter, KernelLoadType.WITH_CELLS);

        if (response.getEntity().isEmpty() || response.hasErrorMessages()) {
            response.getErrorMessages().stream()
                .map(this::getTranslation)
                .forEach(this::showErrorNotification);
            UI.getCurrent().getPage().getHistory().back();
            return;
        }

        kernel = response.getEntity().get();
    }
}
