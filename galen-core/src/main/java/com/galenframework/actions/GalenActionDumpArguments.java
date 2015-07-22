/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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

import java.awt.*;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import static java.lang.Integer.parseInt;

public class GalenActionDumpArguments {
    private List<String> paths;
    private Integer maxWidth;
    private Integer maxHeight;
    private String export;
    private Dimension screenSize;
    private String url;


    public static GalenActionDumpArguments parse(String[] args) {
        args = ArgumentsUtils.processSystemProperties(args);

        Options options = new Options();
        options.addOption("u", "url", true, "Initial test url");
        options.addOption("s", "size", true, "Browser window size");
        options.addOption("W", "max-width", true, "Maximum width of element area image");
        options.addOption("H", "max-height", true, "Maximum height of element area image");
        options.addOption("E", "export", true, "Export path for page dump");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (MissingArgumentException e) {
            throw new IllegalArgumentException("Missing value for " + e.getOption().getLongOpt(), e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        GalenActionDumpArguments arguments = new GalenActionDumpArguments();
        arguments.setUrl(cmd.getOptionValue("u"));
        arguments.setScreenSize(convertScreenSize(cmd.getOptionValue("s")));
        arguments.setMaxWidth(parseOptionalInt(cmd.getOptionValue("W")));
        arguments.setMaxHeight(parseOptionalInt(cmd.getOptionValue("H")));
        arguments.setExport(cmd.getOptionValue("E"));

        String[] leftovers = cmd.getArgs();
        List<String> paths = new LinkedList<String>();
        if (leftovers.length > 0) {
            for (int i = 0; i < leftovers.length; i++) {
                paths.add(leftovers[i]);
            }
        }
        arguments.setPaths(paths);
        return arguments;
    }

    private static Integer parseOptionalInt(String valueText) {
        if (valueText != null && !valueText.trim().isEmpty()) {
            return parseInt(valueText);
        }
        else return null;
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

    public List<String> getPaths() {
        return paths;
    }

    public GalenActionDumpArguments setPaths(List<String> paths) {
        this.paths = paths;
        return this;
    }

    public Dimension getScreenSize() {
        return screenSize;
    }

    public GalenActionDumpArguments setScreenSize(Dimension screenSize) {
        this.screenSize = screenSize;
        return this;
    }

    public Integer getMaxWidth() {
        return maxWidth;
    }

    public GalenActionDumpArguments setMaxWidth(Integer maxWidth) {
        this.maxWidth = maxWidth;
        return this;
    }

    public Integer getMaxHeight() {
        return maxHeight;
    }

    public GalenActionDumpArguments setMaxHeight(Integer maxHeight) {
        this.maxHeight = maxHeight;
        return this;
    }

    public String getExport() {
        return export;
    }

    public GalenActionDumpArguments setExport(String export) {
        this.export = export;
        return this;
    }

    public GalenActionDumpArguments setUrl(String url) {
        this.url = url;
        return this;
    }

    public String getUrl() {
        return url;
    }


    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(paths)
                .append(maxWidth)
                .append(maxHeight)
                .append(export)
                .append(screenSize)
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
        if (!(obj instanceof GalenActionDumpArguments)) {
            return false;
        }
        GalenActionDumpArguments rhs = (GalenActionDumpArguments) obj;
        return new EqualsBuilder()
                .append(rhs.paths, paths)
                .append(rhs.maxWidth, maxWidth)
                .append(rhs.maxHeight, maxHeight)
                .append(rhs.export, export)
                .append(rhs.screenSize, screenSize)
                .append(rhs.url, url)
                .isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("paths", paths)
                .append("maxWidth", maxWidth)
                .append("maxHeight", maxHeight)
                .append("export", export)
                .append("screenSize", screenSize)
                .append("url", url)
                .toString();
    }
}
