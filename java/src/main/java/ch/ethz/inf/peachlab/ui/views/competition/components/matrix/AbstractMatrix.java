package ch.ethz.inf.peachlab.ui.views.competition.components.matrix;

import ch.ethz.inf.peachlab.model.dto.SimpleMainLabelDTO;
import ch.ethz.inf.peachlab.model.enums.MainLabel;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.react.ReactAdapterComponent;

import java.io.Serial;
import java.util.Arrays;
import java.util.List;

public abstract class AbstractMatrix<T, D> extends ReactAdapterComponent {

    @Serial
    private static final long serialVersionUID = -3990065382783900976L;

    protected AbstractMatrix() {
        setState("labelData", Arrays.stream(MainLabel.values())
            .map(l -> SimpleMainLabelDTO.ofMainLabel(l, this::getTranslation))
            .toArray());
        getStyle().set("--display-md", "none");
        getStyle().set("--cell-height", "5px");
    }

    public void addItems(List<T> newItems) {
        List<D> newItemsDTO = newItems.stream()
            .map(this::transformItem)
            .toList();
        try {
            getElement().executeJs(
                "this.dispatchEvent(new CustomEvent('append-items', { detail: $0 }))",
                new ObjectMapper().writeValueAsString(newItemsDTO));
        } catch (JsonProcessingException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract D transformItem(T item);

    public void setTotalItems(long totalItems) {
        setState("totalItems", totalItems);
    }

    public void addKernelClickedListener(ComponentEventListener<KernelClickEvent> listener) {
        addListener(KernelClickEvent.class, listener);
    }

    public void addLoadMoreClickedListener(ComponentEventListener<LoadMoreClickEvent> listener) {
        addListener(LoadMoreClickEvent.class, listener);
    }
}
