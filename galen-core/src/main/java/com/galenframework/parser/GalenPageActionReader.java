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
package com.galenframework.parser;


import java.awt.Dimension;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.galenframework.specs.Place;
import com.galenframework.specs.page.Locator;
import com.galenframework.suite.actions.*;
import com.galenframework.utils.GalenUtils;
import com.galenframework.suite.GalenPageAction;
import com.galenframework.suite.actions.GalenPageActionWait.UntilType;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.PosixParser;
import org.apache.commons.lang3.tuple.Pair;

public class GalenPageActionReader {

    public static GalenPageAction readFrom(String actionText, Place place) {
        String[] args = CommandLineParser.parseCommandLine(actionText);
        
        if (args.length < 2) {
            throw new SyntaxException(place, "Cannot parse: " + actionText);
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
        else if (args[0].equals("cookie")) {
            return cookieActionFrom(args);
        }
        else if (args[0].equals("open")) {
            return openActionFrom(args);
        }
        else if (args[0].equals("resize")) {
            return resizeActionFrom(args);
        }
        else if (args[0].equals("wait")) {
            return waitActionFrom(args);
        }
        else if (args[0].equals("properties")) {
            return propertiesActionFrom(args);
        }
        else if (args[0].equals("dump")) {
            return dumpPageActionFrom(args, actionText);
        }
        else throw new SyntaxException(place, "Unknown action: " + args[0]);
    }



    private static GalenPageAction resizeActionFrom(String[] args) {
        Dimension size = GalenUtils.readSize(args[1]);
        return new GalenPageActionResize(size.width, size.height);
    }
    
    
    private static GalenPageAction propertiesActionFrom(String[] args) {
        List<String> files = new LinkedList<>();
        for (int i = 1; i < args.length; i++) {
            files.add(args[i]);
        }
        return new GalenPageActionProperties().withFiles(files);
    }


    private static GalenPageAction openActionFrom(String[] args) {
        return new GalenPageActionOpen(args[1]);
    }

    private static GalenPageAction cookieActionFrom(String[] args) {
        GalenPageActionCookie action = new GalenPageActionCookie();
        List<String> cookies = new LinkedList<>();
        for(int i = 1; i<args.length; i++) {
            cookies.add(args[i]);
        }
        action.setCookies(cookies);
        return action;
    }

    private static GalenPageAction checkActionFrom(String[] args, String originalText) {
        CommandLineReader reader = new CommandLineReader(args);

        String specPath = null;
        List<String> includedTags = new LinkedList<>();
        List<String> excludedTags = new LinkedList<>();
        String sectionNameFilter = null;
        Map<String, Object> jsVariables = new HashMap<>();


        //Skipping the check action name
        reader.skipArgument();

        while (reader.hasNext()) {
            if (!reader.isNextArgument()) {
                specPath = reader.readNext();
            } else {
                Pair<String, String> argument = reader.readArgument();

                if (argument.getKey().equals("include")) {
                    includedTags.addAll(readTags(argument.getValue()));
                } else if (argument.getKey().equals("exclude")) {
                    excludedTags.addAll(readTags(argument.getValue()));
                } else if (argument.getKey().startsWith("V")) {
                    String varName = argument.getKey().substring(1);
                    String varValue = argument.getValue();
                    jsVariables.put(varName, varValue);
                } else if (argument.getKey().equals("section")) {
                    sectionNameFilter = argument.getValue();
                } else {
                    throw new SyntaxException("Unknown argument: " + argument.getKey());
                }
            }
        }

        if (specPath == null || specPath.isEmpty()) {
            throw new SyntaxException("Missing spec path");
        }


        return new GalenPageActionCheck()
            .withSpec(specPath)
            .withIncludedTags(includedTags)
            .withExcludedTags(excludedTags)
            .withSectionNameFilter(sectionNameFilter)
            .withJsVariables(jsVariables);
    }


    private static GalenPageAction dumpPageActionFrom(String[] args, String originalText) {
        Options options = new Options();
        options.addOption("n", "name", true, "Page name");
        options.addOption("e", "export", true, "Export dir");
        options.addOption("w", "max-width", true, "Maximal width of elements in croppped screenshots");
        options.addOption("h", "max-height", true, "Maximal height of elements in cropped screenshots");
        options.addOption("i", "only-images", false, "Flag for exporting only images without html and json files");

        org.apache.commons.cli.CommandLineParser parser = new PosixParser();

        try {
            CommandLine cmd = parser.parse(options, args);
            String[] leftoverArgs = cmd.getArgs();

            if (leftoverArgs == null || leftoverArgs.length < 2) {
                throw new SyntaxException("There are no page specs: " + originalText);
            }

            Integer maxWidth = null;
            Integer maxHeight = null;

            String maxWidthText = cmd.getOptionValue("w");
            String maxHeightText = cmd.getOptionValue("h");

            if (maxWidthText != null && !maxWidthText.isEmpty()) {
                maxWidth = Integer.parseInt(maxWidthText);
            }

            if (maxHeightText != null && !maxHeightText.isEmpty()) {
                maxHeight = Integer.parseInt(maxHeightText);
            }

            boolean onlyImages = cmd.hasOption("i");

            return new GalenPageActionDumpPage()
                    .withSpecPath(leftoverArgs[1])
                    .withPageName(cmd.getOptionValue("n"))
                    .withPageDumpPath(cmd.getOptionValue("e"))
                    .withMaxWidth(maxWidth)
                    .withMaxHeight(maxHeight)
                    .withOnlyImages(onlyImages);
        }
        catch (Exception e) {
            throw new SyntaxException("Couldn't parse: " + originalText, e);
        }
    }

    private static List<String> readTags(String tagsCommaSeparated) {
        if (tagsCommaSeparated != null) {
            String tagsArray[] = tagsCommaSeparated.split(",");
            
            List<String> tags = new LinkedList<>();
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
    
    private static GalenPageAction waitActionFrom(String[] args) {
        if (args.length < 2) {
            throw new SyntaxException("the timeout is not specified");
        }
        
        
        GalenPageActionWait wait = new GalenPageActionWait();
        wait.setTimeout(parseTimeout(args[1]));
        
        if (args.length > 2) {
            parseUntilConditions(wait, args);
        }
        return wait;
    }

    private static void parseUntilConditions(GalenPageActionWait wait, String[] args) {
        if (args[2].equals("until")) {
            if (args.length > 3) {
                List<GalenPageActionWait.Until> untilElements = new LinkedList<>();
                
                UntilType currentType = null;
                
                for (int i = 3; i < args.length; i++) {
                    UntilType type = UntilType.parseNonStrict(args[i]);
                    
                    if (type != null) {
                        currentType = type;
                    }
                    else {
                        if (currentType == null) {
                            throw new SyntaxException("You have to specify one of the following checks: visible, hidden, exist, gone");
                        }
                        
                        untilElements.add(new GalenPageActionWait.Until(currentType, Locator.parse(args[i])));
                    }
                }
                
                wait.setUntilElements(untilElements);
            }
            else throw new SyntaxException("You have to provide locators");
        }
        else throw new SyntaxException(String.format("Expected \"until\" but got \"%s\"", args[2]));
    }




    private static int parseTimeout(String timeoutText) {
        for (int i = 0; i < timeoutText.length(); i++) {
            if (!isNumber(timeoutText.charAt(i))) {
                int number = Integer.parseInt(timeoutText.substring(0, i));
                String unitPart = timeoutText.substring(i);
                if (unitPart.equals("s")) {
                    return 1000 * number;
                }
                else if (unitPart.equals("ms")) {
                    return number;
                }
                else if (unitPart.equals("m")) {
                    return 60000 * number;
                }
                else throw new SyntaxException("Unkown time unit: " + unitPart);
                
            }
        }
        return Integer.parseInt(timeoutText);
    }


    private static boolean isNumber(char symbol) {
        int code = (int)symbol;
        return code > 47 && code < 58;
    }


    private static GalenPageActionInjectJavascript injectActionFrom(String[] args) {
        return new GalenPageActionInjectJavascript(args[1]);
    }

}
