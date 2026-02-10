package ch.ethz.inf.peachlab.model.entity;

import java.util.Collection;

public interface HasKernelData {

    Double getLines();

    Double getNumCells();

    Double getVotes();

    Collection<HasKernelData> getChildren();
}
