/*******************************************************************************
 * Copyright 2015 Ivan Shubin http://galenframework.com
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

package com.galenframework.tests.utils;

import com.galenframework.utils.VersionUtil;
import org.openqa.selenium.By;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

/**
 * @author hypery2k
 */
public class VersionTest {

    @Test
    public void shouldAlwaysReturnStringAndNotNull() throws Exception {
        String customVersion = VersionUtil.getVersion(By.class, "Custom-Version");
        assertThat(customVersion, not(nullValue()));
    }

    @Test
    public void shouldShowSeleniumVersion() throws Exception {
        String seleniumVersion = VersionUtil.getVersion(By.class, "Selenium-Version");
        assertThat(seleniumVersion, not(isEmptyString()));
    }
}
