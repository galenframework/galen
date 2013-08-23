package net.mindengine.galen.suite.reader;

import net.mindengine.galen.parser.GalenPageActionReader;
import net.mindengine.galen.suite.GalenPageAction;

public class ActionNode extends Node<GalenPageAction> {

    public ActionNode(String arguments) {
        super(arguments);
    }

    @Override
    public Node<?> processNewNode(String line) {
        throw new SuiteReaderException("Incorrect nesting");
    }

    @Override
    public GalenPageAction build(Context context) {
        String actionText = context.process(getArguments());
        return GalenPageActionReader.readFrom(actionText);
    }

}
