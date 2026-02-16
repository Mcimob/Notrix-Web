package ch.ethz.inf.peachlab.ui.views.kernel;

import ch.ethz.inf.peachlab.app.SpringContext;
import ch.ethz.inf.peachlab.logger.HasLogger;
import ch.ethz.inf.peachlab.ui.HasRender;
import ch.ethz.inf.peachlab.ui.HasSavedKernels;
import ch.ethz.inf.peachlab.ui.views.HasNotification;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.page.WebStorage;
import com.vaadin.flow.shared.Registration;

import java.util.Set;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_TEXT_LINK;

public class KernelSaver extends Div implements HasRender, HasLogger, HasNotification, HasSavedKernels {

    private Long kernelId;
    private boolean isSaved;

    private final ObjectMapper objectMapper;

    private final Icon bookmarkEmpty = VaadinIcon.BOOKMARK_O.create();
    private final Icon bookmarkFull = VaadinIcon.BOOKMARK.create();

    private Registration clickRegistration;

    public KernelSaver() {
        this.objectMapper = SpringContext.getBean(ObjectMapper.class);
        addClassNames(STYLE_TEXT_LINK);
    }

    @Override
    public void render() {
        removeAll();

        bookmarkEmpty.setSize("32px");
        bookmarkFull.setSize("32px");
        setSaved(false);

        add(new Div(bookmarkEmpty), new Div(bookmarkFull));
        clickRegistration = addClickListener(click -> getSavedKernels(this::onSavedKernels));
        initSaved();
    }

    private void onSavedKernels(Set<Long> savedKernels) {
        if (isSaved) {
            savedKernels.remove(kernelId);
        } else {
            savedKernels.add(kernelId);
        }
        try {
            WebStorage.setItem("savedKernels", objectMapper.writeValueAsString(savedKernels));
            setSaved(!isSaved);
        } catch (JsonProcessingException e) {
            getLogger().error("Could not write saved Notebooks", e);
            showErrorNotification("Could not save saved Notebooks");
        }
    }

    private void initSaved() {
        getSavedKernels(savedKernels -> setSaved(
            savedKernels.contains(kernelId)),
            e -> {
                clickRegistration.remove();
                removeClassNames(STYLE_TEXT_LINK);
        });
    }

    public void setKernelId(Long kernelId) {
        this.kernelId = kernelId;
    }

    public void setSaved(boolean saved) {
        this.isSaved = saved;
        bookmarkEmpty.setVisible(!isSaved);
        bookmarkFull.setVisible(isSaved);
    }
}
