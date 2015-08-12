/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://galenframework.com
 * <p/>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p/>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p/>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package com.galenframework.junit;

import org.junit.runner.notification.RunNotifier;
import org.junit.runners.Parameterized;

/**
 * Galen JUnit test runner
 *
 * @author mreinhardt
 */
public class GalenReportTestRunner extends Parameterized {

    private RunNotifier notifier;

    /**
     * @param klass
     * @throws Throwable
     */
    public GalenReportTestRunner(Class<?> klass) throws Throwable {
        super(klass);
    }

    @Override
    public void run(final RunNotifier pRunNotifier) {
        this.notifier = pRunNotifier;
        notifier.addFirstListener(new JUnitStepListener());
        super.run(notifier);
    }
}
