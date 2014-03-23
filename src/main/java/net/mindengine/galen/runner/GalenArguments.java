/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.runner;

import static java.lang.Integer.parseInt;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.MissingArgumentException;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenArguments {

    private String action;
    private String javascript;
    private List<String> paths;
    private Boolean recursive = false;
    private List<String> includedTags = new LinkedList<String>();
    private List<String> excludedTags = new LinkedList<String>();
    private Dimension screenSize;
    private String htmlReport;
    private String testngReport;
    private int parallelSuites = 0;
    private String url;
    private String original;
    private Boolean printVersion;
    private String filter;

    public GalenArguments withAction(String action) {
        this.setAction(action);
        return this;
    }

    public String getAction() {
        return action;
    }

    public void setAction(String action) {
        this.action = action;
    }

    public GalenArguments withJavascript(String javascript) {
        this.setJavascript(javascript);
        return this;
    }

    public String getJavascript() {
        return javascript;
    }

    public void setJavascript(String javascript) {
        this.javascript = javascript;
    }

    public GalenArguments withIncludedTags(String...tags) {
        this.setIncludedTags(Arrays.asList(tags));
        return this;
    }

    public GalenArguments withExcludedTags(String...excludedTags) {
        this.setExcludedTags(Arrays.asList(excludedTags));
        return this;
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

    public GalenArguments withScreenSize(Dimension size) {
        this.setScreenSize(size);
        return this;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public void setScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
    }

    public GalenArguments withHtmlReport(String htmlReport) {
        this.setHtmlReport(htmlReport);
        return this;
    }

    public String getHtmlReport() {
        return htmlReport;
    }

    public void setHtmlReport(String htmlReport) {
        this.htmlReport = htmlReport;
    }

    public GalenArguments withUrl(String url) {
        this.setUrl(url);
        return this;
    }

    public static GalenArguments parse(String[] args) throws ParseException {
        
        args = processSystemProperties(args);
        
        //TODO Refactor this ugly way of handling command line arguments. It should be separate per action.
        
        Options options = new Options();
        options.addOption("u", "url", true, "Url for test page");
        options.addOption("j", "javascript", true, "Path to javascript file which will be executed after test page loads");
        options.addOption("i", "include", true, "Tags for sections that should be included in test run");
        options.addOption("e", "exclude", true, "Tags for sections that should be excluded from test run");
        options.addOption("s", "size", true, "Browser screen size");
        options.addOption("H", "htmlreport", true, "Path for html output report");
        options.addOption("g", "testngreport", true, "Path for testng xml report");
        options.addOption("r", "recursive", false, "Flag for recursive tests scan");
        options.addOption("p", "parallel-suites", true, "Amount of suites to be run in parallel");
        options.addOption("v", "version", false, "Current version");
        options.addOption("f", "filter", true, "Test filter");
        
        CommandLineParser parser = new PosixParser();
        
        CommandLine cmd = null;
        
        try {
            cmd = parser.parse(options, args);
        }
        catch (MissingArgumentException e) {
            throw new IllegalArgumentException("Missing value for " + e.getOption().getLongOpt(), e);
        }
        
        
        GalenArguments galen = new GalenArguments();
        
        galen.setOriginal(merge(args));
        String[] leftovers = cmd.getArgs();
        
        if (leftovers.length > 0) {
            String action = leftovers[0];
            galen.setAction(action);
            
            if (leftovers.length > 1) {
                List<String> paths = new LinkedList<String>();
                for (int i=1; i<leftovers.length; i++) {
                    paths.add(leftovers[i]);
                }
                galen.setPaths(paths);
            }
        }
        
        galen.setUrl(cmd.getOptionValue("u"));
        
        galen.setIncludedTags(convertTags(cmd.getOptionValue("i", "")));
        galen.setExcludedTags(convertTags(cmd.getOptionValue("e", "")));
        galen.setScreenSize(convertScreenSize(cmd.getOptionValue("s")));
        galen.setJavascript(cmd.getOptionValue("javascript"));
        galen.setTestngReport(cmd.getOptionValue("g"));
        galen.setRecursive(cmd.hasOption("r"));
        galen.setHtmlReport(cmd.getOptionValue("H"));
        galen.setParallelSuites(Integer.parseInt(cmd.getOptionValue("p", "0")));
        galen.setPrintVersion(cmd.hasOption("v"));
        galen.setFilter(cmd.getOptionValue("f"));
        
        
        verifyArguments(galen);
        return galen;
    }

    private static String[] processSystemProperties(String[] args) {
        ArrayList<String> list = new ArrayList<String>();
        
        for (String arg : args) {
            if (arg.startsWith("-D")) {
                setSystemProperty(arg);
            }
            else {
                list.add(arg);
            }
        }
        return list.toArray(new String[]{});
    }

    private static void setSystemProperty(String systemPropertyDefinition) {
        String string = systemPropertyDefinition.substring(2);
        String values[] = string.split("=");
        if (values.length != 2) {
            throw new IllegalArgumentException("Cannot parse: " + systemPropertyDefinition);
        }
        
        System.setProperty(values[0].trim(), values[1].trim());
    }

    private static void verifyArguments(GalenArguments galen) {
        if (galen.getAction() != null) {
            if ("test".equals(galen.getAction())) {
                verifyTestAction(galen);
            }
            else if ("check".equals(galen.getAction())) {
                verifyCheckAction(galen);
            }
            else if ("config".equals(galen.getAction())) {
                return;
            }
            else throw new IllegalArgumentException("Unknown action: " + galen.getAction());
        }
    }

    private static void verifyCheckAction(GalenArguments galen) {
        if (galen.getPaths() == null || galen.getPaths().isEmpty()) {
            throw new IllegalArgumentException("Missing spec files");
        }
    }

    private static void verifyTestAction(GalenArguments galen) {
        if (galen.getPaths() == null || galen.getPaths().isEmpty()) {
            throw new IllegalArgumentException("Missing test files");
        }
    }

    private static String merge(String[] args) {
        StringBuffer buffer = new StringBuffer();
        for (String arg : args) {
            buffer.append(arg);
            buffer.append(" ");
        }
        return buffer.toString();
    }

    private static Dimension convertScreenSize(String text) {
        if (text == null) {
            return null;
        }
        
        if (Pattern.matches("[0-9]+x[0-9]+", text)) {
            String[] values = text.split("x");
            if (values.length == 2) {
                return new Dimension(parseInt(values[0]), parseInt(values[1]));
            }
        }
        
        throw new IllegalArgumentException("Incorrect size: " + text);
    }

    private static List<String> convertTags(String optionValue) {
        List<String> tags = new LinkedList<String>();
        String[] array = optionValue.split(",");
        
        for (String item : array) {
            String tag = item.trim();
            
            if (!tag.isEmpty()) {
                tags.add(tag);
            }
        }
        return tags;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    
    @Override
    public int hashCode() {
        return new HashCodeBuilder(13, 19)
        .append(action)
        .append(paths)
        .append(recursive)
        .append(javascript)
        .append(includedTags)
        .append(excludedTags)
        .append(screenSize)
        .append(htmlReport)
        .append(testngReport)
        .append(url)
        .append(parallelSuites)
        .append(filter)
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
        if (!(obj instanceof GalenArguments)) {
            return false;
        }
        GalenArguments rhs = (GalenArguments)obj;
        return new EqualsBuilder()
            .append(action, rhs.action)
            .append(paths, rhs.paths)
            .append(recursive, rhs.recursive)
            .append(javascript, rhs.javascript)
            .append(includedTags, rhs.includedTags)
            .append(excludedTags, rhs.excludedTags)
            .append(screenSize, rhs.screenSize)
            .append(htmlReport, rhs.htmlReport)
            .append(testngReport, rhs.testngReport)
            .append(url, rhs.url)
            .append(filter, rhs.filter)
            .append(parallelSuites, rhs.parallelSuites)
            .isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("action", action)
            .append("paths", paths)
            .append("recursive", recursive)
            .append("javascript", javascript)
            .append("includedTags", includedTags)
            .append("excludedTags", excludedTags)
            .append("screenSize", screenSize)
            .append("htmlReport", htmlReport)
            .append("testngReport", testngReport)
            .append("url", url)
            .append("filter", filter)
            .append("parallelSuites", parallelSuites)
            .toString();
    }

    public String getTestngReport() {
        return testngReport;
    }

    public void setTestngReport(String testngReport) {
        this.testngReport = testngReport;
    }

    public GalenArguments withTestngReport(String testngReport) {
        this.testngReport = testngReport;
        return this;
    }

    public List<String> getPaths() {
        return paths;
    }

    public void setPaths(List<String> paths) {
        this.paths = paths;
    }

    public Boolean getRecursive() {
        return recursive;
    }

    public void setRecursive(Boolean recursive) {
        this.recursive = recursive;
    }

    public GalenArguments withPaths(List<String> paths) {
        this.paths = paths;
        return this;
    }

    public GalenArguments withRecursive(Boolean recursive) {
        this.recursive = recursive;
        return this;
    }

    public int getParallelSuites() {
        return parallelSuites;
    }

    public void setParallelSuites(int parallelSuites) {
        this.parallelSuites = parallelSuites;
    }

    public GalenArguments withParallelSuites(int parallelSuites) {
        setParallelSuites(parallelSuites);
        return this;
    }

    public String getOriginal() {
        return this.original;
    }

    public void setOriginal(String original) {
        this.original = original;
    }

    public GalenArguments withOriginal(String original) {
        this.setOriginal(original);
        return this;
    }

    public Boolean getPrintVersion() {
        return printVersion;
    }

    public void setPrintVersion(Boolean printVersion) {
        this.printVersion = printVersion;
    }

    public GalenArguments withFilter(String filter) {
        this.setFilter(filter);
        return this;
    }

    public String getFilter() {
        return filter;
    }

    public void setFilter(String filter) {
        this.filter = filter;
    }
}