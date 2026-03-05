package ch.ethz.inf.peachlab.model.loadtype;

import ch.ethz.inf.peachlab.model.entity.UploadedKernelEntity;

public enum UploadedKernelLoadType implements HasLoadType{

    WITH_CELLS(UploadedKernelEntity.WITH_CELLS_UPLOADED),
    WITH_COMPETITION(UploadedKernelEntity.WITH_COMPETITION_UPLOADED);

    private final String name;

    UploadedKernelLoadType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
