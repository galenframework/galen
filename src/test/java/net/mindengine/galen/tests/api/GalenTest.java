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
package net.mindengine.galen.tests.api;

import com.google.common.io.Files;
import net.mindengine.galen.api.Galen;
import net.mindengine.galen.components.mocks.driver.MockedDriver;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.validation.ErrorArea;
import net.mindengine.galen.validation.ValidationError;
import org.apache.commons.io.FileUtils;
import org.junit.Assert;
import org.openqa.selenium.WebDriver;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.Properties;

import static java.util.Arrays.asList;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class GalenTest {

    @Test
    public void checkLayout_shouldTestLayout_andReturnLayoutReport() throws IOException {
        WebDriver driver = new MockedDriver();
        driver.get("/mocks/pages/galen4j-sample-page.json");

        LayoutReport layoutReport = Galen.checkLayout(driver, "/specs/galen4j/sample-spec-with-error.spec", asList("mobile"), null, new Properties(), null);

        assertThat(layoutReport.getValidationErrors(), contains(
                new ValidationError().withMessage("\"save-button\" is 10px left instead of 50px")
                        .withArea(new ErrorArea(new Rect(10, 10, 100, 50), "save-button"))
                        .withArea(new ErrorArea(new Rect(120, 10, 200, 50), "name-textfield")),
                new ValidationError().withMessage("\"save-button\" text is \"Save\" but should be \"Store\"")
                        .withArea(new ErrorArea(new Rect(10, 10, 100, 50), "save-button"))));
    }


    @Test
    public void dumpPage_shouldGenereate_htmlJsonReport_andStorePicturesOfElements() throws IOException {
        String pageDumpPath = Files.createTempDir().getAbsolutePath() + "/pagedump";

        WebDriver driver = new MockedDriver();
        driver.get("/mocks/pages/galen4j-pagedump.json");
        Galen.dumpPage(driver, "test page", "/specs/galen4j/pagedump.spec", pageDumpPath);

        assertFileExists(pageDumpPath + "/page.json");
        assertFileContent(pageDumpPath + "/page.json", "/pagedump/expected.json");
        assertFileExists(pageDumpPath + "/page.html");

        assertFileExists(pageDumpPath + "/page.png");
        assertFileExists(pageDumpPath + "/objects/button-save.png");
        assertFileExists(pageDumpPath + "/objects/name-textfield.png");
        assertFileExists(pageDumpPath + "/objects/menu-item-1.png");
        assertFileExists(pageDumpPath + "/objects/menu-item-2.png");
        assertFileExists(pageDumpPath + "/objects/menu-item-3.png");
        assertFileExists(pageDumpPath + "/objects/big-container.png");

        assertFileExists(pageDumpPath + "/jquery-1.10.2.min.js");
        assertFileExists(pageDumpPath + "/galen-pagedump.js");
        assertFileExists(pageDumpPath + "/galen-pagedump.css");
    }


    @Test
    public void dumpPage_shouldOnlyStoreScreenshots_thatAreLessThan_theMaxAllowed() throws IOException {
        String pageDumpPath = Files.createTempDir().getAbsolutePath() + "/pagedump";

        WebDriver driver = new MockedDriver();
        driver.get("/mocks/pages/galen4j-pagedump.json");
        Galen.dumpPage(driver, "test page", "/specs/galen4j/pagedump.spec", pageDumpPath, 80, 80);

        assertFileExists(pageDumpPath + "/objects/button-save.png");
        assertFileDoesNotExist(pageDumpPath + "/objects/name-textfield.png");
        assertFileExists(pageDumpPath + "/objects/menu-item-1.png");
        assertFileExists(pageDumpPath + "/objects/menu-item-2.png");
        assertFileExists(pageDumpPath + "/objects/menu-item-3.png");
        assertFileDoesNotExist(pageDumpPath + "/objects/big-container.png");
    }


    private void assertFileContent(String pathForRealContent, String pathForExpectedContent) throws IOException {
        Assert.assertEquals(String.format("Content of \"%s\" should be the same as in \"%s\"", pathForRealContent, pathForExpectedContent),
                readFileToString(new File(pathForRealContent)).replaceAll("\\s+", ""),
                readFileToString(new File(getClass().getResource(pathForExpectedContent).getFile())).replaceAll("\\s+", ""));
    }

    private void assertFileDoesNotExist(String path) {
        assertThat("File " + path + " + should not exist", new File(path).exists(), is(false));
    }

    private void assertFileExists(String path) {
        assertThat("File " + path + " should exist", new File(path).exists(), is(true));
    }

}
