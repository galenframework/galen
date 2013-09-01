package net.mindengine.galen.suite.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.GalenPageTestReader;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;

public class PageNode extends Node<GalenPageTest> {

    public PageNode(Line line) {
        super(line);
    }

    @Override
    public Node<?> processNewNode(Line line) {
        ActionNode actionNode = new ActionNode(line);
        add(actionNode);
        return actionNode;
    }

    @Override
    public GalenPageTest build(BashTemplateContext context) {
        GalenPageTest pageTest;
        try {
            pageTest = GalenPageTestReader.readFrom(context.process(getArguments()));
        }
        catch (SyntaxException e) {
            e.setLine(getLine());
            throw e;
        }
        
        List<GalenPageAction> actions = new LinkedList<GalenPageAction>();
        pageTest.setActions(actions);
        
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof ActionNode) {
                ActionNode actionNode = (ActionNode)childNode;
                actions.add(actionNode.build(context));
            }
        }
        
        return pageTest;
    }


}
