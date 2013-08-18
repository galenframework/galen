package net.mindengine.galen.suite;

import java.util.List;

import net.mindengine.galen.suite.actions.GalenPageActionCheck;
import net.mindengine.galen.suite.actions.GalenPageActionInjectJavascript;
import net.mindengine.galen.suite.actions.GalenPageActionSeleniumJS;

public class GalenPageActions {

    public static GalenPageActionInjectJavascript injectJavascript(String javascriptFilePath) {
        // TODO Auto-generated method stub
        return null;
    }

    public static GalenPageActionCheck check(List<String> specFilePaths) {
        return new GalenPageActionCheck().withSpecs(specFilePaths);
    }

    public static GalenPageActionSeleniumJS seleniumJS(String javascriptPath) {
        return new GalenPageActionSeleniumJS(javascriptPath);
    }

}
