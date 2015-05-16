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
import java.lang.reflect.InvocationTargetException;

import net.mindengine.galen.api.GalenExecutor;
import net.mindengine.galen.api.Inject;

import org.junit.runner.Description;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A test runner for WebDriver-based layout tests. This test runner initializes a WebDriver instance before running the
 * tests in their order of appearance. At the end of the tests, it closes and quits the WebDriver instance. The test
 * runner will by default produce reports in JSON and HTML.
 *
 */
public class GalenRunner extends BlockJUnit4ClassRunner {

    private static final Logger LOG = LoggerFactory.getLogger(GalenRunner.class);

    private GalenExecutor executor;

    public GalenRunner(Class<?> klass) throws InitializationError {
        super(klass);
    }

    @Override
    public Object createTest() throws Exception {
        LOG.debug("Creating Galen Executor");
        Object obj = super.createTest();
        scanFieldsForExecutor(obj, obj.getClass().getDeclaredFields());
        if (this.executor == null) {
            // search super class too
            scanFieldsForExecutor(obj, obj.getClass().getSuperclass().getDeclaredFields());
        }
        LOG.debug("Creation of Galen Executor was successfull");
        return obj;
    }

    public void scanFieldsForExecutor(final Object obj, final Field[] fields) throws InitializationError {
        for (Field field : fields) {
            if (field.getType() == GalenExecutor.class) {
                field.setAccessible(true);
                try {
                    if(field.isAnnotationPresent(Inject.class)){
                        Inject injectAnnotation = field.getAnnotation(Inject.class);
                        Class<? extends GalenExecutor> clazz= injectAnnotation.implementation();
                    	this.executor= (GalenExecutor)  clazz.getConstructors()[0].newInstance();
                    	field.set(obj,this.executor);
                    }
                } catch (IllegalArgumentException e) {
                    throw new InitializationError(e);
                } catch (IllegalAccessException e) {
                    throw new InitializationError(e);
                } catch (InstantiationException e) {
                    throw new InitializationError(e);
				} catch (InvocationTargetException e) {
                    throw new InitializationError(e);
				} catch (SecurityException e) {
                    throw new InitializationError(e);
				}
                field.setAccessible(false);
            }
        }
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
