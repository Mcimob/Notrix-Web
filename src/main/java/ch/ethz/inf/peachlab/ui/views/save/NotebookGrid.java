package ch.ethz.inf.peachlab.ui.views.save;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.KernelService;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.model.Notebook;
import ch.ethz.inf.peachlab.model.dto.SavedNotebook;
import ch.ethz.inf.peachlab.model.entity.HasCompetitionData;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import ch.ethz.inf.peachlab.model.loadtype.UploadedKernelLoadType;
import ch.ethz.inf.peachlab.ui.components.TitleLink;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.html.Anchor;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.treegrid.TreeGrid;

import java.io.Serial;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FONT_SIZE_L;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FONT_WEIGHT_BOLD;

public class NotebookGrid extends TreeGrid<SavedNotebook> {

    @Serial
    private static final long serialVersionUID = -5162920712868282716L;
    private final ObjectMapper objectMapper;
    private final KernelService kernelService;
    private final UploadedKernelService uploadedKernelService;

    public NotebookGrid() {
        super();
        this.objectMapper = SpringContext.getBean(ObjectMapper.class);
        this.kernelService = SpringContext.getBean(KernelService.class);
        this.uploadedKernelService = SpringContext.getBean(UploadedKernelService.class);
        createColumns();
        setHeightFull();
    }

    private void createColumns() {
        addComponentHierarchyColumn(nb -> {
                if (nb.getTitle() != null) {
                    Span span = new Span(nb.getTitle());
                    span.addClassNames(STYLE_FONT_SIZE_L, STYLE_FONT_WEIGHT_BOLD);
                    return span;
                }
                HasCompetitionData<?, ?, ?> competition = nb.getCompetition();
                if (competition != null) {
                    return TitleLink.ofCompetition(competition);
                }
                return TitleLink.ofKernel(nb.getKernel());
            })
            .setHeader("Competition / Notebook Title");
        addColumn(SavedNotebook::getNumNotebooks)
            .setHeader("# Notebooks");
        addComponentColumn(this::createDownload)
            .setHeader("Download");
    }

    private Component createDownload(SavedNotebook nb) {
        HasKernelData<?, ?, ?> kernel = nb.getKernel();
        if (kernel == null) {
            return new Div();
        }
        Icon download = VaadinIcon.DOWNLOAD.create();
        Anchor downloadLink = new Anchor();
        downloadLink.setHref(event -> {

            ServiceResponse<? extends HasKernelData<?, ?, ?>> response;
            if (kernel instanceof UploadedKernelEntity uploadedKernel) {
                response = uploadedKernelService.fetchById(uploadedKernel.getId(), UploadedKernelLoadType.WITH_CELLS);
            } else if (kernel instanceof KernelEntity kernelEntity) {
                response = kernelService.fetchById(kernelEntity.getId(), KernelLoadType.WITH_CELLS);
            } else {
                response = new ServiceResponse<>();
            }
            HasKernelData<?, ?, ?> k = response.getEntity().orElseThrow();
            event.setFileName(k.getId() + ".ipynb");
            event.getOutputStream().write(objectMapper.writeValueAsBytes(Notebook.ofKernel(k)));
        });
        downloadLink.add(download);
        return downloadLink;
    }
}
