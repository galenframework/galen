package com.galenframework.browser.mutation;

import com.galenframework.browser.Browser;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.suite.actions.mutation.AreaMutation;

import java.awt.*;
import java.lang.reflect.Proxy;
import java.util.Map;

public class MutationExecBrowser implements Browser {

    private Map<String, AreaMutation> activeMutations;
    private Browser originBrowser;
    private Map<String, PageElement> recordedElements;
    private Page cachedPage;

    public MutationExecBrowser(Browser originBrowser, Map<String, PageElement> recordedElements) {
        super();
        this.originBrowser = originBrowser;
        this.recordedElements = recordedElements;
    }

    public void setActiveMutations(Map<String, AreaMutation> activeMutations) {
        this.activeMutations = activeMutations;
    }


    @Override
    public void quit() {
    }

    @Override
    public void changeWindowSize(Dimension screenSize) {
    }

    @Override
    public void load(String url) {
    }

    @Override
    public Object executeJavascript(String javascript) {
        return null;
    }

    @Override
    public Page getPage() {
        if (cachedPage == null) {
            cachedPage = (Page) Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class<?>[]{Page.class},
                new MutationExecPageProxy(this, originBrowser, recordedElements)
            );
        }
        return cachedPage;
    }

    @Override
    public void refresh() {
    }

    @Override
    public String getUrl() {
        return originBrowser.getUrl();
    }

    @Override
    public Dimension getScreenSize() {
        return originBrowser.getScreenSize();
    }

    public Map<String, AreaMutation> getActiveMutations() {
        return activeMutations;
    }
}
