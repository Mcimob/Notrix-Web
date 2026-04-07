package ch.ethz.inf.peachlab.ui.views.competition.components.matrix;

import ch.ethz.inf.peachlab.model.dto.KernelDTO;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import com.vaadin.flow.component.AttachEvent;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

import java.io.Serial;

@Tag("notebook-matrix")
@JsModule("./src/notebook-matrix.js")
@JsModule("./src/react/matrix/notebook-matrix.tsx")
@NpmPackage(value = "react-window", version="1.8.9")
@NpmPackage(value = "@types/react-window", version="1.8.8", dev = true)
@NpmPackage(value = "react-virtualized-auto-sizer", version="2.0.2")
public class NotebookMatrix extends AbstractMatrix<HasKernelData<?, ?, ?>, KernelDTO> {

    @Serial
    private static final long serialVersionUID = -3537825270654601440L;

    @Override
    protected KernelDTO transformItem(HasKernelData<?, ?, ?> item) {
        return KernelDTO.ofKernel(item);
    }

    public void clearItems() {
        getElement().executeJs("this.dispatchEvent(new CustomEvent('clear-items', {}))");
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
