package net.mindengine.galen.suite.reader;

import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.SyntaxException;

public abstract class Node<T> {

    private int level = 0;
    private Node<?> parent;
    private Line line;

    public Node(Line line) {
        this.setLine(line);
    }
    
    private List<Node<?>> childNodes = new LinkedList<Node<?>>();
    
    protected void add(Node<?> childNode) {
        childNode.parent = this;
        childNode.level = this.level + 1;
        getChildNodes().add(childNode);
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
            throw new SyntaxException(getLine(), "Wrong nesting");
        }
    }

    public abstract Node<?> processNewNode(Line line);

    public List<Node<?>> getChildNodes() {
        return childNodes;
    }



    public Line getLine() {
        return line;
    }
    
    public String getArguments() {
        return line.getText();
    }



    public void setLine(Line line) {
        this.line = line;
    }

}
