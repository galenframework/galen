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

import org.apache.commons.cli.*;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenActionGenerateArguments {

    private String path;
    private String export;
    private boolean useGalenExtras = true;

    public static GalenActionGenerateArguments parse(String[] args) {
        args = ArgumentsUtils.processSystemProperties(args);

        Options options = new Options();
        options.addOption("e", "export", true, "Path to generated spec file");
        options.addOption("G", "no-galen-extras", false, "Disable galen-extras expressions");

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
        arguments.setExport(cmd.getOptionValue("e"));
        arguments.setUseGalenExtras(!cmd.hasOption("G"));

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
            .append(useGalenExtras, that.useGalenExtras)
            .append(path, that.path)
            .append(export, that.export)
            .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder(17, 37)
            .append(path)
            .append(export)
            .append(useGalenExtras)
            .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
            .append("path", path)
            .append("export", export)
            .append("useGalenExtras", useGalenExtras)
            .toString();
    }

    public boolean isUseGalenExtras() {
        return useGalenExtras;
    }

    public GalenActionGenerateArguments setUseGalenExtras(boolean useGalenExtras) {
        this.useGalenExtras = useGalenExtras;
        return this;
    }
}
