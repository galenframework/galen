/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.junit;

import java.lang.reflect.Field;

import net.mindengine.galen.api.GalenExecutor;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.google.inject.Module;

/**
 * A test runner for WebDriver-based layout tests. This test runner initializes a WebDriver instance before running the
 * tests in their order of appearance. At the end of the tests, it closes and quits the WebDriver instance. The test
 * runner will by default produce reports in JSON and HTML.
 *
 */
public class GalenRunner extends BlockJUnit4ClassRunner {

    private static final Logger LOG = LoggerFactory.getLogger(GalenRunner.class);

    private GalenExecutor executor;

    private Injector injector;

    public GalenRunner(Class<?> klass) throws InitializationError {
        super(klass);
        injector = createInjector();
    }

    @Override
    public Object createTest() throws Exception {
        Object obj = super.createTest();
        injector.injectMembers(obj);
        scanFieldsForExecutor(obj, obj.getClass().getDeclaredFields());
        if (this.executor == null) {
            // search super class too
            scanFieldsForExecutor(obj, obj.getClass().getSuperclass().getDeclaredFields());
        }
        return obj;
    }

    public void scanFieldsForExecutor(final Object obj, final Field[] fields) {
        for (Field field : fields) {
            if (field.getType() == GalenExecutor.class) {
                field.setAccessible(true);
                try {
                    this.executor = (GalenExecutor) field.get(obj);
                } catch (IllegalArgumentException e) {
                    LOG.error("Argument error during preparing injection", e);
                } catch (IllegalAccessException e) {
                    LOG.error("Illegal access error during preparing injection", e);
                }
                field.setAccessible(false);
            }
        }
    }

    private Injector createInjector() throws InitializationError {
        Module module = null;
        try {
            module = (Module) (GalenJunitModule.class).newInstance();
        } catch (InstantiationException e) {
            throw new InitializationError(e);
        } catch (IllegalAccessException e) {
            throw new InitializationError(e);
        }
        return Guice.createInjector(module);
    }

    /**
     * @see org.junit.runners.BlockJUnit4ClassRunner#runChild(org.junit.runners.model.FrameworkMethod,
     *      org.junit.runner.notification.RunNotifier)
     */
    @Override
    protected void runChild(FrameworkMethod method, RunNotifier notifier) {
        try {
            createTest();
            super.runChild(method, notifier);
            notifier.addListener(new JUnitStepListener());
        } catch (Exception e) {
            notifier.fireTestFailure(new Failure(Description.createSuiteDescription(notifier.getClass()), e));
        } finally {
            executor.quitDriver();
        }
    }
}
