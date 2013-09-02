package net.mindengine.galen.suite.reader;

import net.mindengine.galen.parser.BashTemplateContext;
import net.mindengine.galen.parser.GalenPageActionReader;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.suite.GalenPageAction;

public class ActionNode extends Node<GalenPageAction> {

    public ActionNode(Line line) {
        super(line);
    }

    @Override
    public Node<?> processNewNode(Line line) {
        throw new SyntaxException(line, "Incorrect nesting");
    }

    @Override
    public GalenPageAction build(BashTemplateContext context) {
        try {
            String actionText = context.process(getArguments());
            return GalenPageActionReader.readFrom(actionText);
        }
        catch(SyntaxException e) {
            e.setLine(getLine());
            throw e;
        }
    }

}
