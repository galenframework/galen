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
package com.galenframework.tests.javascript;

import com.galenframework.javascript.JsFunctionLoad;
import com.galenframework.utils.GalenUtils;

import org.junit.runner.RunWith;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.omg.CORBA.portable.InputStream;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by hypery2k on 12.04.2016.
 */
public class JsFunctionLoadTest {

    @Test(dataProvider="paths")
    public void shouldReplacePathCorrectly(String given, String expected) throws Exception{
        JsFunctionLoad jsFunctionLoad = new JsFunctionLoad();
        String result =  jsFunctionLoad.getPath(given, ".");
        assertThat(result,is(expected));
    }

    @DataProvider
    public Object[][] paths() {
        return new Object[][] {
                {"/test","/test"},
                {"C:\\test","C:\\test"}
        };
    }
}
