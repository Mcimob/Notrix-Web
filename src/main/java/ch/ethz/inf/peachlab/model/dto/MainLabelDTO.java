package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.enums.LabelCategory;
import ch.ethz.inf.peachlab.model.enums.MainLabel;

import java.util.function.Function;

public record MainLabelDTO(
        int id,
        String name,
        String groupName,
        String color,
        String stroke) {

    public static MainLabelDTO ofLabel(MainLabel label, Function<String, String> translator) {
        return new MainLabelDTO(
                label.ordinal(),
                translator.apply(label.getTitleKey()),
                translator.apply(label.getLabelCategory().getTitleKey()),
                label.getColor(),
                label.getLabelCategory() == LabelCategory.DATA_ORIENTED
                        || label.getLabelCategory() == LabelCategory.MODEL_ORIENTED
                        ? "#666" : "none"
        );
    }
}
