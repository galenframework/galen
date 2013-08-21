package net.mindengine.galen.parser;

import net.mindengine.galen.specs.reader.StringCharReader;

public class CommandLineParser {

    public static String[] parseCommandLine(String text) {
        return Expectations.commandLineArguments().read(new StringCharReader(text));
    }

}
