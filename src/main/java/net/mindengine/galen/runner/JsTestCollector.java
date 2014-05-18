package net.mindengine.galen.runner;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

import net.mindengine.galen.javascript.GalenJsExecutor;
import net.mindengine.galen.tests.GalenTest;

public class JsTestCollector {

    private List<GalenTest> collectedTests = new LinkedList<GalenTest>();

    public JsTestCollector(List<GalenTest> tests) {
        this.collectedTests = tests;
    }

    public JsTestCollector() {
    }

    public void execute(File file) throws IOException {
        GalenJsExecutor js = new GalenJsExecutor();
        js.putObject("_galenCore", this);
        
        Reader scriptFileReader = new FileReader(file);
        js.eval(scriptFileReader, file.getAbsolutePath());
    }

    public void addTest(GalenTest test) {
        this.collectedTests.add(test);
    }
    
    public List<GalenTest> getCollectedTests() {
        return this.collectedTests;
    }

}
