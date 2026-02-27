package ch.ethz.inf.peachlab.model.entity;

import java.io.Serializable;


public interface AbstractEntity<ID> extends Serializable {

    ID getId();

    void setId(ID id);
}
