package ch.ethz.inf.peachlab.ui.views.competition.matrix;

import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.model.dto.ClusterDTO;
import ch.ethz.inf.peachlab.model.dto.SimpleMainLabelDTO;
import ch.ethz.inf.peachlab.model.enums.MainLabel;
import ch.ethz.inf.peachlab.ui.views.HasNotification;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;
import com.vaadin.flow.component.react.ReactAdapterComponent;
import com.vaadin.flow.function.SerializableConsumer;

import java.util.Arrays;
import java.util.List;

@Tag("cluster-matrix")
@JsModule("./src/react/matrix/cluster-matrix.tsx")
@NpmPackage(value = "react-window", version="1.8.9")
@NpmPackage(value = "@types/react-window", version="1.8.8", dev = true)
@NpmPackage(value = "react-virtualized-auto-sizer", version="2.0.2")
public class ClusterMatrix extends ReactAdapterComponent implements HasLogger, HasNotification {

    public void setItems(List<ClusterDTO> items) {
        setState("items", items);
    }

    public ClusterMatrix() {
        setState("labelData", Arrays.stream(MainLabel.values())
            .map(l -> SimpleMainLabelDTO.ofMainLabel(l, this::getTranslation))
            .toArray());
        getStyle().set("--display-md", "none");
        getStyle().set("--cell-height", "5px");
    }

    public void addKernelClickedListener(SerializableConsumer<Long> listener) {
        addStateChangeListener("clickedKernelId", Long.class, listener);
    }

    public void addClusterClickedListener(SerializableConsumer<Long> listener) {
        addStateChangeListener("clickedClusterId", Long.class, listener);
    }
}
