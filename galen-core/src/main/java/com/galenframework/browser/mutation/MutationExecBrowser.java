/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
* 
* Licensed under the Apache License, Version 2.0 (the "License");
* you may not use this file except in compliance with the License.
* You may obtain a copy of the License at
* 
*   http://www.apache.org/licenses/LICENSE-2.0
* 
* Unless required by applicable law or agreed to in writing, software
* distributed under the License is distributed on an "AS IS" BASIS,
* WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
* See the License for the specific language governing permissions and
* limitations under the License.
******************************************************************************/
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
