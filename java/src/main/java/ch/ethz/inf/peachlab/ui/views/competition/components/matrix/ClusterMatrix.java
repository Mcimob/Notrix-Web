package ch.ethz.inf.peachlab.ui.views.competition.components.matrix;

import ch.ethz.inf.peachlab.model.dto.ClusterDTO;
import ch.ethz.inf.peachlab.model.entity.HasClusterData;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.dependency.NpmPackage;

import java.io.Serial;

@Tag("cluster-matrix")
@JsModule("./src/react/matrix/cluster-matrix.tsx")
@NpmPackage(value = "react-window", version="1.8.9")
@NpmPackage(value = "@types/react-window", version="1.8.8", dev = true)
@NpmPackage(value = "react-virtualized-auto-sizer", version="2.0.2")
public class ClusterMatrix extends AbstractMatrix<HasClusterData<?, ?>, ClusterDTO> {

    @Serial
    private static final long serialVersionUID = -5703944031143879709L;

    @Override
    protected ClusterDTO transformItem(HasClusterData<?, ?> item) {
        return ClusterDTO.ofCluster(item);
    }

    public void addClusterClickedListener(ComponentEventListener<ClusterClickEvent> listener) {
        addListener(ClusterClickEvent.class, listener);
    }
}
