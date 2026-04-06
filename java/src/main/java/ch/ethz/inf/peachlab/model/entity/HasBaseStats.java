package ch.ethz.inf.peachlab.model.entity;

import java.util.Collection;
import java.util.List;

public interface HasBaseStats {

    Double getLines();

    Double getNumCells();

    Double getVotes();

    default Collection<HasBaseStats> getChildren() {
        return List.of();
    }
}
