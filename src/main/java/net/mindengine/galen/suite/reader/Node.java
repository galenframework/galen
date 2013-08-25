package net.mindengine.galen.suite.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.parser.BashTemplateContext;

public abstract class Node<T> {

    private int level = 0;
    private Node<?> parent;
    private String arguments;

    public Node(String arguments) {
        this.setArguments(arguments);
    }
    
    private List<Node<?>> childNodes = new LinkedList<Node<?>>();
    
    protected void add(Node<?> childNode) {
        childNode.parent = this;
        childNode.level = this.level + 1;
        getChildNodes().add(childNode);
    }

    public String getArguments() {
        return arguments;
    }

    public void setArguments(String arguments) {
        this.arguments = arguments;
    }

    
    public abstract T build(BashTemplateContext context);

    public Node<?> findProcessingNodeByLevel(int level) {
        if (this.level == level) {
            return this;
        }
        else if (this.level > level) {
            return parent.findProcessingNodeByLevel(level);
        }
        else {
            throw new RuntimeException("Wrong nesting");
        }
    }

    public abstract Node<?> processNewNode(String line);

    public List<Node<?>> getChildNodes() {
        return childNodes;
    }

}
