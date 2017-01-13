package com.galenframework.suite.actions.mutation;


import java.util.List;

public class PageMutation {
    private final String name;
    private final List<PageElementMutation> pageElementMutations;

    public PageMutation(String name, List<PageElementMutation> pageElementMutations) {
        this.name = name;
        this.pageElementMutations = pageElementMutations;
    }

    public String getName() {
        return name;
    }

    public List<PageElementMutation> getPageElementMutations() {
        return pageElementMutations;
    }
}
