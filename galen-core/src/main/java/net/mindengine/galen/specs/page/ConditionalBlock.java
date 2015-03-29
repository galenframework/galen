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
package net.mindengine.galen.specs.page;

import java.util.List;

public class ConditionalBlock {

    private List<ConditionalBlockStatement> statements;
    private PageSection otherwiseObjects;
    private PageSection bodyObjects;

    public List<ConditionalBlockStatement> getStatements() {
        return this.statements;
    }

    public void setStatements(List<ConditionalBlockStatement> statements) {
        this.statements = statements;
    }


    public PageSection getOtherwiseObjects() {
        return otherwiseObjects;
    }

    public void setOtherwiseObjects(PageSection otherwiseObjects) {
        this.otherwiseObjects = otherwiseObjects;
    }

    public PageSection getBodyObjects() {
        return bodyObjects;
    }

    public void setBodyObjects(PageSection bodyObjects) {
        this.bodyObjects = bodyObjects;
    }
}
