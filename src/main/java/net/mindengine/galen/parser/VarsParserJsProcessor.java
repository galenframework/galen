package net.mindengine.galen.parser;

import net.mindengine.galen.javascript.JsFunctionLoad;
import net.mindengine.galen.suite.reader.Context;
import net.mindengine.galen.utils.GalenUtils;
import org.mozilla.javascript.BaseFunction;
import org.mozilla.javascript.ImporterTopLevel;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import java.io.File;
import java.util.Map;

public class VarsParserJsProcessor {
    private final VarsParserJsFunctions jsFunctions;

    private Context varsContext;
    private org.mozilla.javascript.Context cx;
    private ImporterTopLevel scope;

    public VarsParserJsProcessor(Context varsContext, VarsParserJsFunctions jsFunctions) {
        this.varsContext = varsContext;
        this.jsFunctions = jsFunctions;
        initJsProcessor();
    }

    private void initJsProcessor() {
        this.cx = org.mozilla.javascript.Context.enter();
        this.scope = new ImporterTopLevel(cx);

        if (jsFunctions != null) {
            scope.defineProperty("count", new BaseFunction() {
                @Override
                public Object call(org.mozilla.javascript.Context cx, Scriptable scope, Scriptable thisObj, Object[] args) {
                    if (args.length == 0 || !(args[0] instanceof String)) {
                        throw new IllegalArgumentException("Should take string argument");
                    }
                    return jsFunctions.count((String)args[0]);
                }
            }, ScriptableObject.DONTENUM);

            scope.defineProperty("load", new JsFunctionLoad(), ScriptableObject.DONTENUM);
        }

    }

    public String process(String expression) {
        resetAllVariablesFromContext();

        try {
            Object returnedObject = cx.evaluateString(scope, expression, "<cmd>", 1, null);
            if (returnedObject != null) {
                if (returnedObject instanceof Double) {
                    return Integer.toString(((Double) returnedObject).intValue());
                } else if (returnedObject instanceof Float) {
                    return Integer.toString(((Float) returnedObject).intValue());
                } else return returnedObject.toString();
            } else return null;
        }
        catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    private void resetAllVariablesFromContext() {
        if (varsContext != null) {
            for (Map.Entry<String, Object> parameter : varsContext.getParameters().entrySet()) {
                if (!conflictsWithFunctionNames(parameter.getKey())) {
                    ScriptableObject.putProperty(scope, parameter.getKey(), parameter.getValue());
                }
            }
        }
    }

    private boolean conflictsWithFunctionNames(String name) {
        if (name.equals("count")) {
            return true;
        }
        return false;
    }
    public Context getVarsContext() {
        return varsContext;
    }
    public void setVarsContext(Context varsContext) {
        this.varsContext = varsContext;
    }

    public void runJavascriptFromFile(String filePath, String contextPath) {
        String fullPath = filePath;
        if (!filePath.startsWith("/") && contextPath!= null && !contextPath.isEmpty()) {
            fullPath = contextPath + File.separator + filePath;
        }
        new JsFunctionLoad().load(fullPath, this.cx, this.scope);
    }
}
