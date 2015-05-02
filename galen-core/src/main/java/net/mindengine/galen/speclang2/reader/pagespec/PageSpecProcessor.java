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
package net.mindengine.galen.speclang2.reader.pagespec;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.parser.StructNode;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.reader.StringCharReader;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.suite.reader.Line;

public class PageSpecProcessor {
    private final PageSpec pageSpec;
    private final Browser browser;

    public PageSpecProcessor(PageSpec pageSpec, Browser browser) {
        this.pageSpec = pageSpec;
        this.browser = browser;
    }

    public PageSpec buildPageSpec() {
        return pageSpec;
    }

    public void processSpecialInstruction(StructNode structNode) {
        StringCharReader reader = new StringCharReader(structNode.getName());
        String name = reader.readWord();

        if ("@objects".equals(name)) {
            new ObjectDefinitionProcessor(this).process(reader, structNode);
        } else {
            throw  new SyntaxException(new Line(structNode.getSource(), structNode.getFileLineNumber()), "Unknown special instruction: " + name);
        }

    }

    public void addObjectToSpec(String objectName, Locator locator) {
        pageSpec.getObjects().put(objectName, locator);
    }

    public Browser getBrowser() {
        return browser;
    }
}
