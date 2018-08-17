/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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


import com.galenframework.config.GalenConfig;
import com.galenframework.reports.ConsoleReportingListener;
import com.galenframework.runner.CombinedListener;
import com.galenframework.runner.CompleteListener;

import java.io.IOException;
import java.io.PrintStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.LinkedList;
import java.util.List;

public abstract class GalenAction {

    protected final PrintStream outStream;
    protected final PrintStream errStream;
    protected final String[] arguments;

    public GalenAction(String[] arguments, PrintStream outStream, PrintStream errStream) {
        this.arguments = arguments;
        this.outStream = outStream;
        this.errStream = errStream;
    }

    public void loadConfigIfNeeded(String configPath) throws IOException {
        if (configPath != null) {
            GalenConfig.reloadConfigFromPath(configPath);
        }
    }

    public abstract void execute() throws Exception;

    public static GalenAction create(String actionName, String[] arguments, PrintStream outStream, PrintStream errStream, CombinedListener combinedListener) {
        switch (actionName) {
            case "test":
                return new GalenActionTest(arguments, outStream, errStream, combinedListener);
            case "check":
                return new GalenActionCheck(arguments, outStream, errStream, combinedListener);
            case "dump":
                return new GalenActionDump(arguments, outStream, errStream);
            case "mutate":
                return new GalenActionMutate(arguments, outStream, errStream, combinedListener);
            case "help":
            case "-h":
            case "--help":
                return new GalenActionHelp(arguments, outStream, errStream);
            case "version":
            case "-v":
            case "--version":
                return new GalenActionVersion(arguments, outStream, errStream);
            case "config":
                return new GalenActionConfig(arguments, outStream, errStream);
            case "generate":
                return new GalenActionGenerate(arguments, outStream, errStream);
        }
        throw new RuntimeException("Unknown action: " + actionName);
    }

    public CombinedListener createListeners(CombinedListener originalListener) {
        try {
            CombinedListener combinedListener = new CombinedListener();
            combinedListener.add(new ConsoleReportingListener(outStream, outStream));

            // Adding all user defined listeners
            List<CompleteListener> configuredListeners = getConfiguredListeners();
            for (CompleteListener configuredListener : configuredListeners) {
                combinedListener.add(configuredListener);
            }

            if (originalListener != null) {
                combinedListener.add(originalListener);
            }

            return combinedListener;
        }
        catch (Exception ex) {
            throw new RuntimeException("Couldn't configure listeners", ex);
        }
    }

    @SuppressWarnings("unchecked")
    public static List<CompleteListener> getConfiguredListeners() throws ClassNotFoundException, SecurityException, NoSuchMethodException, IllegalArgumentException,
            InstantiationException, IllegalAccessException, InvocationTargetException {
        List<CompleteListener> configuredListeners = new LinkedList<>();
        List<String> classNames = GalenConfig.getConfig().getReportingListeners();

        for (String className : classNames) {
            Constructor<CompleteListener> constructor = (Constructor<CompleteListener>) Class.forName(className).getConstructor();
            configuredListeners.add(constructor.newInstance());
        }
        return configuredListeners;
    }

    protected String originalCommand(String[] arguments) {
        StringBuilder builder = new StringBuilder("check ");
        for (String argument : arguments) {
            builder.append(" ");
            builder.append(argument);
        }
        return builder.toString();
    }
}
