/*******************************************************************************
* Copyright 2013 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.tests.specs.reader;


import static net.mindengine.galen.specs.Side.BOTTOM;
import static net.mindengine.galen.specs.Side.LEFT;
import static net.mindengine.galen.specs.Side.RIGHT;
import static net.mindengine.galen.specs.Side.TOP;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.is;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;
import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.Alignment;
import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecAbove;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.specs.SpecBelow;
import net.mindengine.galen.specs.SpecCentered;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.specs.SpecHeight;
import net.mindengine.galen.specs.SpecHorizontally;
import net.mindengine.galen.specs.SpecInside;
import net.mindengine.galen.specs.SpecNear;
import net.mindengine.galen.specs.SpecOn;
import net.mindengine.galen.specs.SpecText;
import net.mindengine.galen.specs.SpecVertically;
import net.mindengine.galen.specs.SpecVisible;
import net.mindengine.galen.specs.SpecWidth;
import net.mindengine.galen.specs.reader.SpecReader;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

@Test
public class SpecsReaderTest {

    
    @Test
    public void shouldReadSpec_inside_object_10px_right() throws IOException {
        Spec spec = readSpec("inside: object 10px right");
        SpecInside specInside = (SpecInside) spec;

        assertThat(specInside.getObject(), is("object"));
        assertThat(specInside.getPartly(), is(false));
        
        List<Location> locations = specInside.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(specInside.getLocations(), contains(new Location(Range.exact(10), sides(RIGHT))));
        assertThat(spec.getOriginalText(), is("inside: object 10px right"));
    }
    
    @Test
    public void shouldReadSpec_inside_partly_object_10px_right()  throws IOException {
        Spec spec = readSpec("inside partly: object 10px right");
        SpecInside specInside = (SpecInside) spec;

        assertThat(specInside.getObject(), is("object"));
        assertThat(specInside.getPartly(), is(true));
        
        List<Location> locations = specInside.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(specInside.getLocations(), contains(new Location(Range.exact(10), sides(RIGHT))));
        assertThat(spec.getOriginalText(), is("inside partly: object 10px right"));
    }
    

    @Test
    public void shouldReadSpec_inside_object_10_to_30px_left()  throws IOException {
        Spec spec = readSpec("inside: object 10 to 30px left");
        SpecInside specInside = (SpecInside) spec;

        assertThat(specInside.getObject(), is("object"));
        
        List<Location> locations = specInside.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(specInside.getLocations(), contains(new Location(Range.between(10, 30), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("inside: object 10 to 30px left"));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left()  throws IOException {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(TOP, LEFT))));
        assertThat(spec.getOriginalText(), is("inside: object 25px top left"));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left_comma_10_to_20px_bottom()  throws IOException {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left, 10 to 20px bottom");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(2));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(TOP, LEFT)),
                new Location(Range.between(10, 20), sides(BOTTOM))));
        assertThat(spec.getOriginalText(), is("inside: object 25px top left, 10 to 20px bottom"));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_bottom_right()  throws IOException {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px bottom right");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(BOTTOM, RIGHT))));
        assertThat(spec.getOriginalText(), is("inside: object 25px bottom right"));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left_right_bottom()  throws IOException {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left right bottom ");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25), sides(TOP, LEFT, RIGHT, BOTTOM))));
        assertThat(spec.getOriginalText(), is("inside: object 25px top left right bottom"));
    }
    
    @Test public void shouldReadSpec_inside_object_20px_left_and_approximate_30px_top()  throws IOException {
        SpecInside spec = (SpecInside)readSpec("inside: object 20px left, ~30px top");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(2));
        
        Assert.assertEquals(new Location(Range.exact(20), sides(LEFT)), spec.getLocations().get(0));
        Assert.assertEquals(new Location(Range.between(28, 32), sides(TOP)), spec.getLocations().get(1));
        
        assertThat(spec.getOriginalText(), is("inside: object 20px left, ~30px top"));
    }
        
    @Test
    public void shouldReadSpec_contains()  throws IOException {
        Spec spec = readSpec("contains: object, menu, button");
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("contains: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_contains_with_regex()  throws IOException {
        Spec spec = readSpec("contains: menu-item-*");
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.getChildObjects(), contains("menu-item-*"));
        assertThat(spec.getOriginalText(), is("contains: menu-item-*"));
    }
    
    @Test
    public void shouldReadSpec_contains_partly()  throws IOException {
        Spec spec = readSpec("contains partly: object, menu, button");
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.isPartly(), is(true));
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("contains partly: object, menu, button"));
    }
    
    @Test 
    public void shouldReadSpec_near_button_10_to_20px_left()  throws IOException {
        SpecNear spec = (SpecNear) readSpec("near: button 10 to 20px left");
        
        assertThat(spec.getObject(), is("button"));
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(10, 20), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("near: button 10 to 20px left"));
    }
    
    @Test 
    public void shouldReadSpec_near_button_10_to_20px_top_right()  throws IOException {
        SpecNear spec = (SpecNear) readSpec("near: button 10 to 20px top right");
        assertThat(spec.getObject(), is("button"));
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(10, 20), sides(TOP, RIGHT))));
        assertThat(spec.getOriginalText(), is("near: button 10 to 20px top right"));
    }
    
    @Test
    public void shouldReadSpec_near_button_approx_0px_left()  throws IOException {
        SpecNear spec = (SpecNear) readSpec("near: button ~0px left");
        assertThat(spec.getObject(), is("button"));
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(-2, 2), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("near: button ~0px left"));
    }
    
    
    @Test
    public void shouldReadSpec_horizontally_centered()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally centered: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.CENTERED));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("horizontally centered: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally_top()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally top: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.TOP));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("horizontally top: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally_bottom()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally bottom: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.BOTTOM));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("horizontally bottom: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("horizontally: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally_all()  throws IOException {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally all: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("horizontally all: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_centered()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("vertically  centered: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.CENTERED));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("vertically  centered: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_left()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("vertically left: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.LEFT));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("vertically left: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_right()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("vertically right: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.RIGHT));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("vertically right: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_vertically()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("vertically: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("vertically: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_all()  throws IOException {
        SpecVertically spec = (SpecVertically) readSpec("vertically all: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("vertically all: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_absent()  throws IOException {
        Spec spec = readSpec("absent");
        assertThat(spec, Matchers.instanceOf(SpecAbsent.class));
        assertThat(spec.getOriginalText(), is("absent"));
    }
    
    @Test
    public void shouldReadSpec_visible()  throws IOException {
        Spec spec = readSpec("visible");
        assertThat(spec, Matchers.instanceOf(SpecVisible.class));
        assertThat(spec.getOriginalText(), is("visible"));
    }
    
    @Test
    public void shouldReadSpec_width_10px()  throws IOException {
        SpecWidth spec = (SpecWidth) readSpec("width: 10px");
        assertThat(spec.getRange(), is(Range.exact(10)));
        assertThat(spec.getOriginalText(), is("width: 10px"));
    }
    
    @Test
    public void shouldReadSpec_width_5_to_8px()  throws IOException {
        SpecWidth spec = (SpecWidth) readSpec("width: 5 to 8px");
        assertThat(spec.getRange(), is(Range.between(5, 8)));
        assertThat(spec.getOriginalText(), is("width: 5 to 8px"));
    }
    
    @Test
    public void shouldReadSpec_width_100_percent_of_other_object_width()  throws IOException {
        SpecWidth spec = (SpecWidth) readSpec("width: 100% of main-big-container/width");
        assertThat(spec.getRange(), is(Range.exact(100).withPercentOf("main-big-container/width")));
        assertThat(spec.getOriginalText(), is("width: 100% of main-big-container/width"));
    }
    
    @Test
    public void shouldReadSpec_height_10px()  throws IOException {
        SpecHeight spec = (SpecHeight) readSpec("height: 10px");
        assertThat(spec.getRange(), is(Range.exact(10)));
        assertThat(spec.getOriginalText(), is("height: 10px"));
    }
    
    @Test
    public void shouldReadSpec_height_5_to_8px()  throws IOException {
        SpecHeight spec = (SpecHeight) readSpec("height: 5 to 8px");
        assertThat(spec.getRange(), is(Range.between(5, 8)));
        assertThat(spec.getOriginalText(), is("height: 5 to 8px"));
    }
    
    @Test
    public void shouldReadSpec_text_is_some_text()  throws IOException {
        SpecText spec = (SpecText)readSpec("text is:  Some text ");
        assertThat(spec.getText(), is("Some text"));
        assertThat(spec.getType(), is(SpecText.Type.IS));
    }
    
    @Test
    public void shouldReadSpec_text_is_some_text_2()  throws IOException {
        SpecText spec = (SpecText)readSpec("text is:Some text with colon:");
        assertThat(spec.getText(), is("Some text with colon:"));
        assertThat(spec.getType(), is(SpecText.Type.IS));
    }
    
    @Test
    public void shouldReadSpec_text_is_empty()  throws IOException {
        SpecText spec = (SpecText)readSpec("text is:  ");
        assertThat(spec.getText(), is(""));
        assertThat(spec.getType(), is(SpecText.Type.IS));
    }
    
    @Test
    public void shouldReadSpec_text_is_empty_2()  throws IOException {
        SpecText spec = (SpecText)readSpec("text is:");
        assertThat(spec.getText(), is(""));
        assertThat(spec.getType(), is(SpecText.Type.IS));
    }
    
    @Test
    public void shouldReadSpec_text_contains_some_text()  throws IOException {
        SpecText spec = (SpecText)readSpec("text contains:  Some text ");
        assertThat(spec.getText(), is("Some text"));
        assertThat(spec.getType(), is(SpecText.Type.CONTAINS));
    }
    
    @Test
    public void shouldReadSpec_text_startsWith_some_text()  throws IOException {
        SpecText spec = (SpecText)readSpec("text starts:  Some text ");
        assertThat(spec.getText(), is("Some text"));
        assertThat(spec.getType(), is(SpecText.Type.STARTS));
    }
    
    @Test
    public void shouldReadSpec_text_endssWith_some_text()  throws IOException {
        SpecText spec = (SpecText)readSpec("text ends:  Some text ");
        assertThat(spec.getText(), is("Some text"));
        assertThat(spec.getType(), is(SpecText.Type.ENDS));
    }
    
    @Test
    public void shouldReadSpec_text_matches_some_text()  throws IOException {
        SpecText spec = (SpecText)readSpec("text matches:  Some * text ");
        assertThat(spec.getText(), is("Some * text"));
        assertThat(spec.getType(), is(SpecText.Type.MATCHES));
    }
    
    @Test 
    public void shouldReadSpec_above_object_20px()  throws IOException {
    	SpecAbove spec = (SpecAbove)readSpec("above: object 20px");
    	assertThat(spec.getObject(), is("object"));
    	assertThat(spec.getRange(), is(Range.exact(20)));
    }
    
    @Test 
    public void shouldReadSpec_above_object_10_20px()  throws IOException {
    	SpecAbove spec = (SpecAbove)readSpec("above: object 10 to 20px");
    	assertThat(spec.getObject(), is("object"));
    	assertThat(spec.getRange(), is(Range.between(10, 20)));
    }
    
    @Test 
    public void shouldReadSpec_below_object_20px()  throws IOException {
    	SpecBelow spec = (SpecBelow)readSpec("below: object 20px");
    	assertThat(spec.getObject(), is("object"));
    	assertThat(spec.getRange(), is(Range.exact(20)));
    }
    
    @Test 
    public void shouldReadSpec_below_object_10_to_20px()  throws IOException {
    	SpecBelow spec = (SpecBelow)readSpec("below: object 10 to 20px");
    	assertThat(spec.getObject(), is("object"));
    	assertThat(spec.getRange(), is(Range.between(10, 20)));
    }
    
    @Test 
    public void shouldReadSpec_centered_inside_object()  throws IOException {
    	SpecCentered spec = (SpecCentered)readSpec("centered inside: object");
    	assertThat(spec.getObject(), is("object"));
    	assertThat(spec.getLocation(), is(SpecCentered.Location.INSIDE));
    	assertThat(spec.getAlignment(), is(SpecCentered.Alignment.ALL));
    }
    
    @Test 
    public void shouldReadSpec_centered_horizontally_inside_object()  throws IOException {
    	SpecCentered spec = (SpecCentered)readSpec("centered horizontally inside: object");
    	assertThat(spec.getObject(), is("object"));
    	assertThat(spec.getLocation(), is(SpecCentered.Location.INSIDE));
    	assertThat(spec.getAlignment(), is(SpecCentered.Alignment.HORIZONTALLY));
    }
    
    @Test 
    public void shouldReadSpec_centered_vertically_inside_object()  throws IOException {
    	SpecCentered spec = (SpecCentered)readSpec("centered vertically inside: object");
    	assertThat(spec.getObject(), is("object"));
    	assertThat(spec.getLocation(), is(SpecCentered.Location.INSIDE));
    	assertThat(spec.getAlignment(), is(SpecCentered.Alignment.VERTICALLY));
    }
    
    @Test 
    public void shouldReadSpec_centered_all_inside_object()  throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered all inside: object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.INSIDE));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.ALL));
    }
    
    @Test 
    public void shouldReadSpec_centered_all_on_object()  throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered all on: object");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.ALL));
    }
    
    @Test 
    public void shouldReadSpec_centered_on_object()  throws IOException {
    	SpecCentered spec = (SpecCentered)readSpec("centered on: object");
    	assertThat(spec.getObject(), is("object"));
    	assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
    	assertThat(spec.getAlignment(), is(SpecCentered.Alignment.ALL));
    }
    
    @Test 
    public void shouldReadSpec_centered_horizontally_on_object()  throws IOException {
    	SpecCentered spec = (SpecCentered)readSpec("centered horizontally on: object");
    	assertThat(spec.getObject(), is("object"));
    	assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
    	assertThat(spec.getAlignment(), is(SpecCentered.Alignment.HORIZONTALLY));
    }
    
    @Test 
    public void shouldReadSpec_centered_horizontally_on_object_25px() throws IOException {
        SpecCentered spec = (SpecCentered)readSpec("centered horizontally on: object 25px");
        assertThat(spec.getObject(), is("object"));
        assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
        assertThat(spec.getAlignment(), is(SpecCentered.Alignment.HORIZONTALLY));
        assertThat(spec.getErrorRate(), is(25));
    }
    
    @Test 
    public void shouldReadSpec_centered_vertically_on_object() throws IOException {
    	SpecCentered spec = (SpecCentered)readSpec("centered vertically on: object");
    	assertThat(spec.getObject(), is("object"));
    	assertThat(spec.getLocation(), is(SpecCentered.Location.ON));
    	assertThat(spec.getAlignment(), is(SpecCentered.Alignment.VERTICALLY));
    }
    
    @Test
    public void shoulReadSpec_on_object_10px_left() throws IOException {
        SpecOn spec = (SpecOn)readSpec("on: object 10px left");
        
        assertThat(spec.getObject(), is("object"));
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(10), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("on: object 10px left"));
    }
    
    @Test
    public void shoulReadSpec_on_object_10px_left_20px_top() throws IOException {
        SpecOn spec = (SpecOn)readSpec("on: object 10px left, 20px top");
        
        assertThat(spec.getObject(), is("object"));
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(2));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(10), sides(LEFT)), new Location(Range.exact(20), sides(TOP))));
        assertThat(spec.getOriginalText(), is("on: object 10px left, 20px top"));
    }
    
    @Test(expectedExceptions={NullPointerException.class}, expectedExceptionsMessageRegExp="Spec text should not be null") 
    public void givesError_whenTextIsNull() throws IOException {
        readSpec(null);
    }
    
    @Test(expectedExceptions={SyntaxException.class}, expectedExceptionsMessageRegExp="Spec text should not be empty") 
    public void givesError_whenTextIsEmpty() throws IOException {
        readSpec(" ");
    }
    
    @Test(expectedExceptions={SyntaxException.class}, expectedExceptionsMessageRegExp="Incorrect error rate syntax: \" 23 to 123px\"") 
    public void givesError_withIncorrect_errorRate_inSpec_centered() throws IOException {
        readSpec("centered horizontally inside: object 23 to 123px");
    }
    
    private Spec readSpec(String specText) throws IOException {
        return new SpecReader().read(specText);
    }
    
    private List<Side> sides(Side...sides) {
        return Arrays.asList(sides);
    }

}
