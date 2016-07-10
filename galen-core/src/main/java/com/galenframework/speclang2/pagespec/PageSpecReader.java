/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.speclang2.pagespec;

import com.galenframework.page.Page;
import com.galenframework.parser.IndentationStructureParser;
import com.galenframework.parser.StructNode;
import com.galenframework.parser.SyntaxException;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.utils.GalenUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;
import java.util.Map;
import java.util.Properties;

public class PageSpecReader {

    public PageSpec read(String path, Page page,
                         SectionFilter sectionFilter,
                         Properties properties,
                         Map<String, Object> jsVariables, Map<String, Locator> objects) throws IOException {

        String contextPath = GalenUtils.getParentForFile(path);

        InputStream stream = GalenUtils.findFileOrResourceAsStream(path);
        if (stream == null) {
            throw new FileNotFoundException(path);
        }
        return read(stream, path, contextPath, page, sectionFilter, properties, jsVariables, objects);
    }

    public PageSpec read(InputStream inputStream, String source,
                         String contextPath,
                         Page page,
                         SectionFilter sectionFilter,
                         Properties properties,
                         Map<String, Object> jsVariables, Map<String, Locator> objects) throws IOException {
        IndentationStructureParser structParser = new IndentationStructureParser();
        List<StructNode> structs = structParser.parse(inputStream, source);

        PageSpec pageSpec = new PageSpec(objects);

        PageSpecHandler pageSpecHandler = new PageSpecHandler(pageSpec, page, sectionFilter, contextPath, properties, jsVariables);

        List<StructNode> allProcessedChildNodes = new MacroProcessor(pageSpecHandler).process(structs);
        new PostProcessor(pageSpecHandler).process(allProcessedChildNodes);

        return pageSpecHandler.buildPageSpec();
    }


}
