package ch.ethz.inf.peachlab.model.loadtype;

import ch.ethz.inf.peachlab.model.entity.ClusterEntity;

public enum ClusterLoadType implements HasLoadType {

    WITH_KERNELS_AND_CELLS(ClusterEntity.WITH_KERNELS_AND_CELLS);

    private final String name;

    ClusterLoadType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
