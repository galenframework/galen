/*******************************************************************************
* Copyright 2017 Ivan Shubin http://galenframework.com
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
package com.galenframework.specs;

import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

public class Place {

    private String filePath;
    private int lineNumber;

    public Place(String filePath, int lineNumber) {
        this.setFilePath(filePath);
        this.setLineNumber(lineNumber);
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public int getLineNumber() {
        return lineNumber;
    }

    public void setLineNumber(int lineNumber) {
        this.lineNumber = lineNumber;
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
        .append(this.filePath)
        .append(this.lineNumber)
        .toHashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null)
            return false;
        if (obj == this)
            return true;
        if (!(obj instanceof Place))
            return false;

        Place rhs = (Place)obj;
        return new EqualsBuilder().append(filePath, rhs.filePath).append(lineNumber, rhs.lineNumber).isEquals();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
        .append("filePath", filePath)
        .append("lineNumber", lineNumber)
        .toString();
    }

    public String toPrettyString() {
        return filePath + ":" + lineNumber;
    }

    public String toExceptionMessage() {
        return "in " + toPrettyString();
    }
}
