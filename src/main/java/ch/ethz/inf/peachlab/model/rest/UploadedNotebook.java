package ch.ethz.inf.peachlab.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UploadedNotebook(List<UploadedCell> cells) {

    public record UploadedCell(@JsonProperty("cell_type") String cellType, List<String> source) {}
}
