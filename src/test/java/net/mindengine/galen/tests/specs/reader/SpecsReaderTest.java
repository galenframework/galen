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

import java.util.Arrays;
import java.util.List;

import net.mindengine.galen.parser.SyntaxException;
import net.mindengine.galen.specs.Alignment;
import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Side;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.specs.SpecContains;
import net.mindengine.galen.specs.SpecHeight;
import net.mindengine.galen.specs.SpecHorizontally;
import net.mindengine.galen.specs.SpecInside;
import net.mindengine.galen.specs.SpecNear;
import net.mindengine.galen.specs.SpecVertically;
import net.mindengine.galen.specs.SpecWidth;
import net.mindengine.galen.specs.reader.SpecReader;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

@Test
public class SpecsReaderTest {

    
    @Test
    public void shouldReadSpec_inside_object_10px_right() {
        Spec spec = readSpec("inside: object 10px right");
        SpecInside specInside = (SpecInside) spec;

        assertThat(specInside.getObject(), is("object"));
        
        List<Location> locations = specInside.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(specInside.getLocations(), contains(new Location(Range.exact(10), sides(RIGHT))));
        assertThat(spec.getOriginalText(), is("inside: object 10px right"));
    }
    

    @Test
    public void shouldReadSpec_inside_object_10_to_30px_left() {
        Spec spec = readSpec("inside: object 10 to 30px left");
        SpecInside specInside = (SpecInside) spec;

        assertThat(specInside.getObject(), is("object"));
        
        List<Location> locations = specInside.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(specInside.getLocations(), contains(new Location(Range.between(10, 30), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("inside: object 10 to 30px left"));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(TOP, LEFT))));
        assertThat(spec.getOriginalText(), is("inside: object 25px top left"));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left_comma_10_to_20px_bottom() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left, 10 to 20px bottom");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(2));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(TOP, LEFT)),
                new Location(Range.between(10, 20), sides(BOTTOM))));
        assertThat(spec.getOriginalText(), is("inside: object 25px top left, 10 to 20px bottom"));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_bottom_right() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px bottom right");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(BOTTOM, RIGHT))));
        assertThat(spec.getOriginalText(), is("inside: object 25px bottom right"));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left_right_bottom() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left right bottom ");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25), sides(TOP, LEFT, RIGHT, BOTTOM))));
        assertThat(spec.getOriginalText(), is("inside: object 25px top left right bottom"));
    }
    
    @Test public void shouldReadSpec_inside_object_20px_left_and_approximate_30px_top() {
        SpecInside spec = (SpecInside)readSpec("inside: object ~20px left, ~30px top");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(2));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(20), sides(LEFT)), new Location(Range.between(29, 31), sides(TOP))));
        assertThat(spec.getOriginalText(), is("inside: object 20px left, ~30px top"));
    }
        
    @Test
    public void shouldReadSpec_contains() {
        Spec spec = readSpec("contains: object, menu, button");
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("contains: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_contains_with_regex() {
        Spec spec = readSpec("contains: menu-item-*");
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.getChildObjects(), contains("menu-item-*"));
        assertThat(spec.getOriginalText(), is("contains: menu-item-*"));
    }
    
    @Test
    public void shouldReadSpec_contains_partly() {
        Spec spec = readSpec("contains partly: object, menu, button");
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.isPartly(), is(true));
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("contains partly: object, menu, button"));
    }
    
    @Test 
    public void shouldReadSpec_near_button_10_to_20px_left() {
        SpecNear spec = (SpecNear) readSpec("near: button 10 to 20px left");
        
        assertThat(spec.getObject(), is("button"));
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(10, 20), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("near: button 10 to 20px left"));
    }
    
    @Test 
    public void shouldReadSpec_near_button_10_to_20px_top_right() {
        SpecNear spec = (SpecNear) readSpec("near: button 10 to 20px top right");
        assertThat(spec.getObject(), is("button"));
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(10, 20), sides(TOP, RIGHT))));
        assertThat(spec.getOriginalText(), is("near: button 10 to 20px top right"));
    }
    
    @Test
    public void shouldReadSpec_near_button_approx_0px_left() {
        SpecNear spec = (SpecNear) readSpec("near: button ~0px left");
        assertThat(spec.getObject(), is("button"));
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(-1, 1), sides(LEFT))));
        assertThat(spec.getOriginalText(), is("near: button ~0px left"));
    }
    
    
    @Test
    public void shouldReadSpec_horizontally_centered() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally centered: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.CENTERED));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("horizontally centered: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally_top() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally top: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.TOP));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("horizontally top: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally_bottom() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally bottom: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.BOTTOM));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("horizontally bottom: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("horizontally: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally_all() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally all: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("horizontally all: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_centered() {
        SpecVertically spec = (SpecVertically) readSpec("vertically  centered: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.CENTERED));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("vertically  centered: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_left() {
        SpecVertically spec = (SpecVertically) readSpec("vertically left: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.LEFT));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("vertically left: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_right() {
        SpecVertically spec = (SpecVertically) readSpec("vertically right: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.RIGHT));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("vertically right: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_vertically() {
        SpecVertically spec = (SpecVertically) readSpec("vertically: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("vertically: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_all() {
        SpecVertically spec = (SpecVertically) readSpec("vertically all: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.ALL));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
        assertThat(spec.getOriginalText(), is("vertically all: object, menu, button"));
    }
    
    @Test
    public void shouldReadSpec_absent() {
        Spec spec = readSpec("absent");
        assertThat(spec, Matchers.instanceOf(SpecAbsent.class));
        assertThat(spec.getOriginalText(), is("absent"));
    }
    
    @Test
    public void shouldReadSpec_width_10px() {
        SpecWidth spec = (SpecWidth) readSpec("width: 10px");
        assertThat(spec.getRange(), is(Range.exact(10)));
        assertThat(spec.getOriginalText(), is("width: 10px"));
    }
    
    @Test
    public void shouldReadSpec_width_5_plus_minus_3px() {
        SpecWidth spec = (SpecWidth) readSpec("width: 5 ± 3px");
        assertThat(spec.getRange(), is(Range.between(2, 8)));
        assertThat(spec.getOriginalText(), is("width: 5 ± 3px"));
    }
    
    @Test
    public void shouldReadSpec_width_5_to_8px() {
        SpecWidth spec = (SpecWidth) readSpec("width: 5 to 8px");
        assertThat(spec.getRange(), is(Range.between(5, 8)));
        assertThat(spec.getOriginalText(), is("width: 5 to 8px"));
    }
    
    @Test
    public void shouldReadSpec_height_10px() {
        SpecHeight spec = (SpecHeight) readSpec("height: 10px");
        assertThat(spec.getRange(), is(Range.exact(10)));
        assertThat(spec.getOriginalText(), is("height: 10px"));
    }
    
    @Test
    public void shouldReadSpec_height_5_to_8px() {
        SpecHeight spec = (SpecHeight) readSpec("height: 5 to 8px");
        assertThat(spec.getRange(), is(Range.between(5, 8)));
        assertThat(spec.getOriginalText(), is("height: 5 to 8px"));
    }
    
    @Test(expectedExceptions={NullPointerException.class}, expectedExceptionsMessageRegExp="Spec text should not be null") 
    public void givesError_whenTextIsNull() {
        readSpec(null);
    }
    
    @Test(expectedExceptions={SyntaxException.class}, expectedExceptionsMessageRegExp="Spec text should not be empty") 
    public void givesError_whenTextIsEmpty() {
        readSpec(" ");
    }
    
    @Test(expectedExceptions={SyntaxException.class}, expectedExceptionsMessageRegExp="Incorrect format") 
    public void givesError_whenUsingMoreThanOneColon() {
        readSpec(" asfasf:asf :asf");
    }
    
    private Spec readSpec(String specText) {
        return new SpecReader().read(specText);
    }
    
    private List<Side> sides(Side...sides) {
        return Arrays.asList(sides);
    }

}
