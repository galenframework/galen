package net.mindengine.galen.suite.actions;

import java.util.List;

import net.mindengine.galen.suite.GalenPageAction;

public class GalenPageActionCheck implements GalenPageAction {

    private List<String> specs;
    private List<String> includedTags;
    private List<String> ecludedTags;

    @Override
    public void execute() {
        // TODO Auto-generated method stub
        
    }

    public GalenPageActionCheck withSpecs(List<String> specFilePaths) {
        this.setSpecs(specFilePaths);
        return this;
    }

    public List<String> getSpecs() {
        return specs;
    }

    public void setSpecs(List<String> specs) {
        this.specs = specs;
    }

    public GalenPageActionCheck withIncludedTags(List<String> includedTags) {
        this.setIncludedTags(includedTags);
        return this;
    }

    public List<String> getIncludedTags() {
        return includedTags;
    }

    public void setIncludedTags(List<String> includedTags) {
        this.includedTags = includedTags;
    }

    public GalenPageActionCheck withExcludedTags(List<String> excludedTags) {
        this.setEcludedTags(excludedTags);
        return this;
    }

    public List<String> getEcludedTags() {
        return ecludedTags;
    }

    public void setEcludedTags(List<String> ecludedTags) {
        this.ecludedTags = ecludedTags;
    }

}
