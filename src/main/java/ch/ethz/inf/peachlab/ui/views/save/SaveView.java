package ch.ethz.inf.peachlab.ui.views.save;

import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.KernelService;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.model.dto.SavedNotebook;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.model.filter.UploadedKernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import ch.ethz.inf.peachlab.model.loadtype.UploadedKernelLoadType;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import ch.ethz.inf.peachlab.ui.webstorage.HasSavedKernels;
import ch.ethz.inf.peachlab.ui.webstorage.ManagesProcessingNotebooks;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.io.Serial;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;
import static java.util.function.Predicate.not;

@Route(value = "save", layout = MainLayout.class)
public class SaveView extends AbstractView implements HasSavedKernels, ManagesProcessingNotebooks {

    @Serial
    private static final long serialVersionUID = -399023050405817362L;
    private final KernelService kernelService;

    private final NotebookGrid uploadedGrid = new NotebookGrid();
    private final NotebookGrid savedGrid = new NotebookGrid();
    private final UploadedKernelService uploadedKernelService;

    public SaveView(KernelService kernelService, UploadedKernelService uploadedKernelService) {
        this.kernelService = kernelService;
        this.uploadedKernelService = uploadedKernelService;
    }

    @Override
    protected void initStyles() {
        super.initStyles();
        addClassNames(STYLE_FLEX_ROW);
    }

    @Override
    public void render() {
        removeAll();
        add(createLeft());
        add(createRight());
    }

    private Component createLeft() {
        uploadedGrid.setEmptyStateText("Loading uploaded notebooks...");

        getUploadedNotebooks(this::onUploadedKernels);

        H2 title = new H2("Your Notebooks");
        title.addClassNames(STYLE_PADDING_M);
        Div div = new Div(title, uploadedGrid);
        div.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL, STYLE_BACKGROUND_WHITE);
        return div;
    }

    private Component createRight() {
        savedGrid.setEmptyStateText("Loading saved notebooks...");

        getSavedKernels(this::processSavedKernels);

        H2 title = new H2("Saved Notebooks");
        title.addClassNames(STYLE_PADDING_S);
        Div div = new Div(title, savedGrid);
        div.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL, STYLE_BACKGROUND_WHITE);
        return div;
    }

    private void processSavedKernels(Set<Long> savedKernelIds) {
        KernelFilter filter = new KernelFilter();
        filter.setIds(savedKernelIds);
        ServiceResponse<? extends PageImpl<? extends HasKernelData<?,?>>> kernelResponse = kernelService.fetch(Pageable.unpaged(), filter, KernelLoadType.WITH_COMPETITION);
        updateGrid(savedGrid, kernelResponse);
    }

    private void onUploadedKernels(Map<Long, Set<String>> uploadedKernels) {
        UploadedKernelFilter filter = new UploadedKernelFilter();
        filter.setIds(uploadedKernels.values().stream()
            .flatMap(Set::stream)
            .collect(Collectors.toSet()));
        ServiceResponse<PageImpl<UploadedKernelEntity>> kernelResponse = uploadedKernelService.fetch(Pageable.unpaged(), filter, UploadedKernelLoadType.WITH_COMPETITION);
        updateGrid(uploadedGrid, kernelResponse);
    }

    private void updateGrid(NotebookGrid grid, ServiceResponse<? extends PageImpl<? extends HasKernelData<?, ?>>> response) {
        response.getEntity()
            .map(PageImpl::stream)
            .map(s -> s
                .collect(Collectors.groupingBy(HasKernelData::getCompetition))
                .entrySet()
                .stream()
                .map(e -> {
                    List<SavedNotebook> children = e.getValue().stream().map(SavedNotebook::new).toList();
                    return new SavedNotebook(e.getKey(), children);
                })
                .toList())
            .filter(not(List::isEmpty))
            .ifPresentOrElse(items -> grid.setItems(items, SavedNotebook::getChildren),
                () -> grid.setEmptyStateText("No notebooks found"));
    }
}
