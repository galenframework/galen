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

import java.io.*;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.parser.VarsContext;
import net.mindengine.galen.parser.VarsParserJsFunctions;
import net.mindengine.galen.parser.FileSyntaxException;

import net.mindengine.galen.utils.GalenUtils;
import net.mindengine.galen.specs.reader.Place;

public class PageSpecReader implements VarsParserJsFunctions {
    
    private VarsContext bashTemplateContext;
    private Properties properties;
    /*
     *  This field is need to look up early building of objects
     *  so they could be used within bash templates
     */
    private PageSpec pageSpec;

    public PageSpecReader(Properties properties, Browser browser) {
        if (properties == null) {
            this.properties = new Properties();
        }
        else {
            this.properties = properties;
        }

        this.browser = browser;
        bashTemplateContext = new VarsContext(properties, this);
    }


    // Browser is used in order to fetch multi object
    // at earlier state so that it is possible to use dynamic ranges
    private Browser browser;

	// Used to store information about spec files that were already loaded

    private Set<String> processedFiles = new HashSet<String>();

    public PageSpec read(String filePath) throws IOException {
        if (processedFiles.contains(filePath)) {
            return null;
        }
        else {
            processedFiles.add(filePath);

            InputStream is = GalenUtils.findFileOrResourceAsStream(filePath);
            if (is == null) {
                throw new FileNotFoundException("Can't find file or resource: " + filePath);
            }
            return read(is, filePath, GalenUtils.getParentForFile(filePath));
        }
    }


    public PageSpec read(InputStream inputStream) throws IOException {
        return read(inputStream, "<unknown location>", null);
    }

    public PageSpec read(InputStream inputStream, String fileLocation, String contextPath) throws IOException {

        pageSpec = new PageSpec();

        PageSpecLineProcessor lineProcessor = new PageSpecLineProcessor(properties, contextPath, this, pageSpec);
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, System.getProperty("file.encoding")));

        String line = bufferedReader.readLine();

        int lineNumber = 1;
        try {
            while(line != null) {
                lineProcessor.processLine(bashTemplateContext.process(line), new Place(fileLocation, lineNumber));
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

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

}
