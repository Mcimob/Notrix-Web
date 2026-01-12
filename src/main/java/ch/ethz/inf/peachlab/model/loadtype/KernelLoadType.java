package ch.ethz.inf.peachlab.model.loadtype;

import ch.ethz.inf.peachlab.model.entity.KernelEntity;

public enum KernelLoadType implements HasLoadType{

    WITH_CELLS(KernelEntity.WITH_CELLS);

    private final String name;

    KernelLoadType(String name) {
        this.name = name;
    }

    @Override
    public String getName() {
        return name;
    }
}
