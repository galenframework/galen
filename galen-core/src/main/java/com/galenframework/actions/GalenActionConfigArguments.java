package com.galenframework.actions;

import org.apache.commons.cli.*;

public class GalenActionConfigArguments {
    private Boolean isGlobal = false;

    public static GalenActionConfigArguments parse(String[] args) {
        args = ArgumentsUtils.processSystemProperties(args);

        Options options = new Options();
        options.addOption("g", "global", false, "Flag to create global config in user home directory");

        CommandLineParser parser = new PosixParser();
        CommandLine cmd;

        try {
            cmd = parser.parse(options, args);
        } catch (MissingArgumentException e) {
            throw new IllegalArgumentException("Missing value for " + e.getOption().getLongOpt(), e);
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }

        GalenActionConfigArguments configArguments = new GalenActionConfigArguments();
        configArguments.isGlobal = cmd.hasOption("g");
        return configArguments;
    }

    public Boolean getGlobal() {
        return isGlobal;
    }

}
