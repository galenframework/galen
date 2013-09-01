package net.mindengine.galen.suite.reader;

import net.mindengine.galen.parser.BashTemplateContext;

public class SetNode extends Node<Void> {

    public SetNode(Line line) {
        super(line);
    }

    @Override
    public Void build(BashTemplateContext context) {
        String line = context.process(getArguments());
        int indexOfFirstSpace = getArguments().indexOf(' ');
        
        if (indexOfFirstSpace > 0) {
            String name = line.substring(0, indexOfFirstSpace);
            String value = line.substring(indexOfFirstSpace).trim();
            context.putValue(name, value);
        }
        else {
            context.putValue(line, "");
        }
        
        for (Node<?> childNode : getChildNodes()) {
            if (childNode instanceof SetNode) {
                SetNode setNode = (SetNode)childNode;
                setNode.build(context);
            }
        }
        
        return null;
    }

    @Override
    public Node<?> processNewNode(Line line) {
        add(new SetNode(line.trim()));
        return this;
    }

}
