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
package net.mindengine.galen.suite.reader;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.GalenPageActionReader;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.suite.GalenPageAction;

public class ActionNode extends Node<GalenPageAction> {

    public ActionNode(Line line) {
        super(line);
    }

    @Override
    public Node<?> processNewNode(Line line) {
        throw new SyntaxException(line, "Incorrect nesting");
    }

    @Override
    public GalenPageAction build(BashTemplateContext context) {
        try {
            String actionText = context.process(getArguments());
            GalenPageAction pageAction = GalenPageActionReader.readFrom(actionText);
            pageAction.setOriginalCommand(actionText);
            return pageAction;
        }
        catch(SyntaxException e) {
            e.setLine(getLine());
            throw e;
        }
    }

}
