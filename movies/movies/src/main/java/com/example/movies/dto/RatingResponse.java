package com.example.movies.dto;

import com.fasterxml.jackson.annotation.JsonProperty;



public class RatingResponse {
    @JsonProperty("Source")
    private String source;

    @JsonProperty("Value")
    private String value;

    // Getters and Setters

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
