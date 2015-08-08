package com.galenframework.tests.javascript;

import com.galenframework.components.JsTestRegistry;
import com.galenframework.javascript.GalenJsExecutor;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;

public class GalenJsExecutorTest {

    @Test
    public void loadFunction_shouldAlsoTake_arrayOfStrings() {
        JsTestRegistry.get().clear();

        GalenJsExecutor js = new GalenJsExecutor();
        js.runJavaScriptFromFile("/javascript/load-array.js");

        assertThat(JsTestRegistry.get().getEvents(), contains("Loaded script from 1 file",
                "Loaded script from 2 file"));
    }


}
