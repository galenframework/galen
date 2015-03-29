/*******************************************************************************
* Copyright 2015 Ivan Shubin http://mindengine.net
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
package net.mindengine.galen.suite.actions;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.List;

import net.mindengine.galen.browser.Browser;
import net.mindengine.galen.reports.TestReport;
import net.mindengine.galen.suite.GalenPageAction;
import net.mindengine.galen.suite.GalenPageTest;
import net.mindengine.galen.tests.TestSession;
import net.mindengine.galen.validation.ValidationListener;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class GalenPageActionProperties extends GalenPageAction {

    private List<String> files;

    @Override
    public void execute(TestReport report, Browser browser, GalenPageTest pageTest, ValidationListener validationListener) throws Exception {
        if (files != null) {
            for (String filePath: files) {
                File file = new File(filePath);
                if (!file.exists()) {
                    throw new FileNotFoundException("File does not exist: " + filePath);
                }
                else if (!file.isFile()) {
                    throw new FileNotFoundException("Not a file: " + filePath);
                }
                TestSession.current().getProperties().load(new FileReader(file));
            }
        }
    }

    public void setFiles(List<String> files) {
        this.files = files;
    }
    
    public List<String> getFiles() {
        return this.files;
    }

    public GalenPageActionProperties withFiles(List<String> files) {
        setFiles(files);
        return this;
    }

    
    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(files).toHashCode();
    }
    
    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof GalenPageActionProperties))
            return false;
        
        GalenPageActionProperties rhs = (GalenPageActionProperties)obj;
        
        return new EqualsBuilder() //@formatter:off
            .append(this.files, rhs.files)
            .isEquals(); //@formatter:on
        
    }
    
    @Override
    public String toString() { //@formatter:off
        return new ToStringBuilder(this)
        .append("files", files)
        .toString(); //@formatter:on
    }
}
