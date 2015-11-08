/*******************************************************************************
* Copyright 2015 Ivan Shubin http://galenframework.com
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
package com.galenframework.api;

import com.fasterxml.jackson.annotation.JsonIgnore;

import java.util.HashMap;
import java.util.Map;

public class PageDump {
    private String title;
    private Map<String, Element> items = new HashMap<String, Element>();
    private String pageName;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Map<String, Element> getItems() {
        return items;
    }

    public void setItems(Map<String, Element> items) {
        this.items = items;
    }

    public void setPageName(String pageName) {
        this.pageName = pageName;
    }

    public String getPageName() {
        return pageName;
    }

    public static class Element {

        @JsonIgnore
        private String objectName;
        private int[] area;
        private boolean hasImage = false;

        public Element(String objectName, int[] area) {
            setObjectName(objectName);
            setArea(area);
        }

        public void setObjectName(String objectName) {
            this.objectName = objectName;
        }

        public String getObjectName() {
            return objectName;
        }

        public void setArea(int[] area) {
            this.area = area;
        }

        public int[] getArea() {
            return area;
        }

        public void setHasImage(boolean hasImage) {
            this.hasImage = hasImage;
        }

        public boolean getHasImage() {
            return hasImage;
        }
    }
    public void addElement(Element element) {
        items.put(element.getObjectName(), element);
    }
}
