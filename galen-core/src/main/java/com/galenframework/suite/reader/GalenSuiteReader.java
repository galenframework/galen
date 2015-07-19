/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package com.galenframework.suite.reader;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Properties;

import com.galenframework.parser.FileSyntaxException;
import com.galenframework.parser.SyntaxException;
import com.galenframework.tests.GalenBasicTest;


public class GalenSuiteReader {

    public List<GalenBasicTest> read(File file) throws IOException {
        return read(new FileInputStream(file), file.getAbsolutePath());
    }
    public List<GalenBasicTest> read(InputStream inputStream) throws IOException {
        return read(inputStream, "< unknown file >");
    }
    
    private List<GalenBasicTest> read(InputStream inputStream, String filePath) throws IOException {
        try {
            GalenSuiteLineProcessor lineProcessor = new GalenSuiteLineProcessor(new Properties(), getContextPath(filePath));
            lineProcessor.readLines(inputStream);
            return lineProcessor.buildSuites();
        }
        catch (SyntaxException e) {
            
            int lineNumber = -1;
            if (e.getLine() != null) {
                lineNumber = e.getLine().getNumber();
            }
            throw new FileSyntaxException(e, filePath, lineNumber);
        }
    }
    private String getContextPath(String filePath) {
        return new File(filePath).getParent();
    }

}
