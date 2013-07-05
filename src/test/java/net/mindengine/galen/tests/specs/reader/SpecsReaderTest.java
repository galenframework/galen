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
import net.mindengine.galen.specs.reader.IncorrectSpecException;
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
    }
    

    @Test
    public void shouldReadSpec_inside_object_10_to_30px_left() {
        Spec spec = readSpec("inside: object 10 to 30px left");
        SpecInside specInside = (SpecInside) spec;

        assertThat(specInside.getObject(), is("object"));
        
        List<Location> locations = specInside.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(specInside.getLocations(), contains(new Location(Range.between(10, 30), sides(LEFT))));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(TOP, LEFT))));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left_comma_10_to_20px_bottom() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left, 10 to 20px bottom");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(2));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(TOP, LEFT)),
                new Location(Range.between(10, 20), sides(BOTTOM))));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_bottom_right() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px bottom right");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25),sides(BOTTOM, RIGHT))));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left_right_bottom() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left right bottom ");
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.exact(25), sides(TOP, LEFT, RIGHT, BOTTOM))));
    }
    
    @Test
    public void shouldReadSpec_contains() {
        Spec spec = readSpec("contains: object, menu, button");
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test 
    public void shouldReadSpec_near_button_10_to_20px_left() {
        SpecNear spec = (SpecNear) readSpec("near: button 10 to 20px left");
        
        assertThat(spec.getObject(), is("button"));
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(10, 20), sides(LEFT))));
    }
    
    @Test 
    public void shouldReadSpec_near_button_10_to_20px_top_right() {
        SpecNear spec = (SpecNear) readSpec("near: button 10 to 20px top right");
        assertThat(spec.getObject(), is("button"));
        
        List<Location> locations = spec.getLocations();
        assertThat(locations.size(), is(1));
        assertThat(spec.getLocations(), contains(new Location(Range.between(10, 20), sides(TOP, RIGHT))));
    }
    
    @Test
    public void shouldReadSpec_horizontally_centered() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally centered: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.CENTERED));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally_top() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally top: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.TOP));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally_bottom() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally bottom: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.BOTTOM));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_centered() {
        SpecVertically spec = (SpecVertically) readSpec("vertically  centered: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.CENTERED));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_left() {
        SpecVertically spec = (SpecVertically) readSpec("vertically left: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.LEFT));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_right() {
        SpecVertically spec = (SpecVertically) readSpec("vertically right: object, menu, button");
        assertThat(spec.getAlignment(), is(Alignment.RIGHT));
        assertThat(spec.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_absent() {
        Spec spec = readSpec("absent");
        assertThat(spec, Matchers.instanceOf(SpecAbsent.class));
    }
    
    @Test
    public void shouldReadSpec_width_10px() {
        SpecWidth spec = (SpecWidth) readSpec("width: 10px");
        assertThat(spec.getRange(), is(Range.exact(10)));
    }
    
    @Test
    public void shouldReadSpec_width_5_plus_minus_3px() {
        SpecWidth spec = (SpecWidth) readSpec("width: 5 ± 3px");
        assertThat(spec.getRange(), is(Range.between(2, 8)));
    }
    
    @Test
    public void shouldReadSpec_width_5_to_8px() {
        SpecWidth spec = (SpecWidth) readSpec("width: 5 to 8px");
        assertThat(spec.getRange(), is(Range.between(5, 8)));
    }
    
    @Test
    public void shouldReadSpec_height_10px() {
        SpecHeight spec = (SpecHeight) readSpec("height: 10px");
        assertThat(spec.getRange(), is(Range.exact(10)));
    }
    
    @Test
    public void shouldReadSpec_height_5_to_8px() {
        SpecHeight spec = (SpecHeight) readSpec("height: 5 to 8px");
        assertThat(spec.getRange(), is(Range.between(5, 8)));
    }
    
    @Test(expectedExceptions={NullPointerException.class}, expectedExceptionsMessageRegExp="Spec text should not be null") 
    public void givesError_whenTextIsNull() {
        readSpec(null);
    }
    
    @Test(expectedExceptions={IncorrectSpecException.class}, expectedExceptionsMessageRegExp="Spec text should not be empty") 
    public void givesError_whenTextIsEmpty() {
        readSpec(" ");
    }
    
    @Test(expectedExceptions={IncorrectSpecException.class}, expectedExceptionsMessageRegExp="Incorrect format") 
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
