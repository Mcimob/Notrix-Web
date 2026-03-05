package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.enums.MainLabel;

import java.util.function.Function;

public record SimpleMainLabelDTO(
        int id,
        String title
) {

    public static SimpleMainLabelDTO ofMainLabel(MainLabel label, Function<String, String> translator) {
        return new SimpleMainLabelDTO(label.ordinal(), translator.apply(label.getTitleKey()));
    }
}
