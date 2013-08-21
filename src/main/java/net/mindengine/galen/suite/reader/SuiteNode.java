package net.mindengine.galen.suite.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.suite.GalenSuite;

public class SuiteNode extends Node<GalenSuite> {


    public SuiteNode(String suiteName) {
        super(suiteName);
    }

    @Override
    public Node<?> processNewNode(String line) {
        PageNode pageNode = new PageNode(line);
        add(pageNode);
        return pageNode;
    }

    @Override
    public GalenSuite build(Context context) {
        GalenSuite suite = new GalenSuite();
        List<GalenPageTest> pageTests = new LinkedList<GalenPageTest>();
       
        suite.setName(context.process(getArguments()));
        suite.setPageTests(pageTests);
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof PageNode) {
                PageNode pageNode = (PageNode) childNode;
                pageTests.add(pageNode.build(context));
            }
        }
        
        return suite;
    }
    

}
