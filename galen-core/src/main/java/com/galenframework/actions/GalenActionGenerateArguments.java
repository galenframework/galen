package com.galenframework.actions;

import org.apache.commons.cli.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenActionGenerateArguments {

    private String path;
    private String export;

    public static GalenActionGenerateArguments parse(String[] args) {
        args = ArgumentsUtils.processSystemProperties(args);

        Options options = new Options();
        options.addOption("E", "export", true, "Path to generated spec file");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (MissingArgumentException e) {
            throw new IllegalArgumentException("Missing value for " + e.getOption().getLongOpt(), e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }


        GalenActionGenerateArguments arguments = new GalenActionGenerateArguments();
        arguments.setExport(cmd.getOptionValue("E"));

        if (cmd.getArgs() == null || cmd.getArgs().length < 1) {
            throw new IllegalArgumentException("Missing page dump file");
        }
        arguments.setPath(cmd.getArgs()[0]);
        return arguments;
    }

    public String getExport() {
        return export;
    }

    public GalenActionGenerateArguments setExport(String export) {
        this.export = export;
        return this;
    }

    public String getPath() {
        return path;
    }

    public GalenActionGenerateArguments setPath(String path) {
        this.path = path;
        return this;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (o == null || getClass() != o.getClass()) return false;

        GalenActionGenerateArguments that = (GalenActionGenerateArguments) o;

        return new EqualsBuilder()
            .append(path, that.path)
            .append(export, that.export)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(path)
            .append(export)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("path", path)
            .append("export", export)
            .toString();
    }
}
