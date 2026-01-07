package ch.ethz.inf.peachlab.model.entity;

import jakarta.persistence.MappedSuperclass;

import java.io.Serializable;


public interface AbstractEntity extends Serializable {

    public abstract Long getId();

    public abstract void setId(Long id);
}
