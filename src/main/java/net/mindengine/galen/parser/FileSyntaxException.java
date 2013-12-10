/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.parser;

import java.io.PrintStream;
import java.io.PrintWriter;


public class FileSyntaxException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1698658011707718651L;

    private String filePath;
    private int line;
    
    
    public FileSyntaxException(Exception cause, String filePath, int line) {
        super(cause);
        this.filePath = filePath;
        this.line = line;
    }
    public String getFilePath() {
        return filePath;
    }
    public int getLine() {
        return line;
    }
    
    @Override
    public String getMessage() {
        Throwable cause = getCause();
        if (cause instanceof SyntaxException) {
            return withFileInfo(cause.getMessage());
        }
        return withFileInfo(super.getMessage());
    }
    private String withFileInfo(String message) {
        return String.format("%s\n    in %s:%d", message, filePath, line);
    }
    
    @Override
    public void printStackTrace() {
        printStackTrace(System.err);
    }
    
    @Override
    public void printStackTrace(PrintStream stream) {
        stream.println("Error: " + getMessage());
    }
    
    @Override
    public void printStackTrace(PrintWriter writer) {
        writer.println("Error: " + getMessage());
    }
}
