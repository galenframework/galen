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
package net.mindengine.galen.specs.reader.page;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.BashTemplateJsFunctions;
import net.mindengine.galen.parser.FileSyntaxException;

public class PageSpecReader implements BashTemplateJsFunctions {
    
    private BashTemplateContext bashTemplateContext;
    
    /*
     *  This field is need to look up early building of objects
     *  so they could be used within bash templates
     */
    private PageSpec pageSpec;
    
    public PageSpecReader(Browser browser) {
        this.browser = browser;
        bashTemplateContext = new BashTemplateContext(this);
    }
    
    
    // Browser is used in order to fetch multi object 
    // at earlier state so that it is possible to use dynamic ranges
    private Browser browser;
    
    
	// Used to store information about spec files that were already loaded
	private Set<String> processedFiles = new HashSet<String>();
	
    public PageSpec read(File file) throws IOException {
        String absolutePath = file.getAbsolutePath();

    	if (processedFiles.contains(absolutePath)) {
    		return null;
    	}
    	else {
    		processedFiles.add(absolutePath);
    		return read(new FileInputStream(file), absolutePath, file.getParent());
    	}
    }

    public PageSpec read(InputStream inputStream) throws IOException {
        return read(inputStream, "<unknown location>", ".");
    }
    
    
    public PageSpec read(InputStream inputStream, String fileLocation, String contextPath) throws IOException {
        
        pageSpec = new PageSpec();
        PageSpecLineProcessor lineProcessor = new PageSpecLineProcessor(contextPath, this, pageSpec);
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, System.getProperty("file.encoding")));
        
        String line = bufferedReader.readLine();
        
        int lineNumber = 1;
        try {
            while(line != null) {
                lineProcessor.processLine(bashTemplateContext.process(line));
                line = bufferedReader.readLine();
                lineNumber++;
            }
        }
        catch (Exception exception) {
            throw new FileSyntaxException(exception, fileLocation, lineNumber);
        }

        return lineProcessor.buildPageSpec();
    }

    public Browser getBrowser() {
        return browser;
    }

    public void setBrowser(Browser browser) {
        this.browser = browser;
    }

    @Override
    public int count(String regex) {
        String jRegex = regex.replace("*", ".*");
        Pattern pattern = Pattern.compile(jRegex);
        
        int count = 0;
        for (String name : pageSpec.getObjects().keySet()) {
            if (pattern.matcher(name).matches()) {
                count ++;
            }
        }
        return count;
    }
    
    

}
