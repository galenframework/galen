package com.galenframework.actions;

import com.galenframework.utils.GalenUtils;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.awt.*;
import java.util.List;

import static com.galenframework.actions.ArgumentsUtils.convertTags;
import static java.util.Arrays.asList;

public class GalenActionMutateArguments {
    private List<String> paths;
    private List<String> includedTags;
    private List<String> excludedTags;
    private String url;
    private Dimension screenSize;
    private String javascript;
    private String config;

    public static GalenActionMutateArguments parse(String[] args) {
        args= ArgumentsUtils.processSystemProperties(args);

        Options options = new Options();
        options.addOption("i", "include", true, "Tags for sections that should be included in test run");
        options.addOption("e", "exclude", true, "Tags for sections that should be excluded from test run");
        options.addOption("u", "url", true, "Initial test url");
        options.addOption("s", "size", true, "Browser window size");
        options.addOption("J", "javascript", true, "JavaScript code that should be executed before checking layout");
        options.addOption("c", "config", true, "Path to config");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (MissingArgumentException e) {
            throw new IllegalArgumentException("Missing value for " + e.getOption().getLongOpt(), e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        GalenActionMutateArguments arguments = new GalenActionMutateArguments();
        arguments.setUrl(cmd.getOptionValue("u"));
        arguments.setScreenSize(GalenUtils.readSize(cmd.getOptionValue("s")));
        arguments.setJavascript(cmd.getOptionValue("J"));
        arguments.setIncludedTags(convertTags(cmd.getOptionValue("i")));
        arguments.setExcludedTags(convertTags(cmd.getOptionValue("e")));
        arguments.setPaths(asList(cmd.getArgs()));
        arguments.setConfig(cmd.getOptionValue("c"));

        if (arguments.getPaths().isEmpty()) {
            throw new IllegalArgumentException("Missing spec files");
        }

        return arguments;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public void setIncludedTags(List<String> includedTags) {
        this.includedTags = includedTags;
    }

    public List<String> getExcludedTags() {
        return excludedTags;
    }

    public void setExcludedTags(List<String> excludedTags) {
        this.excludedTags = excludedTags;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
    }

    public String getConfig() {
        return config;
    }

    public void setConfig(String config) {
        this.config = config;
    }

    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }
}
