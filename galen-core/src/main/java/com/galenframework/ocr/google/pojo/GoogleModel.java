
package com.galenframework.ocr.google.pojo;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class GoogleModel {

    public List<Response> responses;

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Response {
        public FullTextAnnotation fullTextAnnotation;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class FullTextAnnotation {
        public String text;
    }
}
