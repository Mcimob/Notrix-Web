package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.KernelEntity;
import ch.ethz.inf.peachlab.model.enums.MainLabel;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public record KernelDTO(
    Long id,
    String title,
    String currentUrlSlug,
    Integer[] labelSequence,
    CellDTO[] cells
) {

    public static KernelDTO ofKernel(KernelEntity kernel) {
        return new KernelDTO(
            kernel.getId(),
            kernel.getTitle(),
            kernel.getCurrentUrlSlug(),
            Arrays.stream(Optional.ofNullable(kernel.getLabelSequence()).orElse(new MainLabel[]{}))
                .map(MainLabel::ordinal)
                .toArray(Integer[]::new),
            kernel.getCells().stream()
                .filter(Objects::nonNull)
                .map(CellDTO::ofCell)
                .toArray(CellDTO[]::new)
        );
    }
}
