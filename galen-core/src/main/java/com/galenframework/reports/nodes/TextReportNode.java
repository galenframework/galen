/*******************************************************************************
* Copyright 2018 Ivan Shubin http://galenframework.com
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
package com.galenframework.reports.nodes;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.galenframework.reports.model.FileTempStorage;

import java.util.Date;
import java.util.List;

/**
 * Created by ishubin on 2015/02/15.
 */
public class TextReportNode extends TestReportNode {
    public TextReportNode(FileTempStorage fileTempStorage, String details) {
        super(fileTempStorage);
        setName(details);
    }

    @JsonIgnore
    @Override
    public Status getStatus() {
        return super.getStatus();
    }

    @JsonIgnore
    @Override
    public Date getTime() {
        return super.getTime();
    }

    @JsonIgnore
    @Override
    public List<TestReportNode> getNodes() {
        return super.getNodes();
    }

    @Override
    public String getType() {
        return "text";
    }
}
