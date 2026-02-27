package ch.ethz.inf.peachlab.model.entity;

import java.util.Collection;

public interface HasBaseStats {

    Double getLines();

    Double getNumCells();

    Double getVotes();

    Collection<HasBaseStats> getChildren();
}
