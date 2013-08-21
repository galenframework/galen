package net.mindengine.galen.suite.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.suite.GalenSuite;

public class RootNode extends Node<List<GalenSuite>> {

    public RootNode() {
        super(null);
    }
    
    
    @Override
    public Node<?> findProcessingNodeByLevel(int level) {
        return this;
    }


    @Override
    public Node<?> processNewNode(String line) {
        if (line.startsWith(" ")) {
            throw new SuiteReaderException("Should not start with space");
        }
        
        SuiteNode suiteNode = new SuiteNode(line);
        add(suiteNode);
        return suiteNode;
    }


    @Override
    public List<GalenSuite> build(Context context) {
        List<GalenSuite> suites = new LinkedList<GalenSuite>();
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof SuiteNode) {
                SuiteNode suiteNode = (SuiteNode)childNode;
                suites.add(suiteNode.build(context));
            }
        }
        return suites;
    }

}
