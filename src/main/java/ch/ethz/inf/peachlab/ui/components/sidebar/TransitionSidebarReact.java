package ch.ethz.inf.peachlab.ui.components.sidebar;

import ch.ethz.inf.peachlab.model.dto.LabelCountDTO;
import ch.ethz.inf.peachlab.model.dto.MainLabelDTO;
import ch.ethz.inf.peachlab.model.enums.MainLabel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.react.ReactAdapterComponent;

import java.io.Serial;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_ALIGN_CENTER;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_FLEX_COLUMN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_HEIGHT_FULL;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_OVERFLOW_HIDDEN;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_PADDING_M;
import static ch.ethz.inf.peachlab.ui.DesignConstants.STYLE_WIDTH_FULL;

@JsModule("./src/react/sidebar/transition-sidebar-element.tsx")
@Tag("transition-sidebar")
public class TransitionSidebarReact extends ReactAdapterComponent {

    @Serial
    private static final long serialVersionUID = 8743351204762739076L;

    public TransitionSidebarReact() {
        initStyles();
    }

    private void initStyles() {
        addClassNames(STYLE_HEIGHT_FULL, STYLE_WIDTH_FULL,
            STYLE_FLEX_COLUMN, STYLE_FLEX_ALIGN_CENTER,
            STYLE_PADDING_M, STYLE_OVERFLOW_HIDDEN);
    }

    public void setData(Map<MainLabel, Integer> stageFrequencies, Integer[][] transitionMatrix) {
        setState("data", Map.of(
                "stages", stageFrequencies.entrySet().stream()
                                .filter(e -> e.getKey().ordinal() <= MainLabel.DATA_EXPORT.ordinal())
                                .map(e -> new LabelCountDTO(e.getKey().ordinal(), e.getValue()))
                                .sorted(Comparator.comparing(LabelCountDTO::id))
                                .toList(),
                "transitions", transitionMatrix,
                "labels", Arrays.stream(MainLabel.values())
                        .map(l -> MainLabelDTO.ofLabel(l, this::getTranslation))
                        .toList()));
    }

    public void setOpacityTargets(String[] selectors) {
        setState("opacityTargets", selectors);
    }
}
