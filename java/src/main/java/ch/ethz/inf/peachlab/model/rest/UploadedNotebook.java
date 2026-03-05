package ch.ethz.inf.peachlab.model.rest;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record UploadedNotebook(List<UploadedCell> cells, String title) {

    public record UploadedCell(@JsonProperty("cell_type") String cellType,
                               @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY) List<String> source) {}
}
