package at.technikum_wien.app.controller;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;

public class Controller {
    private ObjectMapper objectMapper;

    public Controller() {
        this.objectMapper = new ObjectMapper();
        this.objectMapper.setSerializationInclusion(JsonInclude.Include.NON_NULL); // Set to exclude nulls globally
    }

    public ObjectMapper getObjectMapper() {
        return objectMapper;
    }
}
