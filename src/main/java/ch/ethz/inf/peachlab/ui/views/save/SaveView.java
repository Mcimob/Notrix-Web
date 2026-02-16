package ch.ethz.inf.peachlab.ui.views.save;

import ch.ethz.inf.peachlab.backend.service.KernelService;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.model.Notebook;
import ch.ethz.inf.peachlab.model.dto.SavedNotebook;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import ch.ethz.inf.peachlab.ui.HasSavedKernels;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.components.TitleLink;
import ch.ethz.inf.peachlab.ui.views.AbstractView;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.router.Route;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ROW;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_S;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

@Route(value = "save", layout = MainLayout.class)
public class SaveView extends AbstractView implements HasSavedKernels {

    private final ObjectMapper objectMapper;
    private final KernelService kernelService;

    private final TreeGrid<SavedNotebook> savedGrid = new TreeGrid<>();

    public SaveView(ObjectMapper objectMapper, KernelService kernelService) {
        this.objectMapper = objectMapper;
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
        add(createLeft());
        add(createRight());
    }

    private Component createLeft() {
        H2 title = new H2("Your Notebooks");
        title.addClassNames(STYLE_PADDING_M);
        Div div = new Div(title);
        div.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL, STYLE_BACKGROUND_WHITE);
        return div;
    }

    private Component createRight() {
        savedGrid.addComponentHierarchyColumn(nb -> {
            CompetitionEntity competition = nb.getCompetition();
            if (competition != null) {
                return new TitleLink(competition);
            }
            return new TitleLink(nb.getKernel());
        })
            .setHeader("Competition / Notebook Title");
        savedGrid.addColumn(SavedNotebook::getNumNotebooks)
            .setHeader("# Notebooks");
        savedGrid.addComponentColumn(this::createDownload)
            .setHeader("Download");

        savedGrid.setEmptyStateText("Loading saved kernels...");
        savedGrid.setHeightFull();

        getSavedKernels(this::processSavedKernels);

        H2 title = new H2("Saved Notebooks");
        title.addClassNames(STYLE_PADDING_S);
        Div div = new Div(title, savedGrid);
        div.addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL, STYLE_BACKGROUND_WHITE);
        return div;
    }

    private Component createDownload(SavedNotebook nb) {
        KernelEntity kernel = nb.getKernel();
        if (kernel == null) {
            return new Div();
        }
        Icon download = VaadinIcon.DOWNLOAD.create();
        Anchor downloadLink = new Anchor();
        downloadLink.setHref(event -> {
            ServiceResponse<KernelEntity> response = kernelService.fetchById(kernel.getId(), KernelLoadType.WITH_CELLS);
            KernelEntity k = response.getEntity().orElseThrow();
            event.setFileName(k.getId() + ".ipynb");
            event.getOutputStream().write(objectMapper.writeValueAsBytes(Notebook.ofKernel(k)));
        });
        downloadLink.add(download);
        return downloadLink;
    }

    private void processSavedKernels(Set<Long> savedKernelIds) {
        KernelFilter filter = new KernelFilter();
        filter.setIds(savedKernelIds);
        ServiceResponse<PageImpl<KernelEntity>> kernelResponse = kernelService.fetch(Pageable.unpaged(), filter, KernelLoadType.WITH_COMPETITION);
        kernelResponse.getEntity()
            .map(PageImpl::stream)
            .map(s -> s
                .collect(Collectors.groupingBy(KernelEntity::getCompetition))
                .entrySet()
                .stream()
                .map(e -> {
                    List<SavedNotebook> children = e.getValue().stream().map(SavedNotebook::new).toList();
                    return new SavedNotebook(e.getKey(), children);
                })
                .toList())
            .ifPresentOrElse(items -> savedGrid.setItems(items, SavedNotebook::getChildren),
                () -> savedGrid.setEmptyStateText("No kernels found"));
    }
}
