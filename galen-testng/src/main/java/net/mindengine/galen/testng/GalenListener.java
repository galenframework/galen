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
package net.mindengine.galen.testng;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import net.mindengine.galen.api.GalenExecutor;
import net.mindengine.galen.api.GalenReportsContainer;
import net.mindengine.galen.api.Inject;
import net.mindengine.galen.reports.GalenTestInfo;
import net.mindengine.galen.reports.HtmlReportBuilder;
import net.mindengine.galen.reports.model.FileTempStorage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.testng.IInvokedMethod;
import org.testng.IInvokedMethodListener;
import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ITestResult;
import org.testng.TestNGException;
import org.testng.xml.XmlSuite;

public class GalenListener implements IReporter, IInvokedMethodListener {

    private static final Logger LOG = LoggerFactory.getLogger(GalenListener.class);

    private GalenExecutor executor;

    @Override
    public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> iSuites, String s) {
        LOG.info("Generating Galen Html reports");
        List<GalenTestInfo> tests = GalenReportsContainer.get().getAllTests();
        try {
            // TODO move to variable
            new HtmlReportBuilder().build(tests, "target/galen-html-reports");
            cleanData(tests);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Removes temporary test data
     * 
     * @param testInfos
     */
    private void cleanData(List<GalenTestInfo> testInfos) {
        for (GalenTestInfo testInfo : testInfos) {
            if (testInfo.getReport() != null) {
                try {
                    FileTempStorage storage = testInfo.getReport().getFileStorage();
                    if (storage != null) {
                        storage.cleanup();
                    }
                } catch (Exception e) {
                    LOG.error("Unkown error during report cleaning", e);
                }
            }
        }
    }

    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        LOG.debug("Creating Galen Executor");
        try {
            Object obj = method.getTestMethod().getInstance();
            scanFieldsForExecutor(obj, obj.getClass().getDeclaredFields());
            if (this.executor == null) {
                // search super class too
                scanFieldsForExecutor(obj, obj.getClass().getSuperclass().getDeclaredFields());
            }
        } catch (SecurityException e) {
            LOG.error("Access error during preparing injection", e);
            throw new TestNGException(e);
        } catch (Exception e) {
            LOG.error("Unkown error during preparing injection", e);
            throw new TestNGException(e);
        }
        LOG.debug("Creation of Galen Executor was successfull");
    }

    public void scanFieldsForExecutor(final Object obj, final Field[] fields) {
        for (Field field : fields) {
            if (field.getType() == GalenExecutor.class) {
                field.setAccessible(true);
                try {
                    if(field.isAnnotationPresent(Inject.class)){
                    	Class<? extends GalenExecutor> clazz= ((Inject)field.getAnnotationsByType(Inject.class)[0]).implementation();
                    	this.executor= (GalenExecutor)  clazz.getConstructors()[0].newInstance();
                    	field.set(obj,this.executor);
                    }
                } catch (IllegalArgumentException e) {
                    throw new TestNGException(e);
                } catch (IllegalAccessException e) {
                    throw new TestNGException(e);
                } catch (InstantiationException e) {
                    throw new TestNGException(e);
				} catch (InvocationTargetException e) {
                    throw new TestNGException(e);
				} catch (SecurityException e) {
                    throw new TestNGException(e);
				}
                field.setAccessible(false);
            }
        }
    }

    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        executor.quitDriver();
    }
}
