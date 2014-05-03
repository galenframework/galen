package net.mindengine.galen.reports;

public class TestStatistic {

    private int passed = 0;
    private int errors = 0;
    private int warnings = 0;
    private int total = 0;
    
    public int getPassed() {
        return passed;
    }
    public void setPassed(int passed) {
        this.passed = passed;
    }
    public int getErrors() {
        return errors;
    }
    public void setErrors(int errors) {
        this.errors = errors;
    }
    public int getWarnings() {
        return warnings;
    }
    public void setWarnings(int warnings) {
        this.warnings = warnings;
    }
    public int getTotal() {
        return total;
    }
    public void setTotal(int total) {
        this.total = total;
    }
}
