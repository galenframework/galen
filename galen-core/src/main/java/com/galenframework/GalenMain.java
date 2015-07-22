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
package com.galenframework;


import java.io.*;
import com.galenframework.actions.GalenAction;
import com.galenframework.actions.GalenActionVersion;
import com.galenframework.validation.FailureListener;
import com.galenframework.config.GalenConfig;
import com.galenframework.runner.CombinedListener;
import com.galenframework.runner.CompleteListener;
import org.apache.commons.lang3.ArrayUtils;

public class GalenMain {
    private final PrintStream outStream;
    private final PrintStream errStream;

    private CompleteListener listener;

    public GalenMain() {
        this.outStream = System.out;
        this.errStream = System.err;
    }

    public GalenMain(PrintStream outStream, PrintStream errStream) {
        this.outStream = outStream;
        this.errStream = errStream;
    }

    public void execute(String[] arguments) {
        FailureListener failureListener = new FailureListener();
        CombinedListener combinedListener = new CombinedListener();
        combinedListener.add(failureListener);
        if (listener != null) {
            combinedListener.add(listener);
        }

        if (arguments.length > 0) {
            String actionName = arguments[0];
            String[] actionArguments = ArrayUtils.subarray(arguments, 1, arguments.length);

            GalenAction action = GalenAction.create(actionName, actionArguments, outStream, errStream, combinedListener);
            try {
                action.execute();
            } catch (Exception ex) {
                throw new RuntimeException(ex);
            }

        } else {
            new GalenActionVersion(arguments, outStream, errStream, combinedListener).execute();
        }

        combinedListener.done();

        if (GalenConfig.getConfig().getUseFailExitCode()) {
            if (failureListener.hasFailures()) {
                errStream.println("There were failures in galen tests");
                System.exit(1);
            }
        }
    }

    public static void main (String[] args) {
        new GalenMain().execute(args);
    }


    public CompleteListener getListener() {
        return listener;
    }

    public void setListener(CompleteListener listener) {
        this.listener = listener;
    }

}
