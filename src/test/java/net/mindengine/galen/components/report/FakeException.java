/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.components.report;

import static java.lang.String.format;

import java.io.PrintStream;
import java.io.PrintWriter;

public class FakeException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = -4840622707009032748L;

    public FakeException(String string) {
        super(string);
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return new StackTraceElement[]{
                new StackTraceElement("net.mindengine.someclass.SomeClass", "method1", "SomeClass.java", 4),
                new StackTraceElement("net.mindengine.someclass.SomeClass2", "method2", "SomeClass2.java", 5),
                new StackTraceElement("net.mindengine.someclass.SomeClass3", "method3", "SomeClass3.java", 6)
        };
    }
    
    @Override
    public void printStackTrace(PrintStream ps) {
        ps.println(getClass().getName() + ": " + getMessage());
        for (StackTraceElement element : getStackTrace()) {
            ps.println(format("\tat %s.%s(%s:%d)", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber()));
        }
    }
    
    @Override
    public void printStackTrace(PrintWriter s) {
        s.println(getClass().getName() + ": " + getMessage());
        for (StackTraceElement element : getStackTrace()) {
            s.println(format("\tat %s.%s(%s:%d)", element.getClassName(), element.getMethodName(), element.getFileName(), element.getLineNumber()));
        }
    }
}
