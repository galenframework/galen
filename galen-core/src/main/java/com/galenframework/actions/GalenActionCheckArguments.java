/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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

import com.galenframework.utils.GalenUtils;
import org.apache.commons.cli.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.awt.*;
import java.util.List;

import static com.galenframework.actions.ArgumentsUtils.convertTags;
import static java.lang.Integer.parseInt;
import static java.util.Arrays.asList;

public class GalenActionCheckArguments {
    private List<String> paths;
    private List<String> includedTags;
    private List<String> excludedTags;
    private String url;
    private Dimension screenSize;
    private String htmlReport;
    private String testngReport;
    private String junitReport;
    private String jsonReport;
    private String javascript;
    private String config;
    private String sectionNameFilter;


    public static GalenActionCheckArguments parse(String[] args) {
        args= ArgumentsUtils.processSystemProperties(args);

        Options options = new Options();
        options.addOption("i", "include", true, "Tags for sections that should be included in test run");
        options.addOption("e", "exclude", true, "Tags for sections that should be excluded from test run");
        options.addOption("S", "section", true, "Section name filter");
        options.addOption("h", "htmlreport", true, "Path for html output report");
        options.addOption("j", "jsonreport", true, "Path for json report");
        options.addOption("g", "testngreport", true, "Path for testng xml report");
        options.addOption("x", "junitreport", true, "Path for junit xml report");
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

        GalenActionCheckArguments arguments = new GalenActionCheckArguments();
        arguments.setTestngReport(cmd.getOptionValue("g"));
        arguments.setJunitReport(cmd.getOptionValue("x"));
        arguments.setHtmlReport(cmd.getOptionValue("h"));
        arguments.setJsonReport(cmd.getOptionValue("j"));
        arguments.setUrl(cmd.getOptionValue("u"));
        arguments.setScreenSize(GalenUtils.readSize(cmd.getOptionValue("s")));
        arguments.setJavascript(cmd.getOptionValue("J"));
        arguments.setIncludedTags(convertTags(cmd.getOptionValue("i")));
        arguments.setExcludedTags(convertTags(cmd.getOptionValue("e")));
        arguments.setPaths(asList(cmd.getArgs()));
        arguments.setConfig(cmd.getOptionValue("c"));
        arguments.setSectionNameFilter(cmd.getOptionValue("S"));

        if (arguments.getPaths().isEmpty()) {
            throw new IllegalArgumentException("Missing spec files");
        }

        return arguments;
    }

    public List<String> getPaths() {
        return paths;
    }

    public GalenActionCheckArguments setPaths(List<String> paths) {
        this.paths = paths;
        return this;
    }

    public String getJsonReport() {
        return jsonReport;
    }

    public GalenActionCheckArguments setJsonReport(String jsonReport) {
        this.jsonReport = jsonReport;
        return this;
    }

    public String getTestngReport() {
        return testngReport;
    }

    public GalenActionCheckArguments setTestngReport(String testngReport) {
        this.testngReport = testngReport;
        return this;
    }

    public String getJunitReport() {
        return junitReport;
    }

    public GalenActionCheckArguments setJunitReport(String junitReport) {
        this.junitReport = junitReport;
        return this;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public GalenActionCheckArguments setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
        return this;
    }

    public List<String> getExcludedTags() {
        return excludedTags;
    }

    public GalenActionCheckArguments setExcludedTags(List<String> excludedTags) {
        this.excludedTags = excludedTags;
        return this;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public GalenActionCheckArguments setIncludedTags(List<String> includedTags) {
        this.includedTags = includedTags;
        return this;
    }

    public String getUrl() {
        return url;
    }

    public GalenActionCheckArguments setUrl(String url) {
        this.url = url;
        return this;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public GalenActionCheckArguments setScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
        return this;
    }

    public String getJavascript() {
        return javascript;
    }

    public GalenActionCheckArguments setJavascript(String javascript) {
        this.javascript = javascript;
        return this;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(paths)
                .append(includedTags)
                .append(excludedTags)
                .append(url)
                .append(screenSize)
                .append(htmlReport)
                .append(testngReport)
                .append(junitReport)
                .append(jsonReport)
                .append(javascript)
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
        if (!(obj instanceof GalenActionCheckArguments)) {
            return false;
        }
        GalenActionCheckArguments rhs = (GalenActionCheckArguments) obj;
        return new EqualsBuilder()
                .append(rhs.paths, paths)
                .append(rhs.includedTags, includedTags)
                .append(rhs.excludedTags, excludedTags)
                .append(rhs.url, url)
                .append(rhs.screenSize, screenSize)
                .append(rhs.htmlReport, htmlReport)
                .append(rhs.testngReport, testngReport)
                .append(rhs.junitReport, junitReport)
                .append(rhs.jsonReport, jsonReport)
                .append(rhs.javascript, javascript)
                .append(rhs.config, config)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("paths", paths)
                .append("includedTags", includedTags)
                .append("excludedTags", excludedTags)
                .append("url", url)
                .append("screenSize", screenSize)
                .append("htmlReport", htmlReport)
                .append("testngReport", testngReport)
                .append("junitReport", junitReport)
                .append("jsonReport", jsonReport)
                .append("javascript", javascript)
                .append("config", config)
                .toString();
    }

    public GalenActionCheckArguments setConfig(String config) {
        this.config = config;
        return this;
    }

    public String getConfig() {
        return config;
    }

    public GalenActionCheckArguments setSectionNameFilter(String sectionNameFilter) {
        this.sectionNameFilter = sectionNameFilter;
        return this;
    }

    public String getSectionNameFilter() {
        return sectionNameFilter;
    }
}
