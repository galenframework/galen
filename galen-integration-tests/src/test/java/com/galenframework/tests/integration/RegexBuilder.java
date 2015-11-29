package com.galenframework.tests.integration;

public class RegexBuilder {
    StringBuilder builder = new StringBuilder();

    public RegexBuilder exact(String string) {
        builder.append("\\Q").append(string).append("\\E");
        return this;
    }


    @Override
    public String toString() {
        return builder.toString();
    }

    public RegexBuilder digits() {
        builder.append("[0-9]+");
        return this;
    }

    public RegexBuilder digits(int times) {
        builder.append("[0-9]{").append(times).append("}");
        return this;
    }

    public static RegexBuilder regex() {
        return new RegexBuilder();
    }
}
