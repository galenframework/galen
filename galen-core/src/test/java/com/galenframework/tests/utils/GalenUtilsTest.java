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

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.notNullValue;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStream;

import com.galenframework.utils.GalenUtils;

import org.apache.commons.io.FileUtils;
import org.testng.annotations.Test;

public class GalenUtilsTest {

    @Test
    public void shouldReadFile() throws Exception {
        File testFile = File.createTempFile("testFile", ".txt");
        InputStream fileStream = GalenUtils.findFileOrResourceAsStream(testFile.getAbsolutePath());
        assertThat(fileStream, notNullValue());
        FileUtils.deleteQuietly(testFile);
    }

    @Test
    public void shouldReadRelativePath() throws Exception {
        File baseDir = new File(System.getProperty("java.io.tmpdir"));
        File tempDir = new File(baseDir, "galen_test");
        tempDir.mkdir();
        File testFile1 = new File(tempDir, "testFile.txt");
        BufferedWriter output = new BufferedWriter(new FileWriter(testFile1));
        output.write("");
        output.close();
        File testFile2 = new File(baseDir, "testFile2.txt");
        BufferedWriter output2 = new BufferedWriter(new FileWriter(testFile2));
        output2.write("");
        output2.close();
        InputStream fileStream1 = GalenUtils.findFileOrResourceAsStream(testFile1.getAbsolutePath());
        assertThat(fileStream1, notNullValue());
        InputStream fileStream2 = GalenUtils.findFileOrResourceAsStream(tempDir.toString() + File.separator + ".." + File.separator + "testFile2.txt");
        assertThat(fileStream2, notNullValue());
        FileUtils.deleteQuietly(tempDir);
        FileUtils.deleteQuietly(testFile2);
    }
}
