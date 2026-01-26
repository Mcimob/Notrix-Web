package ch.ethz.inf.peachlab.ui.components;

import ch.ethz.inf.peachlab.model.dto.LabelCountDTO;
import ch.ethz.inf.peachlab.model.dto.MainLabelDTO;
import ch.ethz.inf.peachlab.model.enums.MainLabel;
import com.vaadin.flow.component.Tag;
import com.vaadin.flow.component.dependency.JsModule;
import com.vaadin.flow.component.react.ReactAdapterComponent;

import java.util.Arrays;
import java.util.Comparator;
import java.util.Map;

@JsModule("./src/react/sidebar/transition-sidebar-element.tsx")
@Tag("transition-sidebar")
public class TransitionSidebarReact extends ReactAdapterComponent {

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
