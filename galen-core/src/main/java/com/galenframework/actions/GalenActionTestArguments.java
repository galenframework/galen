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

import org.apache.commons.cli.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;

import static com.galenframework.actions.ArgumentsUtils.convertTags;
import static java.util.Arrays.asList;

public class GalenActionTestArguments {

    private List<String> paths;
    private Boolean recursive = false;
    private List<String> includedTags = new LinkedList<>();
    private List<String> excludedTags = new LinkedList<>();
    private String htmlReport;
    private String testngReport;
    private String junitReport;
    private int parallelThreads = 0;
    private String filter;
    private String jsonReport;
    private List<String> groups;
    private List<String> excludedGroups;
    private String config;


    public static GalenActionTestArguments parse(String[] args) {
        args= ArgumentsUtils.processSystemProperties(args);

        Options options = new Options();
        options.addOption("i", "include", true, "Tags for sections that should be included in test run");
        options.addOption("e", "exclude", true, "Tags for sections that should be excluded from test run");
        options.addOption("h", "htmlreport", true, "Path for html output report");
        options.addOption("j", "jsonreport", true, "Path for json report");
        options.addOption("g", "testngreport", true, "Path for testng xml report");
        options.addOption("x", "junitreport", true, "Path for junit xml report");
        options.addOption("r", "recursive", false, "Flag for recursive tests scan");
        options.addOption("p", "parallel-tests", true, "Amount of tests to be run in parallel");
        options.addOption("P", "parallel-suites", true, "Amount of tests to be run in parallel");
        options.addOption("f", "filter", true, "Test filter");
        options.addOption("G", "groups", true, "Test groups");
        options.addOption("Q", "excluded-groups", true, "Excluded test groups");
        options.addOption("c", "config", true, "Path to galen config file");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (MissingArgumentException e) {
            throw new IllegalArgumentException("Missing value for " + e.getOption().getLongOpt(), e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        GalenActionTestArguments arguments = new GalenActionTestArguments();
        arguments.setIncludedTags(convertTags(cmd.getOptionValue("i", "")));
        arguments.setExcludedTags(convertTags(cmd.getOptionValue("e", "")));
        arguments.setTestngReport(cmd.getOptionValue("g"));
        arguments.setJunitReport(cmd.getOptionValue("x"));
        arguments.setRecursive(cmd.hasOption("r"));
        arguments.setHtmlReport(cmd.getOptionValue("h"));


        /*
        having this double check in order to have backwards compatibility with previous version
         in which the parallel tests used to be defined via --parallel-suites argument
         */
        if (cmd.hasOption("p")) {
            arguments.setParallelThreads(Integer.parseInt(cmd.getOptionValue("p", "0")));
        } else {
            arguments.setParallelThreads(Integer.parseInt(cmd.getOptionValue("P", "0")));
        }

        arguments.setFilter(cmd.getOptionValue("f"));
        arguments.setJsonReport(cmd.getOptionValue("j"));
        arguments.setGroups(convertTags(cmd.getOptionValue("G")));
        arguments.setExcludedGroups(convertTags(cmd.getOptionValue("Q")));
        arguments.setPaths(asList(cmd.getArgs()));
        arguments.setConfig(cmd.getOptionValue("c"));

        if (arguments.getPaths().isEmpty()) {
            throw new IllegalArgumentException("Missing test files");
        }
        return arguments;
    }

    public List<String> getPaths() {
        return paths;
    }

    public GalenActionTestArguments setPaths(List<String> paths) {
        this.paths = paths;
        return this;
    }

    public Boolean getRecursive() {
        return recursive;
    }

    public GalenActionTestArguments setRecursive(Boolean recursive) {
        this.recursive = recursive;
        return this;
    }

    public List<String> getExcludedGroups() {
        return excludedGroups;
    }

    public GalenActionTestArguments setExcludedGroups(List<String> excludedGroups) {
        this.excludedGroups = excludedGroups;
        return this;
    }

    public List<String> getGroups() {
        return groups;
    }

    public GalenActionTestArguments setGroups(List<String> groups) {
        this.groups = groups;
        return this;
    }

    public String getJsonReport() {
        return jsonReport;
    }

    public GalenActionTestArguments setJsonReport(String jsonReport) {
        this.jsonReport = jsonReport;
        return this;
    }

    public String getFilter() {
        return filter;
    }

    public GalenActionTestArguments setFilter(String filter) {
        this.filter = filter;
        return this;
    }

    public int getParallelThreads() {
        return parallelThreads;
    }

    public GalenActionTestArguments setParallelThreads(int parallelThreads) {
        this.parallelThreads = parallelThreads;
        return this;
    }

    public String getJunitReport() {
        return junitReport;
    }

    public String getTestngReport() {
        return testngReport;
    }

    public GalenActionTestArguments setJunitReport(String junitReport) {
        this.junitReport = junitReport;
        return this;
    }

    public GalenActionTestArguments setTestngReport(String testngReport) {
        this.testngReport = testngReport;
        return this;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public GalenActionTestArguments setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public List<String> getExcludedTags() {
        return excludedTags;
    }

    public GalenActionTestArguments setExcludedTags(List<String> excludedTags) {
        this.excludedTags = excludedTags;
        return this;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public GalenActionTestArguments setIncludedTags(List<String> includedTags) {
        this.includedTags = includedTags;
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(paths)
                .append(recursive)
                .append(includedTags)
                .append(excludedTags)
                .append(htmlReport)
                .append(testngReport)
                .append(junitReport)
                .append(parallelThreads)
                .append(filter)
                .append(jsonReport)
                .append(groups)
                .append(excludedGroups)
                .append(config)
                .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof GalenActionTestArguments)) {
            return false;
        }
        GalenActionTestArguments rhs = (GalenActionTestArguments)obj;
        return new EqualsBuilder()
                .append(paths, rhs.paths)
                .append(recursive, rhs.recursive)
                .append(includedTags, rhs.includedTags)
                .append(excludedTags, rhs.excludedTags)
                .append(htmlReport, rhs.htmlReport)
                .append(testngReport, rhs.testngReport)
                .append(junitReport, rhs.junitReport)
                .append(parallelThreads, rhs.parallelThreads)
                .append(filter, rhs.filter)
                .append(jsonReport, rhs.jsonReport)
                .append(groups, rhs.groups)
                .append(excludedGroups, rhs.excludedGroups)
                .append(config, rhs.config)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("paths", paths)
                .append("recursive", recursive)
                .append("includedTags", includedTags)
                .append("excludedTags", excludedTags)
                .append("htmlReport", htmlReport)
                .append("testngReport", testngReport)
                .append("junitReport", junitReport)
                .append("parallelThreads", parallelThreads)
                .append("filter", filter)
                .append("jsonReport", jsonReport)
                .append("groups", groups)
                .append("excludedGroups", excludedGroups)
                .append("config", config)
                .toString();
    }

    public GalenActionTestArguments setConfig(String config) {
        this.config = config;
        return this;
    }

    public String getConfig() {
        return config;
    }
}
