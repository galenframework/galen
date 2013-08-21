package net.mindengine.galen.suite.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;

public class PageNode extends Node<GalenPageTest> {

    public PageNode(String pageArguments) {
        super(pageArguments);
    }

    @Override
    public Node<?> processNewNode(String line) {
        ActionNode actionNode = new ActionNode(line);
        add(actionNode);
        return actionNode;
    }

    @Override
    public GalenPageTest build(Context context) {
        GalenPageTest pageTest = GalenPageTest.readFrom(context.process(getArguments()));
        
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
