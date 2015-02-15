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
package net.mindengine.galen.reports;

import net.mindengine.galen.reports.nodes.TestReportNode;
import org.apache.commons.lang3.exception.ExceptionUtils;

public class ExceptionReportNode extends TestReportNode {

    private Throwable exception;

    public ExceptionReportNode(Throwable exception) {
        this.exception = exception;
        this.setStatus(TestReportNode.Status.ERROR);
    }
    
    @Override
    public String getName() {
        return ExceptionUtils.getMessage(exception);
    }
    
    public String getStacktrace() {
        return ExceptionUtils.getStackTrace(exception);
    }

}
