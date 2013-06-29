package net.mindengine.galen.tests.specs;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

@Test
public class SpecsReaderTest {

    
    @Test
    public void shouldReadSpec_inside_object_10px_right() {
        Spec spec = readSpec("inside: object 10px right");
        
        SpecInside specInside = (SpecInside) spec;
        assertThat(specInside.getObject(), is("object"));
        assertThat(specInside.getRange(), is(Range.exact(10)));
        assertThat(specInside.getLocations(), contains(Constraints.Location.RIGHT));
    }
    
    @Test
    public void shouldReadSpec_inside_object_10_to_30px_left() {
        Spec spec = readSpec("inside: object 10~30px left");

        SpecInside specInside = (SpecInside) spec;
        assertThat(specInside.getObject(), is("object"));
        assertThat(specInside.getRange(), is(Range.between(10, 30)));
        assertThat(specInside.getLocations(), contains(Constraints.Location.LEFT));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left");
        assertThat(spec.getLocations(), contains(Constraints.Location.TOP, Constraints.Location.LEFT));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_bottom_right() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px bottom right");
        assertThat(spec.getLocations(), contains(Constraints.Location.BOTTOM, Constraints.Location.RIGHT));
    }
    
    @Test
    public void shouldReadSpec_inside_object_25px_top_left_right_bottom() {
        SpecInside spec = (SpecInside)readSpec("inside: object 25px top left");
        assertThat(spec.getLocations(), contains(Constraints.Location.TOP, Constraints.Location.LEFT, Constraints.Location.RIGHT, Constraints.Location.BOTTOM));
    }
    
    @Test
    public void shouldReadSpec_contains() {
        Spec spec = readSpec("contains: object, menu, button");
        
        SpecContains specContains = (SpecContains) spec;
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
    }
    
    
    @Test 
    public void shouldReadSpec_near_button_10_to_20px_left() {
        SpecNear spec = (SpecNeer) readSpec("near: button 10~20px left");
        
        assertThat(spec.getObject(), is("button"));
        assertThat(specInside.getRange(), is(Range.between(10, 20)));
        assertThat(specInside.getLocations(), contains(Constraints.Location.LEFT));
    }
    
    @Test 
    public void shouldReadSpec_near_button_10_to_20px_left() {
        SpecNear spec = (SpecNear) readSpec("near: button 10~20px top right");
        assertThat(spec.getObject(), is("button"));
        assertThat(specInside.getRange(), is(Range.between(10, 20)));
        assertThat(specInside.getLocations(), contains(Constraints.Location.TOP, Constraints.Location.RIGHT));
    }
    
    @Test
    public void shouldReadSpec_horizontally_centered() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally centered: object, menu, button");
        assertThat(spec.getAlignment(), is(Constraints.Alignment.CENTERED));
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally_top() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally top: object, menu, button");
        assertThat(spec.getAlignment(), is(Constraints.Alignment.TOP));
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_horizontally_bottom() {
        SpecHorizontally spec = (SpecHorizontally) readSpec("horizontally bottom: object, menu, button");
        assertThat(spec.getAlignment(), is(Constraints.Alignment.BOTTOM));
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_centered() {
        SpecVertically spec = (SpecVertically) readSpec("vertically centered: object, menu, button");
        assertThat(spec.getAlignment(), is(Constraints.Alignment.CENTERED));
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_left() {
        SpecVertically spec = (SpecVertically) readSpec("vertically left: object, menu, button");
        assertThat(spec.getAlignment(), is(Constraints.Alignment.LEFT));
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_vertically_right() {
        SpecVertically spec = (SpecVertically) readSpec("vertically right: object, menu, button");
        assertThat(spec.getAlignment(), is(Constraints.Alignment.RIGHT));
        assertThat(specContains.getChildObjects(), contains("object", "menu", "button"));
    }
    
    @Test
    public void shouldReadSpec_absent() {
        Spec spec = readSpec("absent");
        assetThat(spec, instanceOf(SpecAbsent.class));
    }
    
    @Test
    public void shouldReadSpec_width_10px() {
        SpecWidth spec = (SpecWidth) readSpec("width: 10px");
        assertThat(spec.getWidthRange(), is(Range.exact(10)));
    }
    
    @Test
    public void shouldReadSpec_width_5_to_8px() {
        SpecWidth spec = (SpecWidth) readSpec("width: 5~8px");
        assertThat(spec.getRange(), is(Range.between(5, 8)));
    }
    
    @Test
    public void shouldReadSpec_height_10px() {
        SpecHeight spec = (SpecHeight) readSpec("height: 10px");
        assertThat(spec.getRange(), is(Range.exact(10)));
    }
    
    @Test
    public void shouldReadSpec_height_5_to_8px() {
        SpecHeight spec = (SpecHeight) readSpec("height: 5~8px");
        assertThat(spec.getRange(), is(Range.between(5, 8)));
    }
    
    
    
    private Spec readSpec(String specText) {
        return new SpecReader().read(specText);
    }
}
