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
package net.mindengine.galen.suite.reader;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

import net.mindengine.galen.parser.FileSyntaxException;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.suite.GalenSuite;


public class GalenSuiteReader {

    public List<GalenSuite> read(File file) throws IOException {
        return read(new FileInputStream(file), file.getAbsolutePath());
    }
    public List<GalenSuite> read(InputStream inputStream) throws IOException {
        return read(inputStream, "< unknown file >");
    }
    
    private List<GalenSuite> read(InputStream inputStream, String filePath) throws IOException {
        try {
            GalenSuiteLineProcessor lineProcessor = new GalenSuiteLineProcessor();
            
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
            
            String line = bufferedReader.readLine();
            int lineNumber = 0;
            while(line != null){
                lineNumber++;
                lineProcessor.processLine(line, lineNumber);
                line = bufferedReader.readLine();
            }
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

}
