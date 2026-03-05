package ch.ethz.inf.peachlab.ui.views.competition;

import ch.ethz.inf.peachlab.backend.broadcaster.ProcessedNotebookBroadcaster;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.ClusterService;
import ch.ethz.inf.peachlab.backend.service.db.CompetitionService;
import ch.ethz.inf.peachlab.backend.service.db.KernelService;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.backend.service.rest.NotebookProcessingService;
import ch.ethz.inf.peachlab.model.dto.ProcessingNotebook;
import ch.ethz.inf.peachlab.model.entity.ClusterEntity;
import ch.ethz.inf.peachlab.model.entity.CompetitionEntity;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.filter.ClusterFilter;
import ch.ethz.inf.peachlab.model.filter.CompetitionFilter;
import ch.ethz.inf.peachlab.model.filter.KernelFilter;
import ch.ethz.inf.peachlab.model.filter.UploadedKernelFilter;
import ch.ethz.inf.peachlab.model.loadtype.ClusterLoadType;
import ch.ethz.inf.peachlab.model.loadtype.HasLoadType;
import ch.ethz.inf.peachlab.model.loadtype.KernelLoadType;
import ch.ethz.inf.peachlab.model.loadtype.UploadedKernelLoadType;
import ch.ethz.inf.peachlab.ui.MainLayout;
import ch.ethz.inf.peachlab.ui.webstorage.ManagesProcessingNotebooks;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.component.upload.Upload;
import com.vaadin.flow.component.upload.UploadI18N;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.streams.InMemoryUploadHandler;
import com.vaadin.flow.server.streams.UploadHandler;
import org.springframework.data.domain.Pageable;

import java.io.Serial;
import java.util.HashSet;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_BACKGROUND_WHITE;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;

@Route(value = "competitions", layout = MainLayout.class)
public class CompetitionView extends AbstractCompetitionView<CompetitionEntity, KernelEntity, ClusterEntity, KernelFilter, ClusterFilter, CompetitionFilter, Long> implements ManagesProcessingNotebooks {

    @Serial
    private static final long serialVersionUID = 8520612132652236410L;

    private final transient NotebookProcessingService nbProcessingService;
    private final transient UploadedKernelService uploadedKernelService;

    private byte[] uploadedData;

    public CompetitionView(CompetitionService competitionService,
                           KernelService kernelService,
                           ClusterService clusterService,
                           NotebookProcessingService nbProcessingService, UploadedKernelService uploadedKernelService) {
        super(competitionService, kernelService, clusterService, new KernelFilter(), new ClusterFilter());
        this.nbProcessingService = nbProcessingService;
        this.uploadedKernelService = uploadedKernelService;
    }

    @Override
    protected HasLoadType getKernelLoadType() {
        return KernelLoadType.WITH_CELLS;
    }

    @Override
    protected HasLoadType getClusterLoadType() {
        return ClusterLoadType.WITH_KERNELS_AND_CELLS;
    }

    @Override
    protected ServiceResponse<? extends HasKernelData<?, ?, ?>> getKernelResponse(String stringId) {
        try {
            long longId = Long.parseLong(stringId);
            return kernelService.fetchById(longId);
        } catch (Exception e) {
            return uploadedKernelService.fetchById(stringId);
        }
    }

    @Override
    protected Long parseId(String stringId) {
        return Long.parseLong(stringId);

    }

    @Override
    protected Component createUpload() {
        if (competition instanceof CompetitionEntity comp) {
            Dialog titleDialog = new Dialog();
            titleDialog.setHeaderTitle("Upload own solution");
            TextField titleField = new TextField("Title");
            titleDialog.add(new Div("Please give your submission a title"), titleField);

            Dialog.DialogFooter footer = titleDialog.getFooter();
            Button cancel = new Button("Cancel", e -> titleDialog.close());
            cancel.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
            Button confirm = new Button("Confirm", e -> {
                titleDialog.close();
                ServiceResponse<String> response = nbProcessingService.startNotebookProcessing(uploadedData);
                response.getErrorMessages().forEach(this::showErrorNotification);
                response.getEntity().ifPresent(identifier -> {
                    ProcessedNotebookBroadcaster.registerNotebookListener(this::onNotebooksProcessingDone, identifier, UI.getCurrent());
                    getProcessingNotebooks(processingNotebooks -> {
                        processingNotebooks.put(identifier,
                            new ProcessingNotebook(titleField.getValue(), comp.getId()));
                        setProcessingNotebooks(processingNotebooks);
                        showSuccessNotification("Your notebook {0} was sent to processing. You can check its status on the 'Saved' page and will be notified, when processing completes", titleField.getValue());
                    });
                });
            });
            footer.add(cancel, confirm);

            InMemoryUploadHandler inMemoryHandler = UploadHandler
                .inMemory((metadata, data) -> {
                    this.uploadedData = data;
                    titleDialog.open();
                });
            Upload upload = new Upload(inMemoryHandler);

            upload.setAcceptedFileTypes("application/json", ".ipynb");
            upload.addFileRejectedListener(event ->
                showErrorNotification(event.getErrorMessage()));
            upload.setDropAllowed(true);
            upload.setMaxFiles(1);
            UploadI18N i18n = new UploadI18N();
            i18n.setAddFiles(new UploadI18N.AddFiles()
                .setOne("Upload own Solution..."));
            i18n.setDropFiles(new UploadI18N.DropFiles()
                .setOne("Drop Notebook here"));
            i18n.setError(new UploadI18N.Error()
                .setIncorrectFileType("The provided file does not have the correct format (Python notebook)"));

            upload.setI18n(i18n);

            Div div = new Div(upload);
            div.addClassNames(STYLE_PADDING_M, STYLE_BACKGROUND_WHITE);
            return div;
        }
        return new Div();
    }

    @Override
    public void render() {
        super.render();
        getUploadedNotebooks(nbs -> {
            UploadedKernelFilter uploadedKernelFilter = new UploadedKernelFilter();
            uploadedKernelFilter.setIds(nbs.getOrDefault(competition.getId(), new HashSet<>()));
            initData(() -> uploadedKernelService.fetch(Pageable.unpaged(), uploadedKernelFilter, UploadedKernelLoadType.WITH_CELLS));
        });
    }

    @Override
    protected ServiceResponse<CompetitionEntity> getInitResponse(String parameter) {
        CompetitionFilter filter = new CompetitionFilter();
        filter.setSlug(parameter);
        return competitionService.fetchOne(filter);
    }
}
