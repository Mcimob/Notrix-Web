package ch.ethz.inf.peachlab.ui.webstorage;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.backend.service.ServiceResponse;
import ch.ethz.inf.peachlab.backend.service.db.UploadedKernelService;
import ch.ethz.inf.peachlab.model.dto.ProcessingNotebook;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

public interface ManagesProcessingNotebooks extends HasWebStorage {

    String PROCESSING_NOTEBOOKS = "processingNotebooks";
    String UPLOADED_NOTEBOOKS = "uploadedNotebooks";

    default void getProcessingNotebooks(Consumer<Map<String, ProcessingNotebook>> consumer) {
        getProcessingNotebooks(consumer, e -> {});
    }

    default void getProcessingNotebooks(Consumer<Map<String, ProcessingNotebook>> consumer, Consumer<JsonProcessingException> exceptionConsumer) {
        getItem(consumer, exceptionConsumer, new TypeReference<Map<String, ProcessingNotebook>>() {}, PROCESSING_NOTEBOOKS, new HashMap<>());
    }

    default void setProcessingNotebooks(Map<String, ProcessingNotebook> processingNotebooks) {
        setProcessingNotebooks(processingNotebooks, e -> {});
    }

    default void setProcessingNotebooks(Map<String, ProcessingNotebook> processingNotebooks, Consumer<JsonProcessingException> exceptionConsumer) {
        setItem(processingNotebooks, exceptionConsumer, PROCESSING_NOTEBOOKS);
    }


    default void getUploadedNotebooks(Consumer<Map<Long, Set<String>>> consumer) {
        getUploadedNotebooks(consumer, e -> {});
    }

    default void getUploadedNotebooks(Consumer<Map<Long, Set<String>>> consumer, Consumer<JsonProcessingException> exceptionConsumer) {
        getItem(consumer, exceptionConsumer, new TypeReference<Map<Long, Set<String>>>() {}, UPLOADED_NOTEBOOKS, new HashMap<>());
    }

    default void setUploadedNotebooks(Map<Long, Set<String>> uploadedNotebooks) {
        setUploadedNotebooks(uploadedNotebooks, e -> {});
    }

    default void setUploadedNotebooks(Map<Long, Set<String>> uploadedNotebooks, Consumer<JsonProcessingException> exceptionConsumer) {
        setItem(uploadedNotebooks, exceptionConsumer, UPLOADED_NOTEBOOKS);
    }


    default void onNotebooksProcessingDone(String identifier) {
        UploadedKernelService kernelService = SpringContext.getBean(UploadedKernelService.class);
        getProcessingNotebooks(nbs -> {
            ProcessingNotebook nb = nbs.get(identifier);

            ServiceResponse<UploadedKernelEntity> response = kernelService.fetchById(identifier);
            if (response.getEntity().isEmpty()) {
                showErrorNotification("Could not find notebook in database. Please check your uploaded notebooks under the 'Save' page");
                return;
            }
            UploadedKernelEntity kernel = response.getEntity().get();
            kernel.setTitle(nb.name());
            kernel.setSourceCompetitionId(nb.competitionId());
            kernelService.save(kernel);

            getUploadedNotebooks(uploadedNbs -> {
                uploadedNbs.putIfAbsent(nb.competitionId(), new HashSet<>());
                uploadedNbs.get(kernel.getSourceCompetitionId()).add(kernel.getId());
                setUploadedNotebooks(uploadedNbs);
            });

            nbs.remove(identifier);
            setProcessingNotebooks(nbs);

            showSuccessNotification("Your notebook {0} is finished processing. You can take a look at it on the 'Saved' page", nb.name());
        });
    }
}
