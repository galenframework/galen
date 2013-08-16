package net.mindengine.galen.runner;

import static java.lang.Integer.parseInt;

import java.awt.Dimension;
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
    private List<String> includedTags;
    private List<String> excludedTags;
    private Dimension screenSize;
    private String spec;
    private String htmlReport;
    private String testngReport;
    private String url;
    

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

    public GalenArguments withSpec(String spec) {
        this.setSpec(spec);
        return this;
    }

    public String getSpec() {
        return spec;
    }

    public void setSpec(String spec) {
        this.spec = spec;
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
        
        Options options = new Options();
        options.addOption("u", "url", true, "Url for test page");
        options.addOption("j", "javascript", true, "Path to javascript file which will be executed after test page loads");
        options.addOption("i", "include", true, "Tags for sections that should be included in test run");
        options.addOption("e", "exclude", true, "Tags for sections that should be excluded from test run");
        options.addOption("r", "size", true, "Browser screen size");
        options.addOption("s", "spec", true, "Path for page specifications file");
        options.addOption("R", "htmlreport", true, "Path for html output report");
        options.addOption("g", "testngreport", true, "Path for testng xml report");
        
        CommandLineParser parser = new PosixParser();
        
        CommandLine cmd = null;
        
        try {
            cmd = parser.parse(options, args);
        }
        catch (MissingArgumentException e) {
            throw new IllegalArgumentException("Missing value for " + e.getOption().getLongOpt(), e);
        }
        
        
        GalenArguments galen = new GalenArguments();
        
        if (cmd.getArgs().length > 0) {
            String action = cmd.getArgs()[0];
            if (action.equals("run")) {
                galen.setAction(action);
            }
            else throw new IllegalArgumentException("Unknown action: " + action);
        }
        else throw new IllegalArgumentException("Missing action");
        
        galen.setUrl(cmd.getOptionValue("u"));
        if (galen.getUrl() == null) {
            throw new IllegalArgumentException("Missing url");
        }
        
        galen.setIncludedTags(convertTags(cmd.getOptionValue("i", "")));
        galen.setExcludedTags(convertTags(cmd.getOptionValue("e", "")));
        galen.setScreenSize(convertScreenSize(cmd.getOptionValue("r")));
        galen.setJavascript(cmd.getOptionValue("javascript"));
        galen.setTestngReport(cmd.getOptionValue("g"));
        
        galen.setSpec(cmd.getOptionValue("s"));
        if (galen.getSpec() == null) {
            throw new IllegalArgumentException("Missing spec file");
        }
        
        galen.setHtmlReport(cmd.getOptionValue("R"));
        
        
        return galen;
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
        .append(javascript)
        .append(includedTags)
        .append(excludedTags)
        .append(screenSize)
        .append(spec)
        .append(htmlReport)
        .append(testngReport)
        .append(url)
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
            .append(javascript, rhs.javascript)
            .append(includedTags, rhs.includedTags)
            .append(excludedTags, rhs.excludedTags)
            .append(screenSize, rhs.screenSize)
            .append(spec, rhs.spec)
            .append(htmlReport, rhs.htmlReport)
            .append(testngReport, rhs.testngReport)
            .append(url, rhs.url)
            .isEquals();
    }
    
    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("action", action)
            .append("javascript", javascript)
            .append("includedTags", includedTags)
            .append("excludedTags", excludedTags)
            .append("screenSize", screenSize)
            .append("spec", spec)
            .append("htmlReport", htmlReport)
            .append("testngReport", testngReport)
            .append("url", url)
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
}