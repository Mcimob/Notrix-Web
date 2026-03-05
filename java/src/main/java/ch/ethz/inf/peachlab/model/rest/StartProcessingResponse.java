package ch.ethz.inf.peachlab.model.rest;

import com.fasterxml.jackson.annotation.JsonProperty;

public record StartProcessingResponse(@JsonProperty("job_id") String jobId) {
}
