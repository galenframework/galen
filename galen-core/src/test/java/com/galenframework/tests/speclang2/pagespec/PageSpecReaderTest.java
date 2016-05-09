/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.speclang2.pagespec;

import com.galenframework.components.validation.MockedInvisiblePageElement;
import com.galenframework.components.validation.MockedPageElement;
import com.galenframework.page.selenium.SeleniumPage;
import com.galenframework.speclang2.pagespec.PageSpecReader;
import com.galenframework.specs.page.CorrectionsRect;
import com.galenframework.specs.page.PageSection;
import com.galenframework.browser.SeleniumBrowser;
import com.galenframework.components.mocks.driver.MockedDriver;
import com.galenframework.components.validation.MockedPage;
import com.galenframework.page.Page;
import com.galenframework.page.PageElement;
import com.galenframework.parser.FileSyntaxException;
import com.galenframework.specs.page.Locator;
import com.galenframework.specs.page.ObjectSpecs;
import com.galenframework.specs.page.PageSpec;
import com.galenframework.speclang2.pagespec.SectionFilter;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

public class PageSpecReaderTest {

    private static final Page EMPTY_PAGE = new MockedPage();
    private static final List<String> EMPTY_TAGS = Collections.emptyList();
    private static final Properties NO_PROPERTIES = null;
    private static final Map<String, Object> NO_VARS = null;
    private static final Map<String, Locator> EMPTY_OBJECTS = null;

    @Test
    public void shouldRead_objectDefinitions() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-definitions.gspec");


        assertThat(pageSpec.getObjects(), is((Map<String, Locator>)new HashMap<String, Locator>(){{
            put("header", new Locator("css", "#header"));
            put("header-icon", new Locator("css", "#header img"));
            put("button", new Locator("xpath", "//div[@id='button']"));
            put("cancel-link", new Locator("id", "cancel"));
            put("caption", new Locator("css", "#wrapper")
                    .withCorrections(new CorrectionsRect(
                            new CorrectionsRect.Correction(0, CorrectionsRect.Type.PLUS),
                            new CorrectionsRect.Correction(100, CorrectionsRect.Type.PLUS),
                            new CorrectionsRect.Correction(5, CorrectionsRect.Type.MINUS),
                            new CorrectionsRect.Correction(7, CorrectionsRect.Type.PLUS)
                    )));
        }}));
    }


    @Test
    public void shouldRead_objectDefinitions_withMultiObjects() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-definitions-multi-objects.gspec",
                new SeleniumBrowser(new MockedDriver("/speclang2/mocks/menu-items.json")).getPage(), asList("mobile"), EMPTY_TAGS);

        assertThat(pageSpec.getObjects(), is((Map<String, Locator>)new HashMap<String, Locator>(){{
            put("menu-item-1", new Locator("css", "#menu li", 1));
            put("menu-item-2", new Locator("css", "#menu li", 2));
            put("menu-item-3", new Locator("css", "#menu li", 3));
            put("menu-item-4", new Locator("css", "#menu li", 4));
        }}));
    }

    @Test
    public void shouldRead_objectDefinitions_withMultiLevelObjects() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-definitions-multi-level-objects.gspec",
                new SeleniumBrowser(new MockedDriver("/speclang2/mocks/multi-level-objects.json")).getPage(), asList("mobile"), EMPTY_TAGS);

        assertThat(pageSpec.getObjects(), is((Map<String, Locator>)new HashMap<String, Locator>(){{
            put("header", new Locator("css", "#header"));
            put("header.icon", new Locator("css", "img")
                    .withParent(new Locator("css", "#header")));

            put("header.item-1", new Locator("css", "li", 1).withParent(new Locator("css", "#header")));
            put("header.item-2", new Locator("css", "li", 2).withParent(new Locator("css", "#header")));

            put("box-1", new Locator("css", ".box", 1));
            put("box-1.caption", new Locator("css", ".caption")
                    .withParent(new Locator("css", ".box", 1)));

            put("box-2", new Locator("css", ".box", 2));
            put("box-2.caption", new Locator("css", ".caption")
                    .withParent(new Locator("css", ".box", 2)));

            put("box-3", new Locator("css", ".box", 3));
            put("box-3.caption", new Locator("css", ".caption")
                    .withParent(new Locator("css", ".box", 3)));
        }}));
    }


    @Test
    public void shouldRead_sectionsWithObjectSpecs() throws  IOException {
        PageSpec pageSpec = readPageSpec("speclang2/sections-with-object-specs.gspec");

        assertThat(pageSpec.getSections().size(), is(2));

        PageSection section1 = pageSpec.getSections().get(0);
        assertThat(section1.getObjects().size(), is(1));
        assertThat(section1.getObjects().get(0).getObjectName(), is("header"));
        assertThat(section1.getObjects().get(0).getSpecs().size(), is(1));
        assertThat(section1.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("height 100px"));

        assertThat(section1.getSections().size(), is(1));
        PageSection subSection = section1.getSections().get(0);
        assertThat(subSection.getObjects().size(), is(2));
        assertThat(subSection.getObjects().get(0).getObjectName(), is("login-link"));
        assertThat(subSection.getObjects().get(0).getSpecs().size(), is(1));
        assertThat(subSection.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("height 30px"));
        assertThat(subSection.getObjects().get(1).getObjectName(), is("register-link"));
        assertThat(subSection.getObjects().get(1).getSpecs().size(), is(1));
        assertThat(subSection.getObjects().get(1).getSpecs().get(0).getOriginalText(), is("right-of login-link 10 to 30px"));

        PageSection section2 = pageSpec.getSections().get(1);
        assertThat(section2.getName(), is("Main section"));
        assertThat(section2.getObjects().size(), is(1));
        assertThat(section2.getObjects().get(0).getObjectName(), is("main-section"));
        assertThat(section2.getObjects().get(0).getSpecs().size(), is(2));
        assertThat(section2.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("below header 0 to 5px"));
        assertThat(section2.getObjects().get(0).getSpecs().get(1).getOriginalText(), is("inside screen 0px left right"));
    }

    @Test
    public void shouldProcess_complexObjectExpressions_insideSections() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/complex-object-expressions.gspec");

        PageSection section = pageSpec.getSections().get(0);

        assertThat(section.getName(), is("Main section"));
        assertThat(section.getObjects().size(), is(6));


        assertThat(section.getObjects().get(0).getObjectName(), is("header"));
        assertThat(section.getObjects().get(0).getSpecs().size(), is(1));
        assertThat(section.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("inside screen 0px left right"));

        assertThat(section.getObjects().get(1).getObjectName(), is("menu"));
        assertThat(section.getObjects().get(1).getSpecs().size(), is(1));
        assertThat(section.getObjects().get(1).getSpecs().get(0).getOriginalText(), is("inside screen 0px left right"));

        assertThat(section.getObjects().get(2).getObjectName(), is("main"));
        assertThat(section.getObjects().get(2).getSpecs().size(), is(1));
        assertThat(section.getObjects().get(2).getSpecs().get(0).getOriginalText(), is("inside screen 0px left right"));

        assertThat(section.getObjects().get(3).getObjectName(), is("menu-item-1"));
        assertThat(section.getObjects().get(3).getSpecs().size(), is(1));
        assertThat(section.getObjects().get(3).getSpecs().get(0).getOriginalText(), is("height 30px"));

        assertThat(section.getObjects().get(4).getObjectName(), is("menu-item-2"));
        assertThat(section.getObjects().get(4).getSpecs().size(), is(1));
        assertThat(section.getObjects().get(4).getSpecs().get(0).getOriginalText(), is("height 30px"));

        assertThat(section.getObjects().get(5).getObjectName(), is("menu-item-3"));
        assertThat(section.getObjects().get(5).getSpecs().size(), is(1));
        assertThat(section.getObjects().get(5).getSpecs().get(0).getOriginalText(), is("height 30px"));

    }

    @Test
    public void shouldProcess_hashInObjectPattern_asDigits() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/hash-in-object-name.gspec");

        PageSection section = pageSpec.getSections().get(0);
        assertThat(section.getObjects().size(), is(2));
        assertThat(section.getObjects().get(0).getObjectName(), is("item-1"));
        assertThat(section.getObjects().get(1).getObjectName(), is("item-342"));
    }



    /**
     * Purpose of this test is to check that "${}" expressions could be processed everywhere
     */
    @Test
    public void shouldRead_variablesDefinition_andProcessThem() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/variables-and-processing.gspec");


        assertThat(pageSpec.getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getName(), is("Section for user Johny"));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getObjectName(), is("welcome-message"));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecs().get(0).getOriginalText(), is("text is \"Welcome, Johny\""));

    }

    @Test
    public void shouldRead_simpleForLoop_andProcessIt() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/for-loop.gspec");

        assertThat(pageSpec.getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getName(), is("Main section"));
        assertThat(pageSpec.getSections().get(0).getObjects().size(), is(13));


        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();

        int objectIndex = 0;
        for (int i = 1; i <= 3; i++) {
            for (Integer j : asList(5, 7, 9)) {
                assertThat("Object #" + objectIndex + " name should be",
                        objects.get(objectIndex).getObjectName(),
                        is("box-" + i + "-" + j));

                assertThat("Object #" + objectIndex + " spec should be",
                        objects.get(objectIndex).getSpecs().get(0).getOriginalText(),
                        is("text is \"" + i + " and " + j + "\""));
                objectIndex++;
            }

            assertThat("Object #" + objectIndex + " name should be",
                    objects.get(objectIndex).getObjectName(),
                    is("label-" + i));

            assertThat("Object #" + objectIndex + " spec should be",
                    objects.get(objectIndex).getSpecs().get(0).getOriginalText(),
                    is("height 10px"));

            objectIndex++;
        }

        assertThat("Object #11 name should be",
                objects.get(objectIndex).getObjectName(),
                is("caption"));

        assertThat("Object #11 spec should be",
                objects.get(objectIndex).getSpecs().get(0).getOriginalText(),
                is("width 50px"));
    }


    @Test
    public void shouldRead_eachLoop_andProcessIt() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/foreach-loop.gspec");

        assertThat(pageSpec.getSections().size(), is(3));

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(4));
        for (int i = 0; i < 4; i++) {
            assertThat("Section 1. Object #" + i + " name should be", objects.get(i).getObjectName(),
                    is("menu-item-" + (i+1)));

            assertThat("Section 1. Object #" + i + " spec should be", objects.get(i).getSpecs().get(0).getOriginalText(),
                    is("width 100px"));
        }

        List<ObjectSpecs> objects2 = pageSpec.getSections().get(1).getObjects();
        assertThat(objects2.size(), is(3));
        for (int i = 0; i < 3; i++) {
            assertThat("Section 2. Object #" + i + " name should be", objects2.get(i).getObjectName(),
                    is("menu-item-" + (i+2)));

            assertThat("Section 2. Object #" + i + " spec should be", objects2.get(i).getSpecs().get(0).getOriginalText(),
                    is("right-of menu-item-" + (i+1) + " 10px"));
        }

        List<ObjectSpecs> objects3 = pageSpec.getSections().get(2).getObjects();
        assertThat(objects3.size(), is(3));
        for (int i = 0; i < 3; i++) {
            assertThat("Section 3. Object #" + i + " name should be", objects3.get(i).getObjectName(),
                    is("menu-item-" + (i+1)));

            assertThat("Section 3. Object #" + i + " spec should be", objects3.get(i).getSpecs().get(0).getOriginalText(),
                    is("left-of menu-item-" + (i + 2) + " 10px"));
        }
    }

    /**
     * Comes from https://github.com/galenframework/galen/issues/303
     */
    @Test
    public void shouldRead_emptyLoops_withoutException() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/empty-for-loops.gspec", EMPTY_PAGE, EMPTY_TAGS, EMPTY_TAGS);
        assertThat(pageSpec.getSections().size(), is(0));
    }

    @Test
    public void shouldLoopOver_existingAndNonExisting_objects() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/foreach-loop-with-nonexisting-objects.gspec");

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();

        assertThat(objects.size(), is(6));

        assertThat(objects.get(0).getObjectName(), is("menu-item-1"));
        assertThat(objects.get(1).getObjectName(), is("menu-item-2"));
        assertThat(objects.get(2).getObjectName(), is("menu-item-3"));
        assertThat(objects.get(3).getObjectName(), is("header"));
        assertThat(objects.get(4).getObjectName(), is("toolbar"));
        assertThat(objects.get(5).getObjectName(), is("popup"));
    }

    @Test
    public void shouldRead_taggedSections_andProcessOnlyThose_thatMatchGivenTags_1() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/tagged-sections.gspec", EMPTY_PAGE, asList("mobile"), EMPTY_TAGS);

        assertThat(pageSpec.getSections().size(), is(1));

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(2));
        assertThat(objects.get(0).getObjectName(), is("header"));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("height 100px"));
        assertThat(objects.get(1).getObjectName(), is("header-icon"));
        assertThat(objects.get(1).getSpecs().get(0).getOriginalText(), is("inside header 5px top left"));
    }

    @Test
    public void shouldRead_taggedSections_andProcessOnlyThose_thatMatchGivenTags_2() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/tagged-sections.gspec", EMPTY_PAGE, asList("tablet"), EMPTY_TAGS);

        assertThat(pageSpec.getSections().size(), is(1));

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(2));
        assertThat(objects.get(0).getObjectName(), is("header"));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("height 100px"));
        assertThat(objects.get(1).getObjectName(), is("header-icon"));
        assertThat(objects.get(1).getSpecs().get(0).getOriginalText(), is("inside header 5px top left"));
    }

    @Test
    public void shouldRead_taggedSections_andProcessOnlyThose_thatMatchGivenTags_3() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/tagged-sections.gspec", EMPTY_PAGE, asList("desktop"), EMPTY_TAGS);

        assertThat(pageSpec.getSections().size(), is(1));

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(2));
        assertThat(objects.get(0).getObjectName(), is("header"));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("height 200px"));
        assertThat(objects.get(1).getObjectName(), is("header-icon"));
        assertThat(objects.get(1).getSpecs().get(0).getOriginalText(), is("inside header 5px top left"));
    }

    @Test
    public void shouldRead_taggedSections_andExcludeTags() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/tagged-sections.gspec", EMPTY_PAGE, asList("mobile", "desktop"), asList("tablet"));

        assertThat(pageSpec.getSections().size(), is(1));

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(2));
        assertThat(objects.get(0).getObjectName(), is("header"));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("height 200px"));
        assertThat(objects.get(1).getObjectName(), is("header-icon"));
        assertThat(objects.get(1).getSpecs().get(0).getOriginalText(), is("inside header 5px top left"));
    }

    @Test
    public void should_importOtherPageSpecs_onlyOnce_andMergeSectionsAndObjects() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/import-other-pagespecs.gspec");

        assertThat(pageSpec.getObjects(), is((Map<String, Locator>) new HashMap<String, Locator>() {{
            put("header", new Locator("css", "#header"));
            put("main-container", new Locator("css", "#main"));
        }}));

        assertThat(pageSpec.getSections().size(), is(2));
        assertThat(pageSpec.getSections().get(0).getName(), is("Header section"));
        assertThat(pageSpec.getSections().get(0).getObjects().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getObjectName(), is("header"));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecs().size(), is(2));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecs().get(0).getOriginalText(), is("inside screen 0px top left right"));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecs().get(1).getOriginalText(), is("height 100px"));

        assertThat(pageSpec.getSections().get(1).getName(), is("Main section"));
        assertThat(pageSpec.getSections().get(1).getObjects().size(), is(1));
        assertThat(pageSpec.getSections().get(1).getObjects().get(0).getObjectName(), is("main-container"));
        assertThat(pageSpec.getSections().get(1).getObjects().get(0).getSpecs().size(), is(1));
        assertThat(pageSpec.getSections().get(1).getObjects().get(0).getSpecs().get(0).getOriginalText(), is("below header 0px"));
    }

    @Test
    public void shouldImportOtherSpecs_whenMainSpecIsLoaded_fromAbsolutePath() throws IOException {
        PageSpec pageSpec = readPageSpec(new File(getClass().getResource("/speclang2/import-other-pagespecs.gspec").getFile()).getAbsolutePath());

        assertThat(pageSpec.getObjects(), is((Map<String, Locator>) new HashMap<String, Locator>() {{
            put("header", new Locator("css", "#header"));
            put("main-container", new Locator("css", "#main"));
        }}));
    }

    @Test
    public void shouldExecute_customJavaScript_fromSeparateFile() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/script-importing.gspec");

        assertThat(pageSpec.getSections().size(), is(1));
        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(2));
        assertThat(objects.get(0).getObjectName(), is("caption"));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("text is \"Awesome website!\""));
        assertThat(objects.get(1).getObjectName(), is("caption-2"));
        assertThat(objects.get(1).getSpecs().get(0).getOriginalText(), is("text is \"Welcome, Johny\""));
    }

    @Test
    public void scriptsPath_shouldBeResolved_whenSpecIsLoaded_fromAbsolutePath() throws IOException {
        String absolutePath = new File(getClass().getResource("/speclang2/script-importing.gspec").getFile()).getAbsolutePath();
        PageSpec pageSpec = readPageSpec(absolutePath);

        assertThat(pageSpec.getSections().size(), is(1));
        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(2));
        assertThat(objects.get(0).getObjectName(), is("caption"));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("text is \"Awesome website!\""));
        assertThat(objects.get(1).getObjectName(), is("caption-2"));
        assertThat(objects.get(1).getSpecs().get(0).getOriginalText(), is("text is \"Welcome, Johny\""));
    }

    @Test
    public void shouldExecute_inPageJavaScript() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/script-inpage.gspec");

        assertThat(pageSpec.getSections().size(), is(1));
        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(2));
        assertThat(objects.get(0).getObjectName(), is("caption"));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("text is \"Awesome website!\""));
        assertThat(objects.get(1).getObjectName(), is("caption-2"));
        assertThat(objects.get(1).getSpecs().get(0).getOriginalText(), is("text is \"Welcome, Johny\""));

    }


    @Test
    public void shouldRead_customSpecRules_andProcessThem() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/custom-rules.gspec");

        assertThat(pageSpec.getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getName(), is("Main section"));
        assertThat(pageSpec.getSections().get(0).getSections().size(), is(1));

        PageSection subSection = pageSpec.getSections().get(0).getSections().get(0);
        assertThat(subSection.getName(), is("menu-item-* should be aligned horizontally"));
        assertThat(subSection.getObjects().size(), is(2));

        assertThat(subSection.getObjects().get(0).getObjectName(), is("menu-item-2"));
        assertThat(subSection.getObjects().get(0).getSpecs().size(), is(1));
        assertThat(subSection.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("aligned horizontally all menu-item-1"));

        assertThat(subSection.getObjects().get(1).getObjectName(), is("menu-item-3"));
        assertThat(subSection.getObjects().get(1).getSpecs().size(), is(1));
        assertThat(subSection.getObjects().get(1).getSpecs().get(0).getOriginalText(), is("aligned horizontally all menu-item-2"));


        assertThat(pageSpec.getSections().get(0).getObjects().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getObjectName(), is("menu-item-1"));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecs().size(), is(0));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().get(0).getSpecs().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().get(0).getName(), is("squared"));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().get(0).getSpecs().get(0).getOriginalText(),
                is("width 100 % of menu-item-1/height"));
    }

    @Test
    public void shouldRead_customSpecRulesInSections_withoutOtherObjects_andProcessThem() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/custom-rules-only-per-section.gspec");

        assertThat(pageSpec.getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getName(), is("Main section"));
        assertThat(pageSpec.getSections().get(0).getSections().size(), is(1));

        PageSection subSection = pageSpec.getSections().get(0).getSections().get(0);
        assertThat(subSection.getName(), is("menu-item-1 is squared"));
        assertThat(subSection.getObjects().size(), is(1));

        assertThat(subSection.getObjects().get(0).getObjectName(), is("menu-item-1"));
        assertThat(subSection.getObjects().get(0).getSpecs().size(), is(1));
        assertThat(subSection.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("width 100 % of menu-item-1/height"));
    }


    @Test
    public void shouldRead_customRulesFromJavaScript_andProcessThem() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/custom-js-rules.gspec",
                new MockedPage(new HashMap<String, PageElement>()),
                EMPTY_TAGS, EMPTY_TAGS);

        assertThat(pageSpec.getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getName(), is("Main section"));
        assertThat(pageSpec.getSections().get(0).getSections().size(), is(1));

        PageSection subSection = pageSpec.getSections().get(0).getSections().get(0);
        assertThat(subSection.getName(), is("menu-item-* should be aligned horizontally"));
        assertThat(subSection.getObjects().size(), is(2));

        assertThat(subSection.getObjects().get(0).getObjectName(), is("menu-item-2"));
        assertThat(subSection.getObjects().get(0).getSpecs().size(), is(1));
        assertThat(subSection.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("aligned horizontally all menu-item-1"));

        assertThat(subSection.getObjects().get(1).getObjectName(), is("menu-item-3"));
        assertThat(subSection.getObjects().get(1).getSpecs().size(), is(1));
        assertThat(subSection.getObjects().get(1).getSpecs().get(0).getOriginalText(), is("aligned horizontally all menu-item-2"));


        assertThat(pageSpec.getSections().get(0).getObjects().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getObjectName(), is("menu-item-1"));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecs().size(), is(0));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().get(0).getSpecs().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().get(0).getName(), is("squared"));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().get(0).getSpecs().get(0).getOriginalText(),
                is("width 100 % of menu-item-1/height"));
    }

    @Test
    public void shouldRead_conditionsWithMultipleElseBlocks()  throws  IOException {
        PageSpec pageSpec = readPageSpec("speclang2/conditions.gspec",
                new MockedPage(new HashMap<String, PageElement>() {{
                    put("header", element(0, 0, 100, 10));
                }}),
                EMPTY_TAGS, EMPTY_TAGS);

        assertThat(pageSpec.getSections().size(), is(1));

        PageSection section = pageSpec.getSections().get(0);
        assertThat(section.getObjects().size(), is(1));
        assertThat(section.getObjects().get(0).getObjectName(), is("header-icon"));
        assertThat(section.getObjects().get(0).getSpecs().size(), is(1));
        assertThat(section.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("inside header 10px top left"));
    }

    @Test
    public void shouldRead_conditionsWithMultipleElseBlocks_2()  throws  IOException {
        PageSpec pageSpec = readPageSpec("speclang2/conditions.gspec",
                new MockedPage(new HashMap<String, PageElement>(){{
                    put("header", invisibleElement(0, 0, 100, 10));
                    put("header2", element(0, 0, 100, 10));
                }}),
                EMPTY_TAGS, EMPTY_TAGS);

        assertThat(pageSpec.getSections().size(), is(1));

        PageSection section = pageSpec.getSections().get(0);
        assertThat(section.getObjects().size(), is(1));
        assertThat(section.getObjects().get(0).getObjectName(), is("header2-icon"));
        assertThat(section.getObjects().get(0).getSpecs().size(), is(1));
        assertThat(section.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("inside header2 5px top left"));
    }

    @Test
    public void shouldRead_conditionsWithMultipleElseBlocks_3()  throws  IOException {
        PageSpec pageSpec = readPageSpec("speclang2/conditions.gspec",
                new MockedPage(new HashMap<String, PageElement>(){{
                    put("header", invisibleElement(0, 0, 100, 10));
                    put("header2", invisibleElement(0, 0, 100, 10));
                }}),
                EMPTY_TAGS, EMPTY_TAGS);

        assertThat(pageSpec.getSections().size(), is(1));

        PageSection section = pageSpec.getSections().get(0);
        assertThat(section.getObjects().size(), is(1));
        assertThat(section.getObjects().get(0).getObjectName(), is("header3"));
        assertThat(section.getObjects().get(0).getSpecs().size(), is(1));
        assertThat(section.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("visible"));
    }

    @Test(expectedExceptions = FileSyntaxException.class,
            expectedExceptionsMessageRegExp = "JavaScript error inside statement\n    in speclang2/condition-with-js-error.gspec:5"
    )
    public void shouldFail_whenThereIsAnError_insideIfStatement() throws IOException {
        readPageSpec("speclang2/condition-with-js-error.gspec",
            new MockedPage(new HashMap<String, PageElement>()),
            EMPTY_TAGS, EMPTY_TAGS);

    }

    @Test
    public void shouldAllow_toPassProperties() throws IOException {
        Properties properties = new Properties();
        properties.put("custom.user.name", "John");
        PageSpec pageSpec = new PageSpecReader().read("speclang2/properties.gspec", EMPTY_PAGE, new SectionFilter(EMPTY_TAGS, EMPTY_TAGS), properties, NO_VARS, EMPTY_OBJECTS);

        assertThat(pageSpec.getSections().get(0).getName(), is("Main section for user John"));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecs().get(0).getOriginalText(),
            is("text is \"Welcome, John!\""));
    }


    @Test
    public void shouldThrow_fileSyntaxException_ifThereIsAnErrorInSpec() throws IOException {
        try {
            readPageSpec("speclang2/syntax-error.gspec");
            throw new RuntimeException("FileSyntaxException was not thrown from page spec reader");
        } catch (FileSyntaxException ex) {
            assertThat(ex.getLine(), is(9));
            assertThat(ex.getFilePath(), endsWith("speclang2/syntax-error.gspec"));
            assertThat(ex.getCause().getMessage(), is("Expecting \"px\", \"to\" or \"%\", got \"\""));
        }
    }

    @Test
    public void shouldAllow_mathExpressions_onIntegerValues_onForLoopIndexes() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/for-loop-math-expression.gspec");
        PageSection section = pageSpec.getSections().get(0);

        assertThat(section.getObjects().size(), is(4));
        assertThat(section.getObjects().get(0).getSpecs().get(0).getOriginalText(),
                is("right-of menu-item-3"));
        assertThat(section.getObjects().get(1).getSpecs().get(0).getOriginalText(),
                is("right-of menu-item-5"));
        assertThat(section.getObjects().get(2).getSpecs().get(0).getOriginalText(),
                is("right-of menu-item-7"));
        assertThat(section.getObjects().get(3).getSpecs().get(0).getOriginalText(),
                is("right-of menu-item-9"));
    }


    @Test
    public void shouldParse_forLoops_withIfStatements() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/for-with-if.gspec");
        PageSection section = pageSpec.getSections().get(0);

        assertThat(section.getObjects().size(), is(1));
        assertThat(section.getObjects().get(0).getSpecs().size(), is(2));
        assertThat(section.getObjects().get(0).getObjectName(),
                is("button"));
        assertThat(section.getObjects().get(0).getSpecs().get(0).getOriginalText(),
                is("inside container1 10px top left"));
        assertThat(section.getObjects().get(0).getSpecs().get(1).getOriginalText(),
                is("inside container2 10px top left"));
    }

    @Test
    public void shouldRead_specsWithWarningLevel() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/warning-level-in-specs.gspec");

        PageSection section = pageSpec.getSections().get(0);

        assertThat(section.getObjects().get(0).getSpecs().get(0).isOnlyWarn(), is(false));
        assertThat(section.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("width 100px"));
        assertThat(section.getObjects().get(0).getSpecs().get(1).isOnlyWarn(), is(true));
        assertThat(section.getObjects().get(0).getSpecs().get(1).getOriginalText(), is("height 100px"));
    }

    @Test
    public void shouldRead_specAliases_beforeActualSpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/spec-notes.gspec");
        PageSection section = pageSpec.getSections().get(0);

        assertThat(section.getObjects().size(), is(1));
        ObjectSpecs object = section.getObjects().get(0);

        assertThat(object.getSpecs().size(), is(3));
        assertThat(object.getSpecs().get(0).getAlias(), is(nullValue()));
        assertThat(object.getSpecs().get(0).getOriginalText(), is("height 100px"));
        assertThat(object.getSpecs().get(1).getAlias(), is("should be visible"));
        assertThat(object.getSpecs().get(1).getOriginalText(), is("visible"));
        assertThat(object.getSpecs().get(2).getAlias(), is("should be on top"));
        assertThat(object.getSpecs().get(2).getOriginalText(), is("inside screen 0px top"));
    }

    @Test
    public void shouldAllow_toPassCustomJsObjects() throws  IOException {
        PageSpec pageSpec = new PageSpecReader().read(
            "speclang2/custom-js-variables.gspec",
            EMPTY_PAGE,
            new SectionFilter(EMPTY_TAGS, EMPTY_TAGS),
            NO_PROPERTIES,
            new HashMap<String, Object>() {{
                put("age", 29);
                put("userName", "John");
            }}, EMPTY_OBJECTS);

        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecs().get(0).getOriginalText(),
            is("text is \"Name: John, age: 29\""));
    }

    @Test
    public void shouldClean_emptySetions() throws  IOException {
        PageSpec pageSpec = readPageSpec("speclang2/empty-sections.gspec");

        assertThat(pageSpec.getSections().size(), is(1));

        PageSection section = pageSpec.getSections().get(0);

        assertThat(section.getSections().size(), is(0));
        assertThat(section.getObjects().size(), is(1));
    }


    @Test
    public void countFunction_shouldCountAllMatchingObjects() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/count.gspec");

        assertThat(pageSpec.getSections().size(), is(2));
        assertThat(pageSpec.getSections().get(0).getObjects().size(), is(6));

        assertThat(pageSpec.getSections().get(1).getObjects().size(), is(3));
    }

    @Test
    public void forEachLoop_shouldOrderCorrectly_objectsWithNumbers() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/foreach-sortorder.gspec");

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();

        assertThat(objects.size(), is(4));
        assertThat(objects.get(0).getObjectName(), is("menu-item-1"));
        assertThat(objects.get(1).getObjectName(), is("menu-item-2"));
        assertThat(objects.get(2).getObjectName(), is("menu-item-12"));
        assertThat(objects.get(3).getObjectName(), is("menu-item-101"));
    }

    @Test
    public void forEachLoop_shouldAllow_toUseIndex() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/forEach-loop-with-index.gspec");

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(3));
        assertThat(objects.get(0).getObjectName(), is("item-1"));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("inside screen 100px top"));

        assertThat(objects.get(1).getObjectName(), is("item-2"));
        assertThat(objects.get(1).getSpecs().get(0).getOriginalText(), is("inside screen 200px top"));

        assertThat(objects.get(2).getObjectName(), is("item-3"));
        assertThat(objects.get(2).getSpecs().get(0).getOriginalText(), is("inside screen 300px top"));
    }

    @Test
    public void shouldAllow_toProvideObjects_toPageSpec() throws IOException {
        Map<String, Locator> objects = new HashMap<>();
        objects.put("header", new Locator("css", "#header"));
        objects.put("menu", new Locator("id", "menu"));

        PageSpec pageSpec = new PageSpecReader().read("speclang2/provide-objects.gspec", EMPTY_PAGE, new SectionFilter(EMPTY_TAGS, EMPTY_TAGS), NO_PROPERTIES, NO_VARS, objects);

        assertThat(pageSpec.getObjects(), allOf(
                hasEntry("header", new Locator("css", "#header")),
                hasEntry("menu", new Locator("id", "menu")),
                hasEntry("button", new Locator("css", "#button"))
        ));

    }


    @Test
    public void customRules_shouldAllow_toUse_ruleBodies_inSpecs() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/custom-rules-using-rule-body.gspec",
                new MockedPage(new HashMap<String, PageElement>(){{
                    put("banner-1", element(0, 0, 100, 10));
                    put("banner-2", invisibleElement(0, 0, 100, 10));
                }}),
                EMPTY_TAGS, EMPTY_TAGS);

        assertThat(pageSpec.getSections().get(0).getObjects().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().get(0).getSpecs().get(0).getOriginalText(), is("height 90px"));


        assertThat(pageSpec.getSections().get(0).getSections().size(), is(2));
        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getSections().get(0).getObjects();

        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).getObjectName(), is("banner-1"));
        assertThat(objects.get(0).getSpecs().size(), is(1));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("width 145px"));


        objects = pageSpec.getSections().get(0).getSections().get(1).getObjects();

        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).getObjectName(), is("banner-3"));
        assertThat(objects.get(0).getSpecs().size(), is(1));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("visible"));
    }

    @Test
    public void shouldRead_objectClasses_inObjectsSection() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-groups-definition.gspec");

        assertThat(pageSpec.getObjectGroups(), hasEntry("image-validation", asList("menu.item-1", "button")));
        assertThat(pageSpec.getObjectGroups(), hasEntry("menu-stuff", asList("menu.item-1")));
    }


    @Test
    public void shouldApply_objectGroups_toAlreadyProcessedObjects() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-groups-added.gspec");

        assertThat(pageSpec.getObjectGroups(), hasEntry("groupA", asList("obj1", "obj3")));
        assertThat(pageSpec.getObjectGroups(), hasEntry("groupB", asList("obj1", "obj3")));
        assertThat(pageSpec.getObjectGroups(), hasEntry("groupC", asList("obj1", "obj2", "obj3")));
    }

    @Test
    public void countFunction_shouldWorkWithGroups() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/count-grouped-objects.gspec");
        assertThat(firstAppearingSpecIn(pageSpec), is("text is \"count is 4\""));
    }

    @Test
    public void findFunction_shouldWorkWithGroups() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/find-grouped-object.gspec");
        assertThat(firstAppearingObjectIn(pageSpec).getObjectName(), is("menu_item-1"));
    }

    @Test
    public void firstFunction_shouldWorkWithGroups() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/first-grouped-object.gspec");
        assertThat(firstAppearingObjectIn(pageSpec).getObjectName(), is("menu_item-1"));
    }

    @Test
    public void lastFunction_shouldWorkWithGroups() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/last-grouped-object.gspec");
        assertThat(firstAppearingObjectIn(pageSpec).getObjectName(), is("menu_icon"));
    }

    @Test
    public void shouldFind_allObjects_forSpecifiedGroups_inRegularObjectStatement() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-groups-search.gspec");
        assertSearchAndForEachGroupsElements(pageSpec);
    }

    @Test
    public void shouldFind_allObjects_forSpecifiedGroups_forEachLoop() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-groups-foreach.gspec");
        assertSearchAndForEachGroupsElements(pageSpec);
    }

    private void assertSearchAndForEachGroupsElements(PageSpec pageSpec) {
        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(2));

        assertThat(objects.get(0).getObjectName(), is("cancel-button"));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("height 30px"));

        assertThat(objects.get(1).getObjectName(), is("login-button"));
        assertThat(objects.get(1).getSpecs().get(0).getOriginalText(), is("height 30px"));
    }

    @Test
    public void shouldFind_allObjects_forSpecifiedGroups_inJavaScriptFindAllFunction() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/object-groups-findAll.gspec",
                new MockedPage(new HashMap<String, PageElement>()),
                EMPTY_TAGS, EMPTY_TAGS);

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(2));

        assertThat(objects.get(0).getObjectName(), is("cancel-button"));
        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is("height 30px"));

        assertThat(objects.get(1).getObjectName(), is("login-button"));
        assertThat(objects.get(1).getSpecs().get(0).getOriginalText(), is("height 30px"));
    }

    /**
     * When nonexisting object is passed to findAll function - it should return an instance of JsPageElement that
     * will give 0 for any location related values like width, height, right etc. Also it should return false for
     * isVisible function.
     * @throws IOException
     */
    @Test
    public void findAll_andFind_functions_shouldHave_strictObjectProcessing_likeForEachLoop() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/findAll-nonexisting-objects.gspec");

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(1));

        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is(
                "text is \"header[0, 0, 0, 0, false], menu[0, 0, 0, 0, false]\""
        ));
        assertThat(objects.get(0).getSpecs().get(1).getOriginalText(), is(
                "text is \"header[0, 0, 0, 0, false]\""
        ));
    }

    @Test
    public void screenAndViewportObjects_shouldBeAccessible_fromJavaScript_codeBlocks() throws IOException {
        MockedDriver driver = new MockedDriver();
        driver.setExpectedJavaScriptReturnValues(asList(
                (Object)asList(1000L, 700L),
                (Object)asList(900L, 700L)
        ));
        PageSpec pageSpec = readPageSpec("speclang2/screen-and-viewport-from-js.gspec", new SeleniumPage(driver));


        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();

        assertThat(objects.size(), is(1));

        assertThat(objects.get(0).getSpecs().get(0).getOriginalText(), is(
                "width 120 px"
        ));
    }

    @Test
    public void shouldAllowToInvoke_first_function() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/first-function.gspec");

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).getObjectName(), is("menu_item-1"));
    }

    @Test
    public void shouldAllowToInvoke_last_function() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/last-function.gspec");

        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(1));
        assertThat(objects.get(0).getObjectName(), is("menu_item-3"));
    }

    @Test
    public void ruleShouldAllows_toUseColon_atTheEnd() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/rule-colon-at-end.gspec");

        assertThat(pageSpec.getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().size(), is(0));
        assertThat(pageSpec.getSections().get(0).getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getSections().get(0).getObjects().size(), is(1));

        ObjectSpecs object = pageSpec.getSections().get(0).getSections().get(0).getObjects().get(0);
        assertThat(object.getObjectName(), is("menu_item-1"));
        assertThat(object.getSpecs().size(), is(1));
        assertThat(object.getSpecs().get(0).getOriginalText(), is("width 100px"));
    }

    @Test
    public void ruleShouldHave_priorityOverRules_thatWereDeclaredFirst() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/rule-priority.gspec");

        assertThat(pageSpec.getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getObjectName(), is("cancel_button"));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().get(0).getSpecs().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getObjects().get(0).getSpecGroups().get(0).getSpecs().get(0).getOriginalText(), is("width 30px"));


        assertThat(pageSpec.getSections().get(0).getSections().size(), is(1));
        assertThat(pageSpec.getSections().get(0).getSections().get(0).getObjects().size(), is(1));

        ObjectSpecs object = pageSpec.getSections().get(0).getSections().get(0).getObjects().get(0);
        assertThat(object.getObjectName(), is("login_button"));
        assertThat(object.getSpecs().size(), is(1));
        assertThat(object.getSpecs().get(0).getOriginalText(), is("width 300px"));
    }

    @Test(expectedExceptions = FileSyntaxException.class,
            expectedExceptionsMessageRegExp = "\\QError processing rule: button is located at the left side inside main_container with 10px margin\\E\\s+\\Qin speclang2/rule-error.gspec:7\\E")
    public void shouldThrownInformativeError_whenThereIsProblemParsingTheRule() throws  IOException {
            readPageSpec("speclang2/rule-error.gspec");
    }

    @Test(expectedExceptions = FileSyntaxException.class,
            expectedExceptionsMessageRegExp = "\\QError processing rule: is located at the left side inside main_container with 10px margin\\E\\s+\\Qin speclang2/rule-error-object-level.gspec:7\\E")
    public void shouldThrownInformativeError_whenThereIsProblemParsingTheRule_inObjectLevel() throws  IOException {
        readPageSpec("speclang2/rule-error-object-level.gspec");
    }

    @Test(expectedExceptions = FileSyntaxException.class,
            expectedExceptionsMessageRegExp = "\\QSpecs cannot have inner blocks\\E\\s+\\Qin speclang2/incorrect/nested-spec.gspec:7\\E")
    public void shouldGiveError_whenSpecIsNested_belowAnotherSpec() throws IOException {
        readPageSpec("speclang2/incorrect/nested-spec.gspec");
    }

    @Test(expectedExceptions = FileSyntaxException.class,
        expectedExceptionsMessageRegExp = "\\QNot enough menu items\\E\\s+\\Qin speclang2/die.gspec:6\\E")
    public void shouldGiveError_whenUsingDieStatement() throws IOException {
        readPageSpec("speclang2/die.gspec");
    }

    private PageSpec readPageSpec(String resource) throws IOException {
        return readPageSpec(resource, EMPTY_PAGE, EMPTY_TAGS, EMPTY_TAGS);
    }

    private PageSpec readPageSpec(String resource, Page page) throws IOException {
        return readPageSpec(resource, page, EMPTY_TAGS, EMPTY_TAGS);
    }

    private PageSpec readPageSpec(String resource, Page page, List<String> tags, List<String> excludedTags) throws IOException {
        return new PageSpecReader().read(resource, page, new SectionFilter(tags, excludedTags), NO_PROPERTIES, NO_VARS, EMPTY_OBJECTS);
    }

    private MockedPageElement element(int left, int top, int width, int height) {
        return new MockedPageElement(left, top, width, height);
    }

    protected PageElement invisibleElement(int left, int top, int width, int height) {
        return new MockedInvisiblePageElement(left, top, width, height);
    }

    private String firstAppearingSpecIn(PageSpec pageSpec) {
        return pageSpec.getSections().get(0).getObjects().get(0).getSpecs().get(0).getOriginalText();
    }

    private ObjectSpecs firstAppearingObjectIn(PageSpec pageSpec) {
        return pageSpec.getSections().get(0).getObjects().get(0);
    }

}
