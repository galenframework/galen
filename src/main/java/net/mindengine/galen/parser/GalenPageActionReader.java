package net.mindengine.galen.parser;

import static java.util.Arrays.asList;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.suite.actions.GalenPageActionInjectJavascript;
import net.mindengine.galen.suite.actions.GalenPageActionRunJavascript;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;

public class GalenPageActionReader {

    public static GalenPageAction readFrom(String actionText) {
        String[] args = net.mindengine.galen.parser.CommandLineParser.parseCommandLine(actionText);
        
        if (args.length < 2) {
            throw new SuiteParserException("Cannot parse: " + actionText);
        }
        
        if (args[0].equals("inject")) {
            return injectActionFrom(args);
        }
        else if (args[0].equals("run")) {
            return runActionFrom(args);
        }
        else if (args[0].equals("check")) {
            return checkActionFrom(args, actionText);
        }
        else throw new SuiteParserException("Unknown action: " + args[0]);
    }

    private static GalenPageAction checkActionFrom(String[] args, String originalText) {
        Options options = new Options();
        options.addOption("i", "include", true, "include tags");
        options.addOption("e", "exclude", true, "exclude tags");
        
        org.apache.commons.cli.CommandLineParser parser = new PosixParser();
        
        try {
            CommandLine cmd = parser.parse(options, args);
            String[] leftoverArgs = cmd.getArgs();
         
            if (leftoverArgs == null || leftoverArgs.length < 2) {
                throw new SuiteParserException("There are no page specs: " + originalText);
            }
            
            List<String> specs = new LinkedList<String>();
            for (int i=1; i < leftoverArgs.length; i++) {
                specs.add(leftoverArgs[i]);
            }
            
            return new GalenPageActionCheck()
                .withSpecs(specs)
                .withIncludedTags(readTags(cmd.getOptionValue("i")))
                .withExcludedTags(readTags(cmd.getOptionValue("e")));
        }
        catch (Exception e) {
            throw new SuiteParserException("Couldn't parse: " + originalText, e);
        }
    }

    private static List<String> readTags(String tagsCommaSeparated) {
        if (tagsCommaSeparated != null) {
            String tagsArray[] = tagsCommaSeparated.split(",");
            
            List<String> tags = new LinkedList<String>();
            for (String tag : tagsArray) {
                tag = tag.trim();
                if (!tag.isEmpty()) {
                    tags.add(tag);
                }
            }
            return tags;
        }
        return null;
    }

    private static GalenPageAction runActionFrom(String[] args) {
        String jsonArguments = null;
        if (args.length > 2) {
            jsonArguments = args[2];
        }
        
        return new GalenPageActionRunJavascript(args[1])
            .withJsonArguments(jsonArguments);
    }

    private static GalenPageActionInjectJavascript injectActionFrom(String[] args) {
        return new GalenPageActionInjectJavascript(args[1]);
    }

}
