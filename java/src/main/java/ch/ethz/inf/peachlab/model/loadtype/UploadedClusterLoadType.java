package ch.ethz.inf.peachlab.model.loadtype;

import ch.ethz.inf.peachlab.model.entity.UploadedClusterEntity;

public enum UploadedClusterLoadType implements HasLoadType {

    WITH_KERNELS_AND_CELLS(UploadedClusterEntity.WITH_KERNELS_AND_CELLS_UPLOADED);

    private final String name;

    UploadedClusterLoadType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
