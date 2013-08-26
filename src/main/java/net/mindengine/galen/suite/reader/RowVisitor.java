package net.mindengine.galen.suite.reader;

import java.util.Map;

public interface RowVisitor {

    void visit(Map<String, String> values);

}
