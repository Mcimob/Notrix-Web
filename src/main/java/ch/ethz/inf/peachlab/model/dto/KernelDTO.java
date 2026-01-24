package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.enums.MainLabel;

import java.util.Arrays;
import java.util.Objects;

public record KernelDTO(
        String title,
        String currentUrlSlug,
        Integer[] labelSequence,
        CellDTO[] cells
) {

    public static KernelDTO ofKernel(KernelEntity kernel) {
        return new KernelDTO(
            kernel.getTitle(),
            kernel.getCurrentUrlSlug(),
            Arrays.stream(kernel.getLabelSequence())
                    .map(MainLabel::ordinal)
                    .toArray(Integer[]::new),
            kernel.getCells().stream()
                    .filter(Objects::nonNull)
                    .map(CellDTO::ofCell)
                    .toArray(CellDTO[]::new)
        );
    }
}
