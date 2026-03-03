package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.model.dto.KernelDTO;
import ch.ethz.inf.peachlab.model.dto.SimpleMainLabelDTO;
import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.enums.MainLabel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.react.ReactAdapterComponent;
import com.vaadin.flow.function.SerializableConsumer;

import java.io.Serial;
import java.util.Arrays;

@Tag("cell-column-element")
@JsModule("./src/react/matrix/cell-column-element.tsx")
public class CellColumn extends ReactAdapterComponent {

    @Serial
    private static final long serialVersionUID = 5247624746801620528L;

    public CellColumn() {
        setState("labelData", Arrays.stream(MainLabel.values())
            .map(l -> SimpleMainLabelDTO.ofMainLabel(l, this::getTranslation))
            .toArray());
        getStyle().set("--display-md", "block");
        getStyle().set("--cell-height", "5px");
    }

    public void setKernel(HasKernelData<?, ?, ?> kernel) {
        setState("kernel", KernelDTO.ofKernel(kernel));
    }

    public void addCellClickListener(SerializableConsumer<Integer> listener) {
        addStateChangeListener("clickedCellIndex", Integer.class, listener);
    }
}
