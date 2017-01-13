package com.galenframework.browser.mutation;

import com.galenframework.browser.Browser;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.specs.page.Locator;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;

import static java.util.Arrays.asList;

public class MutationRecordPageProxy implements InvocationHandler {

    private final Browser originBrowser;
    private final Map<String, PageElement> elementStorage;
    private final Page originPage;
    private final List<Method> recordingMethods;

    public MutationRecordPageProxy(Browser originBrowser, Map<String, PageElement> elementStorage) {
        this.originBrowser = originBrowser;
        this.elementStorage = elementStorage;
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
        Object result = method.invoke(originPage, args);
        if (recordingMethods.contains(method)) {
            elementStorage.put((String)args[0], new StalePageElement((PageElement)result));
        }
        return result;
    }

}
