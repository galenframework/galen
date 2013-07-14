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
package net.mindengine.galen.validation;

import net.mindengine.galen.page.Page;
import net.mindengine.galen.page.PageElement;
import net.mindengine.galen.page.Rect;
import net.mindengine.galen.specs.Spec;
import net.mindengine.galen.specs.reader.page.PageSpec;

public class PageValidation {

    private Page page;
    private PageSpec pageSpec;

    public PageValidation(Page page, PageSpec pageSpec) {
        this.setPage(page);
        this.setPageSpec(pageSpec);
    }

    public Page getPage() {
        return page;
    }

    public void setPage(Page page) {
        this.page = page;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    public ValidationError check(String objectName, Spec spec) {
        SpecValidation specValidation = ValidationFactory.getValidation(spec, this);
        return specValidation.check(objectName, spec);
    }

    public PageSpec getPageSpec() {
        return pageSpec;
    }

    public void setPageSpec(PageSpec pageSpec) {
        this.pageSpec = pageSpec;
    }

    public Rect getObjectArea(String objectName) {
        PageElement pageElement = page.getObject(objectName);
        if (pageElement != null) {
            return pageElement.getArea();
        }
        else return null;
    }

}
