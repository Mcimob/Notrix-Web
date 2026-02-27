package ch.ethz.inf.peachlab.model.dto;

import ch.ethz.inf.peachlab.model.entity.HasKernelData;
import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;
import ch.ethz.inf.peachlab.model.enums.MainLabel;

import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;

public record KernelDTO(
    String id,
    String title,
    String currentUrlSlug,
    Integer[] labelSequence,
    CellDTO[] cells,
    boolean isUploaded
) {

    public static KernelDTO ofKernel(HasKernelData<?, ?> kernel) {
        return new KernelDTO(
            kernel.getId().toString(),
            kernel.getTitle(),
            kernel.getCurrentUrlSlug(),
            Arrays.stream(Optional.ofNullable(kernel.getLabelSequence()).orElse(new MainLabel[]{}))
                .map(MainLabel::ordinal)
                .toArray(Integer[]::new),
            kernel.getCells().stream()
                .filter(Objects::nonNull)
                .map(CellDTO::ofCell)
                .toArray(CellDTO[]::new),
            kernel instanceof UploadedKernelEntity
        );
    }
}
