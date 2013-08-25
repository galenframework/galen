package net.mindengine.galen.suite;

import java.util.List;

import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.suite.actions.GalenPageActionInjectJavascript;
import net.mindengine.galen.suite.actions.GalenPageActionRunJavascript;

public class GalenPageActions {

    public static GalenPageActionInjectJavascript injectJavascript(String javascriptFilePath) {
        return new GalenPageActionInjectJavascript(javascriptFilePath);
    }

    public static GalenPageActionCheck check(List<String> specFilePaths) {
        return new GalenPageActionCheck().withSpecs(specFilePaths);
    }

    public static GalenPageActionRunJavascript runJavascript(String javascriptPath) {
        return new GalenPageActionRunJavascript(javascriptPath);
    }

}
