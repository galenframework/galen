package net.mindengine.galen.suite.reader;

import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.SyntaxException;
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
    public Node<?> processNewNode(Line line) {
        if (line.startsWith(" ")) {
            throw new SyntaxException(line, "Should not start with space");
        }
        
        SuiteNode suiteNode = new SuiteNode(line);
        add(suiteNode);
        return suiteNode;
    }


    @Override
    public List<GalenSuite> build(BashTemplateContext context) {
        rearrangeNodes();
        
        List<GalenSuite> suites = new LinkedList<GalenSuite>();
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof SuiteNode) {
                SuiteNode suiteNode = (SuiteNode)childNode;
                suites.add(suiteNode.build(context));
            }
            else if (childNode instanceof ParameterizedNode) {
                ParameterizedNode parameterizedNode = (ParameterizedNode)childNode;
                suites.addAll(parameterizedNode.build(context));
            }
            else {
                childNode.build(context);
            }
        }
        return suites;
    }


    private void rearrangeNodes() {
        Iterator<Node<?>> it = getChildNodes().iterator();
        
        ParameterizedNode currentParameterizedNode = null;
        
        while(it.hasNext()) {
            Node<?> node = it.next();
            
            if (node instanceof ParameterizedNode) {
                if (currentParameterizedNode != null) {
                    currentParameterizedNode.setToParameterize(node);
                    currentParameterizedNode = (ParameterizedNode) node;
                    it.remove();
                }
                else {
                    currentParameterizedNode = (ParameterizedNode) node;
                }
            }
            else if (node instanceof SuiteNode) {
                if (currentParameterizedNode != null) {
                    currentParameterizedNode.setToParameterize(node);
                    it.remove();
                    currentParameterizedNode = null;
                }
            }
        }
    }

}
