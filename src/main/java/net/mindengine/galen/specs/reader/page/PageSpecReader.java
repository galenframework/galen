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
package net.mindengine.galen.specs.reader.page;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class PageSpecReader {
    
    public PageSpec read(File file) throws IOException {
        return read(new FileInputStream(file), file.getAbsolutePath());
    }

    public PageSpec read(InputStream inputStream) throws IOException {
        return read(inputStream, "<unknown location>");
    }
    
    
    public PageSpec read(InputStream inputStream, String fileLocation) throws IOException {
        PageSpecLineProcessor lineProcessor = new PageSpecLineProcessor();
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "UTF-8"));
        
        String line = bufferedReader.readLine();
        
        int lineNumber = 1;
        try {
            while(line != null){
                lineProcessor.processLine(line);
                line = bufferedReader.readLine();
                lineNumber++;
            }
        }
        catch (Exception exception) {
            throw new PageSpecReaderException(exception, fileLocation, lineNumber);
        }

        return lineProcessor.buildPageSpec();
    }
    
    

}
