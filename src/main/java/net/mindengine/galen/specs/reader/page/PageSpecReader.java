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
package net.mindengine.galen.specs.reader.page;

import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.parser.FileSyntaxException;
import net.mindengine.galen.parser.JsPageElement;
import net.mindengine.galen.parser.VarsContext;
import net.mindengine.galen.parser.VarsParserJsFunctions;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.Place;
import net.mindengine.galen.utils.GalenUtils;

import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.*;
import java.util.regex.Pattern;

public class PageSpecReader implements VarsParserJsFunctions {

    private Page page;
    private PageSpecReader parent;
    private VarsContext varsContext;
    private Properties properties;

    // Used to store information about spec files that were already loaded
    private Set<String> processedFileIds;

    /*
     *  This field is needed to look up early building of objects
     *  so they could be used within js functions
     */
    private PageSpec pageSpec;

    public PageSpecReader(Properties properties, Page page) {
        if (properties == null) {
            this.properties = new Properties();
        }
        else {
            this.properties = properties;
        }
        this.varsContext = new VarsContext(properties, this);
        this.processedFileIds = new HashSet<String>();
        this.page = page;
    }

    /**
     * Creating a sub reader with a parent is need when we need to share javascript objects
     * and be able to check if a certain script was loaded already or not
     * @param pageSpecReader
     */
    public PageSpecReader(PageSpecReader pageSpecReader) {
        this.properties  = pageSpecReader.properties;
        this.parent = pageSpecReader;
        this.varsContext = pageSpecReader.varsContext;
        pageSpecReader.addChild(this);
        this.processedFileIds = pageSpecReader.processedFileIds;
        this.page = pageSpecReader.page;
    }

    // Needed to look up object created within child readers
    private List<PageSpecReader> childReaders = new LinkedList<PageSpecReader>();


    private void addChild(PageSpecReader pageSpecReader) {
        childReaders.add(pageSpecReader);
    }


    public PageSpec read(String filePath) throws IOException {
        InputStream is = GalenUtils.findFileOrResourceAsStream(filePath);
        if (is == null) {
            throw new FileNotFoundException("Can't find file or resource: " + filePath);
        }
        return read(is, filePath, GalenUtils.getParentForFile(filePath));
    }


    public PageSpec read(InputStream inputStream) throws IOException {
        return read(inputStream, "<unknown location>", null);
    }

    public PageSpec read(InputStream inputStream, String fileLocation, String contextPath) throws IOException {
        this.pageSpec = new PageSpec();

        PageSpecLineProcessor lineProcessor = new PageSpecLineProcessor(properties, contextPath, this, pageSpec);
        
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream, System.getProperty("file.encoding")));

        String line = bufferedReader.readLine();

        int lineNumber = 1;
        try {
            while(line != null) {
                lineProcessor.processLine(line, varsContext, new Place(fileLocation, lineNumber));
                line = bufferedReader.readLine();
                lineNumber++;
            }
        }
        catch (Exception exception) {
            throw new FileSyntaxException(exception, fileLocation, lineNumber);
        }

        return lineProcessor.buildPageSpec();
    }

    public int count(String regex) {
        Pattern pattern = GalenUtils.convertObjectNameRegex(regex);

        Set<String> collectedNames = new HashSet<String>();

        for (String name : pageSpec.getObjects().keySet()) {
            if (pattern.matcher(name).matches()) {
               collectedNames.add(name);
            }
        }

        if (childReaders != null) {
            for (PageSpecReader childReader : childReaders) {
                for (String name : childReader.pageSpec.getObjects().keySet()) {
                    if (pattern.matcher(name).matches()) {
                        collectedNames.add(name);
                    }
                }
            }
        }

        return collectedNames.size();
    }

    public JsPageElement find(String objectName) {
        JsPageElement pageElement = findJsPageElement(objectName);
        if (pageElement != null) {
            return pageElement;
        }
        else {

            if (childReaders != null) {
                for (PageSpecReader childReader : childReaders) {
                    pageElement = childReader.find(objectName);
                    if (pageElement != null) {
                        return pageElement;
                    }
                }
            }
        }
        return null;
    }

    private JsPageElement findJsPageElement(String objectName) {
        if (pageSpec != null) {
            if (pageSpec.getObjects().containsKey(objectName)) {
                Locator locator = pageSpec.getObjectLocator(objectName);
                if (locator != null && page != null) {
                    PageElement pageElement = page.getObject(objectName, locator);
                    if (pageElement != null) {
                        return new JsPageElement(pageElement);
                    }
                }
            }
        }
        return null;
    }

    public Properties getProperties() {
        return properties;
    }

    public void setProperties(Properties properties) {
        this.properties = properties;
    }

    public void runJavascriptFromFile(String filePath, String contextPath) {

        varsContext.runJavascriptFromFile(filePath, contextPath);
    }

    public PageSpecReader createNewSubReader() {
        return new PageSpecReader(this);
    }

    public PageSpecReader getParent() {
        return parent;
    }

    public void importPageSpec(String filePath, String contextPath) throws IOException, NoSuchAlgorithmException {
        filePath = filePath.trim();
        String path;
        if (contextPath != null && !filePath.startsWith("/")) {
            path = contextPath + File.separator + filePath;
        }
        else {
            path = filePath;
        }

        String fileId = GalenUtils.calculateFileId(path);
        if (!processedFileIds.contains(fileId)) {
            processedFileIds.add(fileId);
            PageSpec spec = createNewSubReader().read(path);
            if (spec != null) {
                pageSpec.merge(spec);
            }
        }
    }

    public Page getPage() {
        return page;
    }

    public List<PageSpecRule> getRules() {
        return pageSpec.getRules();
    }
}
