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

import static java.util.Arrays.asList;
import static net.mindengine.galen.specs.Location.locations;
import static net.mindengine.galen.specs.Side.LEFT;
import static net.mindengine.galen.specs.Side.RIGHT;
import static net.mindengine.galen.specs.Side.TOP;
import static net.mindengine.galen.specs.Side.sides;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.hasEntry;
import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.notNullValue;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;

import net.mindengine.galen.parser.FileSyntaxException;
import net.mindengine.galen.specs.Location;
import net.mindengine.galen.specs.Range;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.SpecAbsent;
import net.mindengine.galen.specs.SpecInside;
import net.mindengine.galen.specs.SpecNear;
import net.mindengine.galen.specs.SpecWidth;
import net.mindengine.galen.specs.page.Locator;
import net.mindengine.galen.specs.page.ObjectSpecs;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.specs.reader.page.PageSpec;
import net.mindengine.galen.specs.reader.page.PageSpecReader;

import org.hamcrest.Matchers;
import org.testng.annotations.Test;

public class PageSpecsReaderTest {
    
    private static final String BASE_TEST = "shouldLoadSpecSuccessfully";
    PageSpecReader pageSpecReader = new PageSpecReader();
    PageSpec pageSpec;
    
    @Test
    public void shouldBePossible_toReadSpec_fromInputStream() throws IOException {
        PageSpec pageSpec = new PageSpecReader().read(getClass().getResourceAsStream("/specs.txt"));
        assertThat(pageSpec, is(notNullValue()));
    }
    
    @Test
    public void shouldBePossible_toReadSpec_fromFile() throws IOException {
        PageSpec pageSpec = new PageSpecReader().read(new File(getClass().getResource("/specs.txt").getFile()));
        assertThat(pageSpec, is(notNullValue()));
    }
    
    @Test
    public void shouldLoadSpecSuccessfully() throws IOException {
        pageSpec = pageSpecReader.read(new File(getClass().getResource("/specs.txt").getFile()));
        assertThat(pageSpec, is(notNullValue()));
    }
    
    @Test(dependsOnMethods = BASE_TEST)
    public void shouldRead_objectDefinitions() {
        Map<String, Locator> objects = pageSpec.getObjects();
        assertThat("Amount of objects should be", objects.size(), is(5));
        assertThat(objects, hasEntry("submit", new Locator("xpath", "//input[@name = 'submit']"))); 
        assertThat(objects, hasEntry("search-field", new Locator("css", "#search")));
        assertThat(objects, hasEntry("menu", new Locator("id", "menu")));
        assertThat(objects, hasEntry("big-box", new Locator("tag", "container")));
        assertThat(objects, hasEntry("anotherObject", new Locator("xpath", "//div")));
        
        assertThat("Amount of multi-objects should be", pageSpec.getMultiObjects().size(), is(1));
        assertThat(pageSpec.getMultiObjects(), hasEntry("menu-item-*", new Locator("css", "#menu .menu-item")));
    }
    
    
    @Test(dependsOnMethods = BASE_TEST)
    public void shouldRead_allSpecs_asSections_markedByTags() {
        List<PageSection> sections = pageSpec.getSections();

        assertThat("Amount of sections should be", sections.size(), is(6));
        assertThat(sections.get(0).getTags(), hasSize(0));
        
        assertThat(sections.get(1).getTags(), hasSize(2));
        assertThat(sections.get(1).getTags(), contains("tablet", "desktop"));
        
        assertThat(sections.get(2).getTags(), hasSize(0));
        
        assertThat(sections.get(3).getTags(), hasSize(1));
        assertThat(sections.get(3).getTags(), contains("mobile"));
        
        assertThat(sections.get(4).getTags(), hasSize(1));
        assertThat(sections.get(4).getTags(), contains("parameterized"));
        
        assertThat(sections.get(5).getTags(), hasSize(1));
        assertThat(sections.get(5).getTags(), contains("parameterized2"));
    }
    
    @Test(dependsOnMethods = BASE_TEST)
    public void shouldRead_parameterizedSpecs() {
        List<PageSection> sections = pageSpec.findSections(asList("parameterized"));
        assertThat(sections.size(), is(1));
        
        PageSection section = sections.get(0);
        
        List<ObjectSpecs> objects = section.getObjects();
        
        assertThat(objects.size(), is(6));
        
        for (int index = 1; index <=6; index++) {
            ObjectSpecs objectSpecs = objects.get(index - 1);
            assertThat(objectSpecs.getObjectName(), is("box-" + index + "-link"));
            SpecInside spec = (SpecInside) objectSpecs.getSpecs().get(0);
            assertThat(spec.getObject(), is("box-" + index));
        }
    }
    
    @Test(dependsOnMethods = BASE_TEST)
    public void shouldRead_parameterizedSpecs_2() {
        List<PageSection> sections = pageSpec.findSections(asList("parameterized2"));
        assertThat(sections.size(), is(1));
        
        PageSection section = sections.get(0);
        
        List<ObjectSpecs> objects = section.getObjects();
        
        assertThat(objects.size(), is(6));
        
        //[ 1, 3, 6-8, 10]
        int[] indexes = {1, 3, 6, 7, 8, 10};
        
        for (int index = 0; index <6; index++) {
            ObjectSpecs objectSpecs = objects.get(index);
            int number = indexes[index];
            assertThat("Object name #" + index, objectSpecs.getObjectName(), is("box-" + number + "-link"));
            SpecInside spec = (SpecInside) objectSpecs.getSpecs().get(0);
            assertThat(spec.getObject(), is("box-" + number));
        }
    }
    
    @Test(dependsOnMethods = BASE_TEST)
    public void shouldRead_allSpecs_withinFirstSection() {
        List<ObjectSpecs> objects = pageSpec.getSections().get(0).getObjects();
        assertThat(objects.size(), is(1));
        
        ObjectSpecs objectSpecs = objects.get(0);
        assertThat(objectSpecs.getObjectName(), is("menu"));
        
        List<Spec> specs = objectSpecs.getSpecs();
        assertThat(specs.size(), is(2));
        
        assertThat((SpecNear) specs.get(0), is(new SpecNear("button", locations(new Location(Range.exact(10), sides(LEFT))))));
        assertThat((SpecWidth) specs.get(1), is(new SpecWidth(Range.exact(70))));
    }

    
    @Test(dependsOnMethods = BASE_TEST)
    public void shouldRead_allSpecs_withinSecondSection() {
        List<ObjectSpecs> objects = pageSpec.getSections().get(1).getObjects();
        assertThat(objects.size(), is(2));
        
        assertThat(objects.get(0).getObjectName(), is("submit"));
        assertThat(objects.get(0).getSpecs().size(), is(2));
        assertThat((SpecInside) objects.get(0).getSpecs().get(0), is(new SpecInside("big-box", 
                locations(
                        new Location(Range.between(10, 30), sides(RIGHT)),
                        new Location(Range.between(20, 40), sides(TOP))
        ))));
        
        assertThat((SpecNear) objects.get(0).getSpecs().get(1), is(new SpecNear("menu", locations(new Location(Range.exact(20), sides(LEFT))))));
        
        assertThat(objects.get(1).getObjectName(), is("search-field"));
        assertThat(objects.get(1).getSpecs().size(), is(1));
        assertThat((SpecInside) objects.get(1).getSpecs().get(0), is(new SpecInside("big-box", locations(new Location(Range.exact(30), sides(RIGHT))))));
    }
    
    @Test(dependsOnMethods = BASE_TEST)
    public void shouldRead_allSpecs_withinThirdSection() {
        List<ObjectSpecs> objects = pageSpec.getSections().get(2).getObjects();
        assertThat(objects.size(), is(1));
        
        ObjectSpecs objectSpecs = objects.get(0);
        assertThat(objectSpecs.getObjectName(), is("big-box"));
        
        List<Spec> specs = objectSpecs.getSpecs();
        assertThat(specs.size(), is(1));
        
        assertThat((SpecWidth) specs.get(0), is(new SpecWidth(Range.exact(900))));
    }
    
    @Test(dependsOnMethods = BASE_TEST)
    public void shouldRead_allSpecs_withinFourthSection() {
        List<ObjectSpecs> objects = pageSpec.getSections().get(3).getObjects();
        assertThat(objects.size(), is(1));
        
        ObjectSpecs objectSpecs = objects.get(0);
        assertThat(objectSpecs.getObjectName(), is("submit"));
        
        List<Spec> specs = objectSpecs.getSpecs();
        assertThat(specs.size(), is(1));
        
        assertThat((SpecAbsent) specs.get(0), is(new SpecAbsent()));
    }
    
    
    @Test
    public void givesError_ifThereAreSpecs_withNoObjectSpecified_inSection() throws IOException {
        FileSyntaxException exception = expectExceptionFromReading("/negative-specs/no-object-in-section.spec");
        
        String fullSpecPath = getClass().getResource("/negative-specs/no-object-in-section.spec").getFile();
        
        assertThat(exception.getMessage(), is("There is no object defined in section (in " + fullSpecPath + ":8)"));
        assertThat(exception.getFilePath(), endsWith("/no-object-in-section.spec"));
        assertThat(exception.getLine(), is(8));
    }
    
    @Test
    public void givesError_ifThereAre_invalidSpecs() throws IOException {
        FileSyntaxException exception = expectExceptionFromReading("/negative-specs/invalid-spec.spec");
        String fullSpecPath = getClass().getResource("/negative-specs/invalid-spec.spec").getFile();
        
        assertThat(exception.getMessage(), is("There is no location defined (in " + fullSpecPath + ":10)"));
        assertThat(exception.getFilePath(), endsWith("/invalid-spec.spec"));
        assertThat(exception.getLine(), is(10));
    }
    
    @Test 
    public void givesError_ifThereAre_invalidObjectLocators() throws Exception {
        FileSyntaxException exception = expectExceptionFromReading("/negative-specs/invalid-object-locator.spec");
        
        String fullSpecPath = getClass().getResource("/negative-specs/invalid-object-locator.spec").getFile();
        
        assertThat(exception.getMessage(), is("Missing locator for object \"bad-object\" (in " + fullSpecPath +":7)"));
        assertThat(exception.getFilePath(), endsWith("/invalid-object-locator.spec"));
        assertThat(exception.getLine(), is(7));
    }
    
    @Test
    public void shouldImport_otherSpecs_fromOtherFiles() throws Exception {
    	PageSpec pageSpec = pageSpecReader.read(new File(getClass().getResource("/spec-import-test/main.spec").getFile()));
    	
    	assertThat(pageSpec.getObjects(), hasKey("content"));
    	assertThat(pageSpec.getObjects(), hasKey("header"));
    	assertThat(pageSpec.getObjects(), hasKey("header-text"));
    	
    	
    	List<PageSection> sections = pageSpec.getSections();
    	
    	assertThat(sections.size(), is(2));
    	
    	assertThat(sections.get(0).getTags(), contains("all"));
    	
    	List<ObjectSpecs> objects = sections.get(0).getObjects();
    	assertThat(objects.size(), is(2));
    	assertThat(objects.get(0).getObjectName(), is("header"));
    	assertThat(objects.get(1).getObjectName(), is("header-text"));
    	
    	assertThat(sections.get(1).getTags(), contains("all"));
    	objects = sections.get(1).getObjects();
    	assertThat(objects.size(), is(1));
    	assertThat(objects.get(0).getObjectName(), is("content"));
    	
    	//TODO check recursive import doesn't give error
    }

    private FileSyntaxException expectExceptionFromReading(String file) throws IOException {
        try {
            pageSpecReader.read(new File(getClass().getResource(file).getFile()));
        }
        catch(FileSyntaxException exception) {
            return exception;
        }
        throw new RuntimeException("Expected exception was not caught when reading page spec: " + file);
    }  
}
