/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
import com.galenframework.specs.page.Locator;
import com.galenframework.suite.actions.mutation.AreaMutation;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class MutationExecPageProxy implements InvocationHandler {
    private MutationExecBrowser mutationExecBrowser;
    private final Map<String, PageElement> recordedElements;
    private final List<Method> recordingMethods;
    private final Page originPage;

    public MutationExecPageProxy(MutationExecBrowser mutationExecBrowser, Browser originBrowser, Map<String, PageElement> recordedElements) {
        this.mutationExecBrowser = mutationExecBrowser;
        this.recordedElements = recordedElements;

        this.originPage = originBrowser.getPage();
        this.recordingMethods = initRecordingMethods();
    }

    private List<Method> initRecordingMethods() {
        try {
            return asList(
                Page.class.getMethod("getObject", String.class, Locator.class),
                Page.class.getMethod("getSpecialObject", String.class)
            );
        } catch (Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (recordingMethods.contains(method)) {
            String elementName = (String)args[0];

            PageElement element = recordedElements.get(elementName);

            AreaMutation activeMutation = mutationExecBrowser.getActiveMutations().get(elementName);
            if (activeMutation != null) {
                return new MutatedPageElement(element, activeMutation);
            } else {
                return element;
            }
        } else {
            return method.invoke(originPage, args);
        }
    }
}
