package com.galenframework.tests.speclang2.pagespec;


import com.galenframework.components.validation.MockedPage;
import com.galenframework.page.PageElement;
import com.galenframework.specs.page.PageSection;
import com.galenframework.specs.page.PageSpec;
import org.testng.annotations.Test;

import java.io.IOException;
import java.util.HashMap;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

public class PageSpecReaderRulesTest extends PageSpecReaderTestBase {

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
    public void shouldRead_customRules_andNotCare_aboutExtraWhiteSpace() throws IOException {
        PageSpec pageSpec = readPageSpec("speclang2/custom-rules-white-space.gspec",
            new MockedPage(new HashMap<String, PageElement>()),
            EMPTY_TAGS, EMPTY_TAGS);

        assertThat(pageSpec.getSections().size(), is(1));

        PageSection section = pageSpec.getSections().get(0);
        assertThat(section.getSections().size(), is(1));

        PageSection ruleSection = section.getSections().get(0);
        assertThat(ruleSection.getName(), is("login_panel     should    stretch    to     screen"));

        assertThat(ruleSection.getObjects().size(), is(1));
        assertThat(ruleSection.getObjects().get(0).getObjectName(), is("login_panel"));
        assertThat(ruleSection.getObjects().get(0).getSpecs().size(), is(1));
        assertThat(ruleSection.getObjects().get(0).getSpecs().get(0).getOriginalText(), is("inside screen 0px left right"));
    }
}
