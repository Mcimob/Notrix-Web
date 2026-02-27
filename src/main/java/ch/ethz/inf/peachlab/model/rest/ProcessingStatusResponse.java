package ch.ethz.inf.peachlab.model.rest;

import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;

public record ProcessingStatusResponse(ProcessingStatus status, UploadedKernelEntity result) {
}
