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
    Integer[] labelSequenceWithMd,
    Integer[] lengthSequence,
    boolean isUploaded
) {

    public static KernelDTO ofKernel(HasKernelData<?, ?, ?> kernel) {
        return new KernelDTO(
            kernel.getId().toString(),
            kernel.getTitle(),
            kernel.getCurrentUrlSlug(),
            Optional.ofNullable(kernel.getLabelSequenceWithMd()).orElse(new Integer[]{}),
            Optional.ofNullable(kernel.getLengthSequence()).orElse(new Integer[]{}),
            kernel instanceof UploadedKernelEntity
        );
    }
}
