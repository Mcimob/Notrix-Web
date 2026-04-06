package ch.ethz.inf.peachlab.ui.views.competition.components.matrix;

import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.model.dto.KernelDTO;
import ch.ethz.inf.peachlab.model.dto.SimpleMainLabelDTO;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.enums.MainLabel;
import ch.ethz.inf.peachlab.ui.views.HasNotification;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.react.ReactAdapterComponent;

import java.io.Serial;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Tag("notebook-matrix")
@JsModule("./src/notebook-matrix.js")
@JsModule("./src/react/matrix/notebook-matrix.tsx")
@NpmPackage(value = "react-window", version="1.8.9")
@NpmPackage(value = "@types/react-window", version="1.8.8", dev = true)
@NpmPackage(value = "react-virtualized-auto-sizer", version="2.0.2")
public class NotebookMatrix extends ReactAdapterComponent implements HasLogger, HasNotification {

    @Serial
    private static final long serialVersionUID = -3537825270654601440L;

    private final List<HasKernelData<?, ?, ?>> items = new ArrayList<>();

    public NotebookMatrix() {
        setState("labelData", Arrays.stream(MainLabel.values())
                .map(l -> SimpleMainLabelDTO.ofMainLabel(l, this::getTranslation))
                .toArray());
        getStyle().set("--display-md", "none");
        getStyle().set("--cell-height", "5px");
    }

    public void addItems(List<HasKernelData<?, ?, ?>> newItems) {
        items.addAll(newItems);
        setState("items", items.stream()
            .map(KernelDTO::ofKernel)
            .toList());
    }

    public void clearItems() {
        items.clear();
        setState("items", List.of());
    }

    public void setTotalItems(long totalItems) {
        setState("totalItems", totalItems);
    }

    public void addKernelClickedListener(ComponentEventListener<KernelClickEvent> listener) {
        addListener(KernelClickEvent.class, listener);
    }

    public void addLoadMoreClickedListener(ComponentEventListener<LoadMoreClickEvent> listener) {
        addListener(LoadMoreClickEvent.class, listener);
    }

    @Override
    protected void onAttach(AttachEvent attachEvent) {
        super.onAttach(attachEvent);

        getElement().executeJs(
                "window.attachNotebookMatrixHover($0, $1)",
                getElement(),
                "kernel-grid"
        );
    }
}
