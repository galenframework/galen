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

import java.util.LinkedList;

import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.page.ConditionalBlock;
import net.mindengine.galen.specs.page.ConditionalBlockStatement;
import net.mindengine.galen.specs.page.PageSection;

public class StateDoingConditionalBlocks extends State {
    
    private ConditionalBlock conditionalBlock;
    private StateDoingSection currentSectionState;
    
    private enum STATE {
        STATEMENT, BODY, OTHERWISE
    }
    
    private STATE state = STATE.STATEMENT;
    
    
    
    public StateDoingConditionalBlocks(boolean inverted) {
        conditionalBlock = new ConditionalBlock();
        conditionalBlock.setStatements(new LinkedList<ConditionalBlockStatement>());
        
        startNewStatement(inverted);
    }

    @Override
    public void process(String line) {
        currentSectionState.process(line);
    }

    public void startNewStatement(boolean inverted) {
        if (state != STATE.STATEMENT) {
            throw new SyntaxException("Wrong place for this statement");
        }
        
        ConditionalBlockStatement currentStatement = new ConditionalBlockStatement();
        currentStatement.setInverted(inverted);
        conditionalBlock.getStatements().add(currentStatement);
        
        PageSection currentSection = new PageSection();
        currentSectionState = new StateDoingSection(currentSection);
        
        currentStatement.setObjects(currentSection.getObjects());
    }

    public void startBody() {
        if (state != STATE.STATEMENT) {
            throw new SyntaxException("Wrong place for this statement");
        }
        
        PageSection currentSection = new PageSection();
        currentSectionState = new StateDoingSection(currentSection);
        
        conditionalBlock.setBodyObjects(currentSection.getObjects());
        state = STATE.BODY;
    }

    public void startOtherwise() {
        if (state != STATE.BODY) {
            throw new SyntaxException("Wrong place for this statement");
        }
        
        PageSection currentSection = new PageSection();
        currentSectionState = new StateDoingSection(currentSection);
        state = STATE.BODY;
        
        conditionalBlock.setOtherwiseObjects(currentSection.getObjects());
    }

    public ConditionalBlock build() {
        if (state == STATE.STATEMENT) {
            throw new SyntaxException("There is no body defined for this conditional block");
        }
        return conditionalBlock;
    }

}
