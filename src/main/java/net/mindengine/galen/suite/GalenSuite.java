package net.mindengine.galen.suite;

import java.util.List;

public class GalenSuite {
    
    private String name;
    private List<GalenPageTest> pageTests;
    

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<GalenPageTest> getPageTests() {
        return pageTests;
    }

    public void setPageTests(List<GalenPageTest> pageTests) {
        this.pageTests = pageTests;
    }
    

}
