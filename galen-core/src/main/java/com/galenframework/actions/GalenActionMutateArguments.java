/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
package com.galenframework.actions;

import com.galenframework.suite.actions.mutation.MutationOptions;
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
    private String htmlReport;
    private String jsonReport;
    private String testngReport;
    private String junitReport;
    private MutationOptions mutationOptions = new MutationOptions();

    public static GalenActionMutateArguments parse(String[] args) {
        args = ArgumentsUtils.processSystemProperties(args);

        Options options = new Options();
        options.addOption("i", "include", true, "Tags for sections that should be included in test run");
        options.addOption("e", "exclude", true, "Tags for sections that should be excluded from test run");
        options.addOption("u", "url", true, "Initial test url");
        options.addOption("s", "size", true, "Browser window size");
        options.addOption("o", "offset", true, "Offset for each mutation (default 5)");
        options.addOption("h", "htmlreport", true, "Path for html output report");
        options.addOption("j", "jsonreport", true, "Path for json report");
        options.addOption("g", "testngreport", true, "Path for testng xml report");
        options.addOption("x", "junitreport", true, "Path for junit xml report");
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
        arguments.setHtmlReport(cmd.getOptionValue("h"));
        arguments.setJsonReport(cmd.getOptionValue("j"));
        arguments.setTestngReport(cmd.getOptionValue("g"));
        arguments.setJunitReport(cmd.getOptionValue("x"));
        arguments.setIncludedTags(convertTags(cmd.getOptionValue("i")));
        arguments.setExcludedTags(convertTags(cmd.getOptionValue("e")));
        arguments.setPaths(asList(cmd.getArgs()));
        arguments.setConfig(cmd.getOptionValue("c"));
        arguments.getMutationOptions().setPositionOffset(Integer.parseInt(cmd.getOptionValue("o", "5")));

        if (arguments.getPaths().isEmpty()) {
            throw new IllegalArgumentException("Missing spec files");
        }

        return arguments;
    }

    public List<String> getPaths() {
        return paths;
    }

    public GalenActionMutateArguments setPaths(List<String> paths) {
        this.paths = paths;
        return this;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public GalenActionMutateArguments setIncludedTags(List<String> includedTags) {
        this.includedTags = includedTags;
        return this;
    }

    public List<String> getExcludedTags() {
        return excludedTags;
    }

    public GalenActionMutateArguments setExcludedTags(List<String> excludedTags) {
        this.excludedTags = excludedTags;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public GalenActionMutateArguments setUrl(String url) {
        this.url = url;
        return this;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public GalenActionMutateArguments setScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
        return this;
    }

    public String getConfig() {
        return config;
    }

    public GalenActionMutateArguments setConfig(String config) {
        this.config = config;
        return this;
    }

    public String getJavascript() {
        return javascript;
    }

    public GalenActionMutateArguments setJavascript(String javascript) {
        this.javascript = javascript;
        return this;
    }

    public GalenActionMutateArguments setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public GalenActionMutateArguments setJsonReport(String jsonReport) {
        this.jsonReport = jsonReport;
        return this;
    }

    public String getJsonReport() {
        return jsonReport;
    }

    public GalenActionMutateArguments setTestngReport(String testngReport) {
        this.testngReport = testngReport;
        return this;
    }

    public String getTestngReport() {
        return testngReport;
    }

    public GalenActionMutateArguments setJunitReport(String junitReport) {
        this.junitReport = junitReport;
        return this;
    }

    public String getJunitReport() {
        return junitReport;
    }

    public MutationOptions getMutationOptions() {
        return mutationOptions;
    }

    public GalenActionMutateArguments setMutationOptions(MutationOptions mutationOptions) {
        this.mutationOptions = mutationOptions;
        return this;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("paths", paths)
            .append("includedTags", includedTags)
            .append("excludedTags", excludedTags)
            .append("url", url)
            .append("screenSize", screenSize)
            .append("javascript", javascript)
            .append("config", config)
            .append("htmlReport", htmlReport)
            .append("jsonReport", jsonReport)
            .append("testngReport", testngReport)
            .append("junitReport", junitReport)
            .append("mutationOptions", mutationOptions)
            .toString();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GalenActionMutateArguments that = (GalenActionMutateArguments) o;

        return new EqualsBuilder()
            .append(paths, that.paths)
            .append(includedTags, that.includedTags)
            .append(excludedTags, that.excludedTags)
            .append(url, that.url)
            .append(screenSize, that.screenSize)
            .append(javascript, that.javascript)
            .append(config, that.config)
            .append(htmlReport, that.htmlReport)
            .append(jsonReport, that.jsonReport)
            .append(testngReport, that.testngReport)
            .append(junitReport, that.junitReport)
            .append(mutationOptions, that.mutationOptions)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(paths)
            .append(includedTags)
            .append(excludedTags)
            .append(url)
            .append(screenSize)
            .append(javascript)
            .append(config)
            .append(htmlReport)
            .append(jsonReport)
            .append(testngReport)
            .append(junitReport)
            .append(mutationOptions)
            .toHashCode();
    }
}
