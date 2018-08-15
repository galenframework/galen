package com.galenframework.validation;

import com.galenframework.reports.model.LayoutMeta;

public class SimpleValidationResult {
    private final String error;
    private final LayoutMeta meta;


    private SimpleValidationResult(String error, LayoutMeta meta) {
        this.error = error;
        this.meta = meta;
    }

    public static SimpleValidationResult success(LayoutMeta meta) {
        return new SimpleValidationResult(null, meta);
    }

    public static SimpleValidationResult error(String error, LayoutMeta meta) {
       if (error == null)  {
           throw new IllegalArgumentException("error should not be null");
       }

       if (error.trim().isEmpty()) {
           throw new IllegalArgumentException("error should not be empty");
       }
       return new SimpleValidationResult(error, meta);
    }

    public LayoutMeta getMeta() {
        return meta;
    }


    public String getError() {
        return error;
    }

    public boolean isSuccessful() {
        return error == null;
    }

    public boolean isError() {
        return error != null;
    }
}
