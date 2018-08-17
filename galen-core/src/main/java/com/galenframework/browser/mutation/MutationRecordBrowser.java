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

import java.awt.*;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

public class MutationRecordBrowser implements Browser {

    private Browser originBrowser;
    private Map<String, PageElement> recordedElements = new HashMap<>();

    private Page cachedPage = null;

    public MutationRecordBrowser(Browser originBrowser) {
        this.originBrowser = originBrowser;
    }

    @Override
    public void quit() {
        originBrowser.quit();
    }

    @Override
    public void changeWindowSize(Dimension screenSize) {
        originBrowser.changeWindowSize(screenSize);
    }

    @Override
    public void load(String url) {
        originBrowser.load(url);
    }

    @Override
    public Object executeJavascript(String javascript) {
        return originBrowser.executeJavascript(javascript);
    }

    @Override
    public Page getPage() {
        if (cachedPage == null) {
            cachedPage = (Page) Proxy.newProxyInstance(
                ClassLoader.getSystemClassLoader(),
                new Class<?>[]{Page.class},
                new MutationRecordPageProxy(originBrowser, recordedElements)
            );
        }
        return cachedPage;
    }

    @Override
    public void refresh() {
        originBrowser.refresh();
    }

    @Override
    public String getUrl() {
        return originBrowser.getUrl();
    }

    @Override
    public Dimension getScreenSize() {
        return originBrowser.getScreenSize();
    }

    public Map<String, PageElement> getRecordedElements() {
        return recordedElements;
    }
}
