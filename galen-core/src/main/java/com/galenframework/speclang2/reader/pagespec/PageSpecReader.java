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
package com.galenframework.speclang2.reader.pagespec;

import com.galenframework.page.Page;
import com.galenframework.parser.FileSyntaxException;
import com.galenframework.parser.IndentationStructureParser;
import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.reader.page.PageSpec;
import com.galenframework.utils.GalenUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PageSpecReader {

    public PageSpec read(String path, Page page,
                         List<String> tags, List<String> excludedTags,
                         Properties properties,
                         Map<String, Object> jsVariables) throws IOException {

        String contextPath = GalenUtils.getParentForFile(path);
        InputStream stream = GalenUtils.findFileOrResourceAsStream(path);
        if (stream == null) {
            throw new FileNotFoundException(path);
        }
        return read(stream, path, contextPath, page, tags, excludedTags, properties, jsVariables);
    }

    public PageSpec read(InputStream inputStream, String source,
                         String contextPath,
                         Page page,
                         List<String> tags, List<String> excludedTags,
                         Properties properties,
                         Map<String, Object> jsVariables) throws IOException {
        try {
            IndentationStructureParser structParser = new IndentationStructureParser();
            List<StructNode> structs = structParser.parse(inputStream, source);

            PageSpec pageSpec = new PageSpec();

            PageSpecHandler pageSpecHandler = new PageSpecHandler(pageSpec, page, tags, excludedTags, contextPath, properties, jsVariables);

            List<StructNode> allProcessedChildNodes = new MacroProcessor(pageSpecHandler).process(structs);
            new PostProcessor(pageSpecHandler).process(allProcessedChildNodes);


            return pageSpecHandler.buildPageSpec();
        } catch (SyntaxException ex) {
            String exceptionSource = "<unknown location>";
            Integer lineNumber = -1;
            if (ex.getLine() != null) {
                exceptionSource = ex.getLine().getText();
                lineNumber = ex.getLine().getNumber();
            }

            throw new FileSyntaxException(ex, exceptionSource, lineNumber);
        }
    }


}
