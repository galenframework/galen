/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.tests.speclang2;


import static com.galenframework.components.TestUtils.deleteSystemProperty;
import static com.galenframework.specs.Side.BOTTOM;
import static com.galenframework.specs.Side.LEFT;
import static com.galenframework.specs.Side.RIGHT;
import static com.galenframework.specs.Side.TOP;
import static java.util.Arrays.asList;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;

import java.awt.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.galenframework.rainbow4j.colorscheme.GradientColorClassifier;
import com.galenframework.rainbow4j.colorscheme.SimpleColorClassifier;
import com.galenframework.rainbow4j.filters.*;
import com.galenframework.specs.*;
import com.galenframework.specs.colors.ColorRange;
import junit.framework.Assert;
import com.galenframework.config.GalenConfig;
import com.galenframework.page.Rect;
import com.galenframework.parser.SyntaxException;
import com.galenframework.speclang2.specs.SpecReader;

import org.hamcrest.Matchers;
import org.junit.BeforeClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

public class SpecsReaderV2Test {

    @BeforeClass
    public void init() throws IOException {
        deleteSystemProperty("galen.range.approximation");
        deleteSystemProperty("galen.reporting.listeners");
        GalenConfig.getConfig().reset();
    }

    @BeforeMethod
    public void configureApproximation() {
        System.setProperty("galen.range.approximation", "2");
    }

    @AfterMethod
    public void clearApproximation() {
        System.getProperties().remove("galen.range.approximation");
    }

    @Test
    public void shouldReadSpec_inside() {
        Spec spec = readSpec("inside object");
        SpecInside specInside = (SpecInside) spec;

        assertThat(specInside.getObject(), is("object"));
        assertThat(specInside.getPartly(), is(false));

        List<Location> locations = specInside.getLocations();
        assertThat(locations.size(), is(0));
    }

    @Test
    public void shouldReadSpec_inside_object_10px_right() {
        Spec spec = readSpec("inside object 10px right");
        SpecInside specInside = (SpecInside) spec;

        assertThat(specInside.getObject(), is("object"));
        assertThat(specInside.getPartly(), is(false));

        List<Location> locations = specInside.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(specInside.getLocations(), contains(new Location(Range.exact(10), sides(RIGHT))));
        assertThat(spec.getOriginalText(), is("inside object 10px right"));
    }

    @Test
    public void shouldReadSpec_inside_partly_object_10px_right() {
        Spec spec = readSpec("inside partly object 10px right");
        SpecInside specInside = (SpecInside) spec;

        assertThat(specInside.getObject(), is("object"));
        assertThat(specInside.getPartly(), is(true));

        List<Location> locations = specInside.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(specInside.getLocations(), contains(new Location(Range.exact(10), sides(RIGHT))));
        assertThat(spec.getOriginalText(), is("inside partly object 10px right"));
    }


    @Test
    public void shouldReadSpec_inside_object_10_to_30px_left() {
        Spec spec = readSpec("inside object 10 to 30px left");
        SpecInside specInside = (SpecInside) spec;

        assertThat(specInside.getObject(), is("object"));

        List<Location> locations = specInside.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(specInside.getLocations(), contains(new Location(Range.between(10, 30), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("inside object 10 to 30px left"));
    }

    @Test
    public void shouldReadSpec_inside_object_25px_top_left() {
        SpecInside spec = (SpecInside)readSpec("inside object 25px top left");

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25), sides(TOP, LEFT))));
        assertThat(spec.getOriginalText(), is("inside object 25px top left"));
    }

    @Test
    public void shouldReadSpec_inside_object_25px_top_left_comma_10_to_20px_bottom() {
        SpecInside spec = (SpecInside)readSpec("inside object 25px top left, 10 to 20px bottom");

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(2));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(TOP, LEFT)),
                new Location(Range.between(10, 20), sides(BOTTOM))));
        assertThat(spec.getOriginalText(), is("inside object 25px top left, 10 to 20px bottom"));
    }

    @Test
    public void shouldReadSpec_inside_object_25px_bottom_right()   {
        SpecInside spec = (SpecInside)readSpec("inside object 25px bottom right");

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(BOTTOM, RIGHT))));
        assertThat(spec.getOriginalText(), is("inside object 25px bottom right"));
    }

    @Test
    public void shouldReadSpec_inside_object_25px_top_left_right_bottom()   {
        SpecInside spec = (SpecInside)readSpec("inside object 25px top left right bottom ");

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25), sides(TOP, LEFT, RIGHT, BOTTOM))));
        assertThat(spec.getOriginalText(), is("inside object 25px top left right bottom"));
    }

    @Test public void shouldReadSpec_inside_object_20px_left_and_approximate_30px_top()   {
        SpecInside spec = (SpecInside)readSpec("inside object 20px left, ~30px top");

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(2));

        Assert.assertEquals(new Location(Range.exact(20), sides(LEFT)), spec.getLocations().get(0));
        Assert.assertEquals(new Location(Range.between(28, 32), sides(TOP)), spec.getLocations().get(1));

        assertThat(spec.getOriginalText(), is("inside object 20px left, ~30px top"));
    }

    @Test(expectedExceptions = SyntaxException.class,
            expectedExceptionsMessageRegExp = "Missing object name")
    public void shouldGiveError_inside_withoutObjects()  {
        readSpec("inside");
    }

    @Test(expectedExceptions = SyntaxException.class,
            expectedExceptionsMessageRegExp = "Missing object name")
    public void shouldGiveError_inside_partly_withoutObjects()  {
        readSpec("inside partly");
    }

    @Test
    public void shouldReadSpec_contains()   {
        Spec spec = readSpec("contains object, menu, button");
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("contains object, menu, button"));
    }

    @Test
    public void shouldReadSpec_contains_with_regex()   {
        Spec spec = readSpec("contains menu-item-*");
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.getChildObjects(), contains("menu-item-*"));
        assertThat(spec.getOriginalText(), is("contains menu-item-*"));
    }

    @Test
    public void shouldReadSpec_contains_partly()   {
        Spec spec = readSpec("contains partly object, menu, button");
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.isPartly(), is(true));
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("contains partly object, menu, button"));
    }

    @Test(expectedExceptions = SyntaxException.class,
            expectedExceptionsMessageRegExp = "Missing object name")
    public void shouldGiveError_contains_withoutObjects()  {
        readSpec("contains");
    }

    @Test(expectedExceptions = SyntaxException.class,
            expectedExceptionsMessageRegExp = "Missing object name")
    public void shouldGiveError_contains_partly_withoutObjects()  {
        readSpec("contains partly");
    }

    @Test
    public void shouldReadSpec_near_button_10_to_20px_left()   {
        SpecNear spec = (SpecNear) readSpec("near button 10 to 20px left");

        assertThat(spec.getObject(), is("button"));

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(10, 20), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("near button 10 to 20px left"));
    }

    @Test
    public void shouldReadSpec_near_button_10_to_20px_top_right()   {
        SpecNear spec = (SpecNear) readSpec("near button 10 to 20px top right");
        assertThat(spec.getObject(), is("button"));

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(10, 20), sides(TOP, RIGHT))));
        assertThat(spec.getOriginalText(), is("near button 10 to 20px top right"));
    }

    @Test
    public void shouldReadSpec_near_button_approx_0px_left() {
        SpecNear spec = (SpecNear) readSpec("near button ~0px left");
        assertThat(spec.getObject(), is("button"));

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(-2, 2), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("near button ~0px left"));
    }

    @Test(expectedExceptions = SyntaxException.class,
            expectedExceptionsMessageRegExp = "Missing object name")
    public void shouldGiveError_near()  {
        readSpec("near");
    }

    @Test(expectedExceptions = SyntaxException.class,
        expectedExceptionsMessageRegExp = "Missing location")
    public void shouldGiveError_near_button()  {
        readSpec("near button");
    }

    @Test
    public void shouldReadSpec_aligned_horizontally_centered()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("aligned horizontally centered object");
        assertThat(spec.getAlignment(), Matchers.is(Alignment.CENTERED));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(0));
        assertThat(spec.getOriginalText(), is("aligned horizontally centered object"));
    }

    @Test
    public void shouldReadSpec_aligned_horizontally_top()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("aligned horizontally top object");
        assertThat(spec.getAlignment(), is(Alignment.TOP));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(0));
        assertThat(spec.getOriginalText(), is("aligned horizontally top object"));
    }

    @Test
    public void shouldReadSpec_aligned_horizontally_bottom()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("aligned horizontally bottom object");
        assertThat(spec.getAlignment(), is(Alignment.BOTTOM));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(0));
        assertThat(spec.getOriginalText(), is("aligned horizontally bottom object"));
    }


    @Test
    public void shouldReadSpec_aligned_horizontally_all()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("aligned horizontally all object");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(0));
        assertThat(spec.getOriginalText(), is("aligned horizontally all object"));
    }

    @Test
    public void shouldReadSpec_aligned_vertically_centered()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("aligned  vertically  centered object");
        assertThat(spec.getAlignment(), is(Alignment.CENTERED));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(0));
        assertThat(spec.getOriginalText(), is("aligned  vertically  centered object"));
    }

    @Test
    public void shouldReadSpec_aligned_vertically_left()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("aligned vertically left object");
        assertThat(spec.getAlignment(), is(Alignment.LEFT));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(0));
        assertThat(spec.getOriginalText(), is("aligned vertically left object"));
    }

    @Test
    public void shouldReadSpec_aligned_vertically_right()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("aligned vertically right object");
        assertThat(spec.getAlignment(), is(Alignment.RIGHT));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(0));
        assertThat(spec.getOriginalText(), is("aligned vertically right object"));
    }

    @Test
    public void shouldReadSpec_aligned_vertically_all()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("aligned vertically all object");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(0));
        assertThat(spec.getOriginalText(), is("aligned vertically all object"));
    }

    @Test
    public void shouldReadSpec_aligned_vertically_with_error_rate_10px()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("aligned vertically all object 10px");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(10));
        assertThat(spec.getOriginalText(), is("aligned vertically all object 10px"));
    }

    @Test
    public void shouldReadSpec_aligned_vertically_with_error_rate_10_px()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("aligned vertically all object 10  px");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(10));
        assertThat(spec.getOriginalText(), is("aligned vertically all object 10  px"));
    }

    @Test
    public void shouldReadSpec_aligned_horizontally_with_error_rate_10px()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("aligned horizontally all object 10px");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(10));
        assertThat(spec.getOriginalText(), is("aligned horizontally all object 10px"));
    }

    @Test
    public void shouldReadSpec_aligned_horizontally_with_error_rate_10_px()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("aligned horizontally all object 10 px");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getErrorRate(), is(10));
        assertThat(spec.getOriginalText(), is("aligned horizontally all object 10 px"));
    }

    @Test(expectedExceptions = SyntaxException.class,
        expectedExceptionsMessageRegExp = "Incorrect alignment direction. Expected 'vertically' or 'horizontally' but got: object")
    public void shouldGiveError_aligned_object() {
        readSpec("aligned object");
    }

    @Test(expectedExceptions = SyntaxException.class,
            expectedExceptionsMessageRegExp = "Incorrect side for vertical alignment: top")
    public void shouldGiveError_aligned_vertically_top_object() {
        readSpec("aligned vertically top object");
    }

    @Test(expectedExceptions = SyntaxException.class,
            expectedExceptionsMessageRegExp = "Incorrect side for horizontal alignment: left")
    public void shouldGiveError_aligned_horizontally_left_object() {
        readSpec("aligned horizontally left object");
    }

    @Test(expectedExceptions = SyntaxException.class,
            expectedExceptionsMessageRegExp = "Missing object name")
    public void shouldGiveError_aligned_horizontally_left() {
        readSpec("aligned horizontally left");
    }

    @Test
    public void shouldReadSpec_absent()  throws IOException {
        Spec spec = readSpec("absent");
        assertThat(spec, instanceOf(SpecAbsent.class));
        assertThat(spec.getOriginalText(), is("absent"));
    }

    @Test
    public void shouldReadSpec_visible()  throws IOException {
        Spec spec = readSpec("visible");
        assertThat(spec, instanceOf(SpecVisible.class));
        assertThat(spec.getOriginalText(), is("visible"));
    }

    @Test
    public void shouldReadSpec_width_10px()  throws IOException {
        SpecWidth spec = (SpecWidth) readSpec("width 10px");
        assertThat(spec.getRange(), is(Range.exact(10)));
        assertThat(spec.getOriginalText(), is("width 10px"));
    }

    @Test
    public void shouldReadSpec_width_5_to_8px()  throws IOException {
        SpecWidth spec = (SpecWidth) readSpec("width 5 to 8px");
        assertThat(spec.getRange(), is(Range.between(5, 8)));
        assertThat(spec.getOriginalText(), is("width 5 to 8px"));
    }

    @Test
    public void shouldReadSpec_width_100_percent_of_other_object_width()  throws IOException {
        SpecWidth spec = (SpecWidth) readSpec("width 100% of main-big-container/width");
        assertThat(spec.getRange(), is(Range.exact(100).withPercentOf("main-big-container/width")));
        assertThat(spec.getOriginalText(), is("width 100% of main-big-container/width"));
    }

    @Test
    public void shouldReadSpec_height_10px()  throws IOException {
        SpecHeight spec = (SpecHeight) readSpec("height 10px");
        assertThat(spec.getRange(), is(Range.exact(10)));
        assertThat(spec.getOriginalText(), is("height 10px"));
    }

    @Test
    public void shouldReadSpec_height_5_to_8px()  throws IOException {
        SpecHeight spec = (SpecHeight) readSpec("height 5 to 8px");
        assertThat(spec.getRange(), is(Range.between(5, 8)));
        assertThat(spec.getOriginalText(), is("height 5 to 8px"));
    }

    @Test(expectedExceptions = SyntaxException.class,
            expectedExceptionsMessageRegExp = "Unexpected token: 1234 px")
    public void shouldGiveError_width_unexcpected_token() {
        readSpec("width 10 to 40 px 1234 px ");
    }

    @Test
    public void shouldReadSpec_text_is_some_text()  throws IOException {
        SpecText spec = (SpecText)readSpec("text is  \"Some text\"");
        assertThat(spec.getText(), is("Some text"));
        assertThat(spec.getType(), is(SpecText.Type.IS));
    }

    @Test
    public void shouldReadSpec_text_is_some_text_2()  throws IOException {
        SpecText spec = (SpecText)readSpec("text is \"Some text\\\" with \\t special \\n symbols\"");
        assertThat(spec.getText(), is("Some text\" with \t special \n symbols"));
        assertThat(spec.getType(), is(SpecText.Type.IS));
    }

    @Test
    public void shouldReadSpec_text_is_empty()  throws IOException {
        SpecText spec = (SpecText)readSpec("text is \"\"");
        assertThat(spec.getText(), is(""));
        assertThat(spec.getType(), is(SpecText.Type.IS));
    }

    @Test
    public void shouldReadSpec_text_contains_some_text()  throws IOException {
        SpecText spec = (SpecText)readSpec("text contains \"Some text\" ");
        assertThat(spec.getText(), is("Some text"));
        assertThat(spec.getType(), is(SpecText.Type.CONTAINS));
    }

    @Test
    public void shouldReadSpec_text_startsWith_some_text()  throws IOException {
        SpecText spec = (SpecText)readSpec("text starts  \"Some text\" ");
        assertThat(spec.getText(), is("Some text"));
        assertThat(spec.getType(), is(SpecText.Type.STARTS));
    }

    @Test
    public void shouldReadSpec_text_endssWith_some_text()  throws IOException {
        SpecText spec = (SpecText)readSpec("text ends \"Some text\" ");
        assertThat(spec.getText(), is("Some text"));
        assertThat(spec.getType(), is(SpecText.Type.ENDS));
    }

    @Test
    public void shouldReadSpec_text_matches_some_text()  throws IOException {
        SpecText spec = (SpecText)readSpec("text matches  \"Some * text\" ");
        assertThat(spec.getText(), is("Some * text"));
        assertThat(spec.getType(), is(SpecText.Type.MATCHES));
    }

    @Test
    public void shouldReadSpec_text_lowercase_is() throws IOException {
        SpecText spec = (SpecText)readSpec("text lowercase is \"some text\"");
        assertThat(spec.getText(), is("some text"));
        assertThat(spec.getType(), is(SpecText.Type.IS));
        assertThat(spec.getOperations(), contains("lowercase"));
    }

    @Test
    public void shouldReadSpec_text_lowercase_uppercase_is() throws IOException {
        SpecText spec = (SpecText)readSpec("text lowercase uppercase is \"SOME TEXT\"");
        assertThat(spec.getText(), is("SOME TEXT"));
        assertThat(spec.getType(), is(SpecText.Type.IS));
        assertThat(spec.getOperations(), contains("lowercase", "uppercase"));
    }

    @Test
    public void shouldReadSpec_text_singleline() throws IOException {
        SpecText spec = (SpecText)readSpec("text singleline is \"Some text\"");
        assertThat(spec.getText(), is("Some text"));
        assertThat(spec.getType(), is(SpecText.Type.IS));
        assertThat(spec.getOperations(), contains("singleline"));
    }

    @Test
    public void shouldReadSpec_css_fontsize_is_18px() throws IOException {
        SpecCss spec = (SpecCss)readSpec("css font-size is \"18px\"");
        assertThat(spec.getCssPropertyName(), is("font-size"));
        assertThat(spec.getText(), is("18px"));
        assertThat(spec.getType(), is(SpecText.Type.IS));
    }

    @Test
    public void shouldReadSpec_css_fontsize_starts() throws IOException {
        SpecCss spec = (SpecCss)readSpec("css font-size starts \"18px\"");
        assertThat(spec.getCssPropertyName(), is("font-size"));
        assertThat(spec.getText(), is("18px"));
        assertThat(spec.getType(), is(SpecText.Type.STARTS));
    }

    @Test
    public void shouldReadSpec_css_fontsize_ends() throws IOException {
        SpecCss spec = (SpecCss)readSpec("css font-size ends \"18px\"");
        assertThat(spec.getCssPropertyName(), is("font-size"));
        assertThat(spec.getText(), is("18px"));
        assertThat(spec.getType(), is(SpecText.Type.ENDS));
    }

    @Test
    public void shouldReadSpec_css_fontsize_contains() throws IOException {
        SpecCss spec = (SpecCss)readSpec("css font-size contains \"18px\"");
        assertThat(spec.getCssPropertyName(), is("font-size"));
        assertThat(spec.getText(), is("18px"));
        assertThat(spec.getType(), is(SpecText.Type.CONTAINS));
    }

    @Test
    public void shouldReadSpec_css_fontsize_matches() throws IOException {
        SpecCss spec = (SpecCss)readSpec("css font-size matches \"18px\"");
        assertThat(spec.getCssPropertyName(), is("font-size"));
        assertThat(spec.getText(), is("18px"));
        assertThat(spec.getType(), is(SpecText.Type.MATCHES));
    }

    @Test
    public void shouldReadSpec_above_object_20px()  throws IOException {
        SpecAbove spec = (SpecAbove)readSpec("above object 20px");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getRange(), is(Range.exact(20)));
    }

    @Test
    public void shouldReadSpec_above_object_10_20px()  throws IOException {
        SpecAbove spec = (SpecAbove)readSpec("above object 10 to 20px");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getRange(), is(Range.between(10, 20)));
    }

    @Test
    public void shouldReadSpec_above()  throws IOException {
        SpecAbove spec = (SpecAbove)readSpec("above object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getRange(), is(Range.greaterThanOrEquals(0)));
    }

    @Test
    public void shouldReadSpec_below()  throws IOException {
        SpecBelow spec = (SpecBelow)readSpec("below object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getRange(), is(Range.greaterThanOrEquals(0)));
    }

    @Test
    public void shouldReadSpec_below_object_20px()  throws IOException {
        SpecBelow spec = (SpecBelow)readSpec("below object 20px");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getRange(), is(Range.exact(20)));
    }

    @Test
    public void shouldReadSpec_below_object_10_to_20px()  throws IOException {
        SpecBelow spec = (SpecBelow)readSpec("below object 10 to 20px");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getRange(), is(Range.between(10, 20)));
    }


    @Test
    public void shouldReadSpec_left_of_object_10px() throws IOException {
        SpecLeftOf specLeftOf = (SpecLeftOf)readSpec("left-of object 10px");
        assertThat(specLeftOf.getObject(), is("object"));
        assertThat(specLeftOf.getRange(), is(Range.exact(10)));
    }

    @Test
    public void shouldReadSpec_left_of_object_10_to_20px() throws IOException {
        SpecLeftOf specLeftOf = (SpecLeftOf)readSpec("left-of object 10 to 20px");
        assertThat(specLeftOf.getObject(), is("object"));
        assertThat(specLeftOf.getRange(), is(Range.between(10, 20)));
    }

    @Test
    public void shouldReadSpec_left_of_object() throws IOException {
        SpecLeftOf specLeftOf = (SpecLeftOf)readSpec("left-of object");
        assertThat(specLeftOf.getObject(), is("object"));
        assertThat(specLeftOf.getRange(), is(Range.greaterThanOrEquals(0)));
    }


    @Test
    public void shouldReadSpec_right_of_object_10px() throws IOException {
        SpecRightOf specRightOf = (SpecRightOf)readSpec("right-of object 10px");
        assertThat(specRightOf.getObject(), is("object"));
        assertThat(specRightOf.getRange(), is(Range.exact(10)));
    }

    @Test
    public void shouldReadSpec_right_of_object_10_to_20px() throws IOException {
        SpecRightOf specRightOf = (SpecRightOf)readSpec("right-of object 10 to 20px");
        assertThat(specRightOf.getObject(), is("object"));
        assertThat(specRightOf.getRange(), is(Range.between(10, 20)));
    }


    @Test
    public void shouldReadSpec_right_of_object() throws IOException {
        SpecRightOf specRightOf = (SpecRightOf)readSpec("right-of object");
        assertThat(specRightOf.getObject(), is("object"));
        assertThat(specRightOf.getRange(), is(Range.greaterThanOrEquals(0)));
    }


    @Test(expectedExceptions = {SyntaxException.class},
            expectedExceptionsMessageRegExp = "Missing validation type \\(is, contains, starts, ends, matches\\)"
    )
    public void shouldGiveException_empty_css_spec() throws IOException {
        readSpec("css  \"18px\"");
    }

    @Test(expectedExceptions = {SyntaxException.class},
            expectedExceptionsMessageRegExp = "Unknown validation type: \"18px\""
    )
    public void shouldGiveException_css_without_type() throws IOException {
        readSpec("css font-size \"18px\"");
    }

    @Test
    public void shouldReadSpec_centered_inside_object()  throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered inside object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.INSIDE));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.ALL));
    }

    @Test
    public void shouldReadSpec_centered_horizontally_inside_object()  throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered horizontally inside object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.INSIDE));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.HORIZONTALLY));
    }

    @Test
    public void shouldReadSpec_centered_vertically_inside_object()  throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered vertically inside object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.INSIDE));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.VERTICALLY));
    }

    @Test
    public void shouldReadSpec_centered_all_inside_object()  throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered all inside object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.INSIDE));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.ALL));
    }

    @Test
    public void shouldReadSpec_centered_all_on_object()  throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered all on object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.ALL));
    }

    @Test
    public void shouldReadSpec_centered_on_object()  throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered on object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.ALL));
    }

    @Test
    public void shouldReadSpec_centered_horizontally_on_object()  throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered horizontally on object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.HORIZONTALLY));
    }

    @Test
    public void shouldReadSpec_centered_horizontally_on_object_25px() throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered horizontally on object 25px");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.HORIZONTALLY));
        assertThat(spec.getErrorRate(), is(25));
    }

    @Test
    public void shouldReadSpec_centered_horizontally_on_object_25_px() throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered horizontally on object 25 px");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.HORIZONTALLY));
        assertThat(spec.getErrorRate(), is(25));
    }

    @Test
    public void shouldReadSpec_centered_vertically_on_object() throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered vertically on object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.VERTICALLY));
    }

    @Test
    public void shoulReadSpec_on_object_10px_left() throws IOException {
        SpecOn spec = (SpecOn)readSpec("on edge object 10px left");

        assertThat(spec.getSideHorizontal(), is(TOP));
        assertThat(spec.getSideVertical(), is(LEFT));
        assertThat(spec.getObject(), is("object"));

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(10), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("on edge object 10px left"));
    }

    @Test
    public void shoulReadSpec_on_object_10px_left_20px_top() throws IOException {
        SpecOn spec = (SpecOn)readSpec("on edge object 10px left, 20px top");

        assertThat(spec.getSideHorizontal(), is(TOP));
        assertThat(spec.getSideVertical(), is(LEFT));
        assertThat(spec.getObject(), is("object"));

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(2));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(10), sides(LEFT)), new Location(Range.exact(20), sides(TOP))));
        assertThat(spec.getOriginalText(), is("on edge object 10px left, 20px top"));
    }

    @Test
    public void shouldReadSpec_on_top_object_10px_top_right() throws Exception {
        SpecOn spec = (SpecOn)readSpec("on top edge object 10px top right");

        assertThat(spec.getSideHorizontal(), is(TOP));
        assertThat(spec.getSideVertical(), is(LEFT));
        assertThat(spec.getObject(), is("object"));

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(10), sides(TOP, RIGHT))));
        assertThat(spec.getOriginalText(), is("on top edge object 10px top right"));
    }

    @Test
    public void shouldReadSpec_on_left_object_10px_top_right() throws Exception {
        SpecOn spec = (SpecOn)readSpec("on left edge object 10px top right");

        assertThat(spec.getSideHorizontal(), is(TOP));
        assertThat(spec.getSideVertical(), is(LEFT));
        assertThat(spec.getObject(), is("object"));

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(10), sides(TOP, RIGHT))));
        assertThat(spec.getOriginalText(), is("on left edge object 10px top right"));
    }

    @Test
    public void shouldReadSpec_on_bottom_right_object_10px_top_right() throws Exception {
        SpecOn spec = (SpecOn)readSpec("on right bottom edge object 10px top right");

        assertThat(spec.getSideHorizontal(), is(BOTTOM));
        assertThat(spec.getSideVertical(), is(RIGHT));
        assertThat(spec.getObject(), is("object"));

        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(10), sides(TOP, RIGHT))));
        assertThat(spec.getOriginalText(), is("on right bottom edge object 10px top right"));
    }


    @Test(expectedExceptions = SyntaxException.class,
        expectedExceptionsMessageRegExp = "Missing \"edge\"")
    public void shouldGiveError_missingEdges_forSpec_on() throws Exception {
        readSpec("on top left object 10px");
    }

    @Test
    public void shouldReadSpec_color_scheme_40percent_black_approx_30percent_white() throws Exception {
        SpecColorScheme spec = (SpecColorScheme)readSpec("color-scheme 40% black  , ~30% white");
        List<ColorRange> colors = spec.getColorRanges();
        assertThat(colors.size(), is(2));

        assertThat(colors.get(0).getRange(), is(Range.exact(40)));
        assertThat(colors.get(0).getColorClassifier(), is(new SimpleColorClassifier("black", new Color(0, 0, 0))));
        assertThat(colors.get(1).getRange(), is(Range.between(28, 32)));
        assertThat(colors.get(1).getColorClassifier(), is(new SimpleColorClassifier("white", new Color(255, 255, 255))));
    }

    @Test
    public void shouldReadSpec_color_scheme_greater_than_40percent_ffaa03() throws Exception {
        SpecColorScheme spec = (SpecColorScheme)readSpec("color-scheme > 40% #ffaa03");
        List<ColorRange> colors = spec.getColorRanges();
        assertThat(colors.size(), is(1));

        assertThat(colors.get(0).getRange(), is(Range.greaterThan(40)));
        assertThat(colors.get(0).getColorClassifier(), is(new SimpleColorClassifier("#ffaa03", new Color(255, 170, 3))));
    }

    @Test
    public void shouldReadSpec_color_scheme_40_to_50percent_ffaa03() throws Exception {
        SpecColorScheme spec = (SpecColorScheme)readSpec("color-scheme 40 to 50% red");
        List<ColorRange> colors = spec.getColorRanges();
        assertThat(colors.size(), is(1));

        assertThat(colors.get(0).getRange(), is(Range.between(40, 50)));
        assertThat(colors.get(0).getColorClassifier(), is(new SimpleColorClassifier("red", new Color(255, 0, 0))));
    }


    @Test
    public void shouldReadSpec_color_withShorthand_hexColorNotation() throws Exception {
        SpecColorScheme spec = (SpecColorScheme)readSpec("color-scheme 40% #f3e");
        List<ColorRange> colors = spec.getColorRanges();
        assertThat(colors.size(), is(1));

        assertThat(colors.get(0).getRange(), is(Range.exact(40)));
        assertThat(colors.get(0).getColorClassifier(), is(new SimpleColorClassifier("#f3e", new Color(255, 51, 238))));
    }

    @Test
    public void shouldReadSpec_color_withGradients() throws Exception {
        SpecColorScheme spec = (SpecColorScheme)readSpec("color-scheme 40% white-green-blue");
        List<ColorRange> colors = spec.getColorRanges();
        assertThat(colors.size(), is(1));

        assertThat(colors.get(0).getRange(), is(Range.exact(40)));
        assertThat(colors.get(0).getColorClassifier(), is(new GradientColorClassifier("white-green-blue", asList(
                new Color(255, 255, 255),
                new Color(0, 255, 0),
                new Color(0, 0, 255)
        ))));
    }

    @Test
    public void shouldReadSpec_color_withGradients_2() throws Exception {
        SpecColorScheme spec = (SpecColorScheme)readSpec("color-scheme 40% white - green - blue, 10 to 23% #a3f - #00e");
        List<ColorRange> colors = spec.getColorRanges();
        assertThat(colors.size(), is(2));

        assertThat(colors.get(0).getRange(), is(Range.exact(40)));
        assertThat(colors.get(0).getColorClassifier(), is(new GradientColorClassifier("white - green - blue", asList(
                new Color(255, 255, 255),
                new Color(0, 255, 0),
                new Color(0, 0, 255)
        ))));

        assertThat(colors.get(1).getRange(), is(Range.between(10, 23)));
        assertThat(colors.get(1).getColorClassifier(), is(new GradientColorClassifier("#a3f - #00e", asList(
                new Color(170, 51, 255),
                new Color(0, 0, 238)
        ))));
    }
    @Test
    public void shouldReadSpec_image_withMaxPercentageError() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, error 2.4%");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getErrorRate().getValue(), is(2.4));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PERCENT));
        assertThat(spec.getTolerance(), is(25));
    }

    @Test
    public void shouldReadSpec_image_withMaxPixelsError() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, error 112 px");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getErrorRate().getValue(), is(112.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PIXELS));
        assertThat(spec.getTolerance(), is(25));
    }

    @Test
    public void shouldReadSpec_image_withMaxPixelsError_tolerance5() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, error 112 px, tolerance 5");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getErrorRate().getValue(), is(112.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PIXELS));
        assertThat(spec.getTolerance(), is(5));
        assertThat(spec.isStretch(), is(false));
        assertThat(spec.isCropIfOutside(), is(false));
    }

    @Test
    public void shouldReadSpec_image_withMaxPixelsError_tolerance5_stretch() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, error 112 px, tolerance 5, stretch");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getErrorRate().getValue(), is(112.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PIXELS));
        assertThat(spec.getTolerance(), is(5));
        assertThat(spec.isStretch(), is(true));
    }

    @Test
    public void shouldReadSpec_image_withCropIfOutside() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, crop-if-outside");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.isCropIfOutside(), is(true));
    }

    @Test
    public void shouldReadSpec_image_withMaxPixelsError_tolerance5_filterBlur2() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, error 112 px, tolerance 5, filter blur 2");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getErrorRate().getValue(), is(112.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PIXELS));
        assertThat(spec.getTolerance(), is(5));
        assertThat(spec.getOriginalFilters().size(), is(1));
        assertThat(spec.getSampleFilters().size(), is(1));

        assertThat(((BlurFilter)spec.getOriginalFilters().get(0)).getRadius(), is(2));
        assertThat(((BlurFilter)spec.getSampleFilters().get(0)).getRadius(), is(2));
    }

    @Test
    public void shouldReadSpec_image_withMaxPixelsError_tolerance5_filterABlur2() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, error 112 px, tolerance 5, filter-a blur 2");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getErrorRate().getValue(), is(112.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PIXELS));
        assertThat(spec.getTolerance(), is(5));
        assertThat(spec.getOriginalFilters().size(), is(1));
        assertThat(spec.getSampleFilters().size(), is(0));

        assertThat(((BlurFilter)spec.getOriginalFilters().get(0)).getRadius(), is(2));
    }

    @Test
    public void shouldReadSpec_image_withMaxPixelsError_tolerance5_filterBBlur2() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, error 112 px, tolerance 5, filter-b blur 2");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getErrorRate().getValue(), is(112.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PIXELS));
        assertThat(spec.getTolerance(), is(5));
        assertThat(spec.getOriginalFilters().size(), is(0));
        assertThat(spec.getSampleFilters().size(), is(1));

        assertThat(((BlurFilter)spec.getSampleFilters().get(0)).getRadius(), is(2));
    }


    @Test
    public void shouldReadSpec_image_withMaxPixelsError_tolerance5_filterBlur2_filterDenoise1() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, error 112 px, filter blur 2, filter denoise 4, tolerance 5");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getErrorRate().getValue(), is(112.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PIXELS));
        assertThat(spec.getTolerance(), is(5));

        assertThat(spec.getOriginalFilters().size(), is(2));

        BlurFilter filter1 = (BlurFilter) spec.getOriginalFilters().get(0);
        assertThat(filter1.getRadius(), is(2));

        DenoiseFilter filter2 = (DenoiseFilter) spec.getOriginalFilters().get(1);
        assertThat(filter2.getRadius(), is(4));
    }

    @Test
    public void shouldReadSpec_image_withMaxPixelsError_tolerance5_filterBlur2_filterSaturation10_mapFilterDenoise1() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, error 112 px, filter blur 2, filter saturation 10, map-filter denoise 4, tolerance 5");
        assertThat(spec.getErrorRate().getValue(), is(112.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PIXELS));
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getTolerance(), is(5));

        assertThat(spec.getOriginalFilters().size(), is(2));
        assertThat(spec.getSampleFilters().size(), is(2));
        assertThat(spec.getMapFilters().size(), is(1));

        assertThat(((BlurFilter)spec.getOriginalFilters().get(0)).getRadius(), is(2));
        assertThat(((BlurFilter)spec.getSampleFilters().get(0)).getRadius(), is(2));

        assertThat(((SaturationFilter)spec.getOriginalFilters().get(1)).getLevel(), is(10));
        assertThat(((SaturationFilter)spec.getSampleFilters().get(1)).getLevel(), is(10));

        DenoiseFilter filter2 = (DenoiseFilter) spec.getMapFilters().get(0);
        assertThat(filter2.getRadius(), is(4));

    }

    @Test
    public void shouldReadSpec_image_withMask() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file image.png, filter mask color-scheme-image-1.png");

        assertThat(spec.getImagePaths(), contains("image.png"));
        assertThat(spec.getOriginalFilters().size(), is(1));
        assertThat(spec.getOriginalFilters().get(0), is(instanceOf(MaskFilter.class)));

        assertThat(spec.getSampleFilters().size(), is(1));
        assertThat(spec.getSampleFilters().get(0), is(instanceOf(MaskFilter.class)));
    }

    @Test
    public void shouldReadSpec_image_withMaxPixelsError_andArea() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, error 112 px, area 10 10 100 20");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getErrorRate().getValue(), is(112.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PIXELS));
        assertThat(spec.getTolerance(), is(25));
        assertThat(spec.getSelectedArea(), is(new Rect(10,10,100,20)));
    }

    @Test
    public void shouldReadSpec_image_withAnalyzeOffset() throws IOException {
        SpecImage spec = (SpecImage)readSpec("image file imgs/image.png, analyze-offset 5");
        assertThat(spec.getImagePaths(), contains("imgs/image.png"));
        assertThat(spec.getAnalyzeOffset(), is(5));
    }


    @Test
    public void shouldReadSpec_image_andBuildImagePath_withContextPath() throws IOException {
        SpecImage spec = (SpecImage) readSpec("image file image.png", "some-component/specs");
        assertThat(spec.getImagePaths(), contains("some-component/specs/image.png"));
    }

    /**
     * Comes from https//github.com/galenframework/galen/issues/171
     * @throws IOException
     */
    @Test
    public void shouldReadSpec_image_toleranceAndErrorRate_fromConfig() throws IOException {
        System.setProperty("galen.spec.image.tolerance", "21");
        System.setProperty("galen.spec.image.error", "121%");
        SpecImage spec = (SpecImage)readSpec("image file image.png");

        assertThat(spec.getTolerance(), is(21));
        assertThat(spec.getErrorRate().getValue(), is(121.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PERCENT));


        System.getProperties().remove("galen.spec.image.tolerance");
        System.getProperties().remove("galen.spec.image.error");
    }

    @Test
    public void shouldReadSpec_image_replaceColors() throws IOException {
        SpecImage specImage = (SpecImage) readSpec("image file image.png, filter replace-colors #000-#333 #f0f0f0 #a0a0a0-#a0b0a0-#a0b0c0 with #111 tolerance 30 radius 2");

        assertThat(specImage.getOriginalFilters().size(), is(1));
        assertThat(specImage.getOriginalFilters().get(0), is(instanceOf(ReplaceColorsFilter.class)));

        ReplaceColorsFilter filter = (ReplaceColorsFilter) specImage.getOriginalFilters().get(0);

        assertThat(filter.getReplaceColorsDefinitions().size(), is(1));
        ReplaceColorsDefinition replaceColorsDefinitions = filter.getReplaceColorsDefinitions().get(0);
        assertThat(replaceColorsDefinitions.getReplaceColor(), is(new Color(17, 17, 17)));

        assertThat(replaceColorsDefinitions.getTolerance(), is(30));
        assertThat(replaceColorsDefinitions.getRadius(), is(2));

        assertThat(replaceColorsDefinitions.getColorClassifiers().size(), is(3));
        assertThat(replaceColorsDefinitions.getColorClassifiers().get(0), instanceOf(GradientColorClassifier.class));
        GradientColorClassifier gradient = (GradientColorClassifier) replaceColorsDefinitions.getColorClassifiers().get(0);
        assertThat(gradient.getName(), is("#000-#333"));

        assertThat(replaceColorsDefinitions.getColorClassifiers().get(1), instanceOf(SimpleColorClassifier.class));
        SimpleColorClassifier simple = (SimpleColorClassifier) replaceColorsDefinitions.getColorClassifiers().get(1);
        assertThat(simple.getName(), is("#f0f0f0"));

        assertThat(replaceColorsDefinitions.getColorClassifiers().get(2), instanceOf(GradientColorClassifier.class));
        gradient = (GradientColorClassifier) replaceColorsDefinitions.getColorClassifiers().get(2);
        assertThat(gradient.getName(), is("#a0a0a0-#a0b0a0-#a0b0c0"));
    }

    @Test
    public void shouldReadSpec_image_ignoredObjects() throws IOException {
        SpecImage spec = (SpecImage) readSpec("image file img.png, ignore-objects [menu_item-*, &excluded_objects], error 10px, ignore-objects one_more_obj");
        assertThat(spec.getImagePaths(), contains("img.png"));
        assertThat(spec.getIgnoredObjectExpressions(), contains("menu_item-*, &excluded_objects", "one_more_obj"));
        assertThat(spec.getErrorRate().getValue(), is(10.0));
        assertThat(spec.getErrorRate().getType(), is(SpecImage.ErrorRateType.PIXELS));
    }

    @Test public void shouldReadSpec_image_filter_edges() throws IOException {
        SpecImage spec = (SpecImage) readSpec("image file img.png, filter edges 34");
        assertThat(spec.getImagePaths(), contains("img.png"));
        assertThat(spec.getOriginalFilters().size(), is(1));
        assertThat(spec.getOriginalFilters().get(0), is(instanceOf(EdgesFilter.class)));

        EdgesFilter filter = (EdgesFilter) spec.getOriginalFilters().get(0);
        assertThat(filter.getTolerance(), is(34));
    }

    @Test
    public void shouldReadSpec_component() throws IOException {
        SpecComponent spec = (SpecComponent)readSpec("component some.spec");
        assertThat(spec.isFrame(), is(false));
        assertThat(spec.getSpecPath(), is("some.spec"));
        assertThat(spec.getOriginalText(), is("component some.spec"));
    }

    @Test
    public void shouldReadSpec_component_frame() throws IOException {
        SpecComponent spec = (SpecComponent)readSpec("component frame some.spec");
        assertThat(spec.isFrame(), is(true));
        assertThat(spec.getSpecPath(), is("some.spec"));
        assertThat(spec.getOriginalText(), is("component frame some.spec"));
    }

    @Test
    public void shouldReadSpec_component_withArguments_andRecogniseBasicTypes() throws IOException {
        SpecComponent spec = (SpecComponent)readSpec("component some.gspec, arg1 1, arg2 2.4, arg3 true, arg4 false, arg5 something, arg6 \"surrounded in quotes\" ");
        assertThat(spec.isFrame(), is(false));
        assertThat(spec.getSpecPath(), is("some.gspec"));
        assertThat(spec.getArguments(), is((Map<String, Object>)new HashMap<String, Object>(){{
            put("arg1", 1L);
            put("arg2", 2.4d);
            put("arg3", true);
            put("arg4", false);
            put("arg5", "something");
            put("arg6", "surrounded in quotes");
        }}));
    }

    @Test
    public void shouldReadSpec_count_any_pattern_is_6() throws IOException {
        SpecCount spec = (SpecCount)readSpec("count any menu-item-* is 6");
        assertThat(spec.getPattern(), is("menu-item-*"));
        assertThat(spec.getAmount(), is(Range.exact(6)));
        assertThat(spec.getFetchType(), is(SpecCount.FetchType.ANY));
        assertThat(spec.getOriginalText(), is("count any menu-item-* is 6"));
    }

    @Test
    public void shouldReadSpec_count_visible_pattern_is_6() throws IOException {
        SpecCount spec = (SpecCount)readSpec("count visible menu-item-* is 6");
        assertThat(spec.getPattern(), is("menu-item-*"));
        assertThat(spec.getAmount(), is(Range.exact(6)));
        assertThat(spec.getFetchType(), is(SpecCount.FetchType.VISIBLE));
        assertThat(spec.getOriginalText(), is("count visible menu-item-* is 6"));
    }

    @Test
    public void shouldReadSpec_absent_visible_pattern_is_6() throws IOException {
        SpecCount spec = (SpecCount)readSpec("count absent menu-item-* is 6");
        assertThat(spec.getPattern(), is("menu-item-*"));
        assertThat(spec.getAmount(), is(Range.exact(6)));
        assertThat(spec.getFetchType(), is(SpecCount.FetchType.ABSENT));
        assertThat(spec.getOriginalText(), is("count absent menu-item-* is 6"));
    }
    @Test
    public void shouldReadSpec_count_pattern_in_double_qoutes_is_6() throws IOException {
        SpecCount spec = (SpecCount)readSpec("count any \"menu-item-*, box-*\" is 6");
        assertThat(spec.getPattern(), is("menu-item-*, box-*"));
        assertThat(spec.getAmount(), is(Range.exact(6)));
        assertThat(spec.getOriginalText(), is("count any \"menu-item-*, box-*\" is 6"));
    }

    @Test
    public void shouldReadSpec_count_pattern_is_6_to_8() throws IOException {
        SpecCount spec = (SpecCount)readSpec("count any menu-item-* is 6 to 8");
        assertThat(spec.getPattern(), is("menu-item-*"));
        assertThat(spec.getAmount(), is(Range.between(6, 8)));
        assertThat(spec.getOriginalText(), is("count any menu-item-* is 6 to 8"));
    }

    @Test
    public void shouldReadSpec_count_pattern_is__lessThan_8() throws IOException {
        SpecCount spec = (SpecCount)readSpec("count any menu-item-* is < 8");
        assertThat(spec.getPattern(), is("menu-item-*"));
        assertThat(spec.getAmount(), is(Range.lessThan(8)));
        assertThat(spec.getOriginalText(), is("count any menu-item-* is < 8"));
    }

    @Test
    public void shouldReadSpec_count_pattern_is__biggerThan_8() throws IOException {
        SpecCount spec = (SpecCount)readSpec("count any menu-item-* is > 8");
        assertThat(spec.getPattern(), is("menu-item-*"));
        assertThat(spec.getAmount(), is(Range.greaterThan(8)));
        assertThat(spec.getOriginalText(), is("count any menu-item-* is > 8"));
    }

    @Test(expectedExceptions = SyntaxException.class,
            expectedExceptionsMessageRegExp = "Couldn't process: whatever non parsed arguments"
    )
    public void shouldThrowError_whenSpecHasNotParsed_theWholeText() {
        readSpec("left-of some-object 10 px whatever non parsed arguments");
    }

    private Spec readSpec(String specText) {
        return new SpecReader().read(specText);
    }

    private Spec readSpec(String specText, String contextPath) {
        return new SpecReader().read(specText, contextPath);
    }

    private List<Side> sides(Side...sides) {
        return asList(sides);
    }
}
