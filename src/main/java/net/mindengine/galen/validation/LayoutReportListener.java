/*******************************************************************************
* Copyright 2014 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.validation;

import java.util.HashMap;
import java.util.Map;

import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.reports.model.LayoutObject;
import net.mindengine.galen.reports.model.LayoutReport;
import net.mindengine.galen.reports.model.LayoutSection;
import net.mindengine.galen.reports.model.LayoutSpec;
import net.mindengine.galen.runner.GalenPageRunner;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.page.PageSection;
import net.mindengine.galen.suite.GalenPageAction;

public class LayoutReportListener implements ValidationListener {

    private LayoutObject currentObject;
    
    //This is needed in order to group objects by name within single page section
    //After each section this map should be cleared
    private Map<String, LayoutObject> cachedPageTestObjectsMap = new HashMap<String, LayoutObject>();
    private Map<String, LayoutSection> cachedSections = new HashMap<String, LayoutSection>();
    
    private LayoutReport layoutReport;
    private LayoutSection currentSection;
    
    
    public LayoutReportListener(LayoutReport layoutReport) {
        this.layoutReport = layoutReport;
    }

    @Override
    public void onObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        if (currentObject == null) {
            if (cachedPageTestObjectsMap.containsKey(objectName)) {
                currentObject = cachedPageTestObjectsMap.get(objectName);
            }
            else {
                currentObject = new LayoutObject();
                currentObject.setName(objectName);
                
                PageElement objectPageElement = pageValidation.findPageElement(objectName);
                if (objectPageElement != null && objectPageElement.isVisible()) {
                    currentObject.setArea(objectPageElement.getArea());
                }
                
                currentSection.getObjects().add(currentObject);
                cachedPageTestObjectsMap.put(objectName, currentObject);
            }
        }
        else {
            LayoutObject parentObject = currentObject;
            currentObject = new LayoutObject(parentObject);
            currentObject.setName(objectName);
            
            PageElement objectPageElement = pageValidation.findPageElement(objectName);
            if (objectPageElement != null && objectPageElement.isVisible()) {
                currentObject.setArea(objectPageElement.getArea());
            }
        }
    }

    @Override
    public void onAfterObject(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName) {
        if (currentObject.getParent() != null) {
            currentObject = currentObject.getParent();
        }
        else {
            currentObject = null;
        }
    }

    @Override
    public void onSpecError(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec originalSpec, ValidationError error) {

        LayoutSpec spec = new LayoutSpec();
        currentObject.getSpecs().add(spec);
        
        spec.setText(originalSpec.getOriginalText());
        spec.setFailed(true);
        
        spec.setErrorMessages(error.getMessages());
        spec.setErrorAreas(error.getErrorAreas());
        
        pickSubObjectsForSpec(spec);
    }

    @Override
    public void onSpecSuccess(GalenPageRunner pageRunner, PageValidation pageValidation, String objectName, Spec originalSpec) {
        LayoutSpec spec = new LayoutSpec();
        currentObject.getSpecs().add(spec);
        spec.setText(originalSpec.getOriginalText());
        spec.setFailed(false);
        
        pickSubObjectsForSpec(spec);
    }
    
    private void pickSubObjectsForSpec(LayoutSpec spec) {
        if (currentObject.getSubObjects() != null) {
            spec.setSubObjects(currentObject.getSubObjects());
            currentObject.setSubObjects(null);
        }
    }

    @Override
    public void onGlobalError(GalenPageRunner pageRunner, Exception e) {
        
    }

    @Override
    public void onBeforePageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        
    }

    @Override
    public void onAfterPageAction(GalenPageRunner pageRunner, GalenPageAction action) {
        
    }

    /* Using section level counter so that in html reports only the higher level section gets shown 
     * */ 
    int sectionLevel = 0;
    
    @Override
    public void onBeforeSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        sectionLevel++;
        
        if (sectionLevel <= 1) {
            //Reseting objects map
            cachedPageTestObjectsMap = new HashMap<String, LayoutObject>();
            
            String name = pageSection.getName();
            if (name == null || name.trim().isEmpty()) {
                name = "Unnamed";
            }
            
            currentSection = createSection(name);
        }
    }


    private LayoutSection createSection(String name) {
        if (cachedSections.containsKey(name)) {
            return cachedSections.get(name);
        }
        else {
            LayoutSection section = new LayoutSection();
            section.setName(name);
            cachedSections.put(name, section);
            layoutReport.getSections().add(section);
            return section;
        }
    }


    @Override
    public void onAfterSection(GalenPageRunner pageRunner, PageValidation pageValidation, PageSection pageSection) {
        sectionLevel--;
    }

}
