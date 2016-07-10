/*******************************************************************************
* Copyright 2016 Ivan Shubin http://galenframework.com
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
package com.galenframework.parser;

import com.galenframework.suite.reader.Line;
import org.apache.commons.lang3.builder.EqualsBuilder;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ToStringBuilder;

import java.util.LinkedList;
import java.util.List;

public class StructNode {
    public static final StructNode UNKNOWN_SOURCE = new StructNode();
    private String name;
    private List<StructNode> childNodes;
    private Line line;

    public StructNode(String name) {
        this.name = name;
    }

    public StructNode() {
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setChildNodes(List<StructNode> childNodes) {
        this.childNodes = childNodes;
    }

    public List<StructNode> getChildNodes() {
        return childNodes;
    }

    public void addChildNode(StructNode childNode) {
        if (childNodes == null) {
            childNodes = new LinkedList<>();
        }

        childNodes.add(childNode);
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (obj == this) {
            return true;
        }
        if (!(obj instanceof StructNode)) {
            return false;
        }
        StructNode rhs = (StructNode)obj;
        return new EqualsBuilder()
                .append(this.name, rhs.name)
                .append(this.childNodes, rhs.childNodes)
                .append(this.line, rhs.line)
                .isEquals();
    }

    @Override
    public int hashCode() {
        return new HashCodeBuilder()
                .append(name)
                .append(childNodes)
                .append(line)
                .toHashCode();
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this)
                .append("name", name)
                .append("childNodes", childNodes)
                .append("line", line)
                .toString();
    }

    public boolean hasChildNodes() {
        return childNodes != null &&  !childNodes.isEmpty();
    }

    public String assembleAllNodes() {
        return assembleAllNodes("");
    }

    private String assembleAllNodes(String indentation) {
        StringBuilder builder = new StringBuilder();
        builder.append(indentation);
        builder.append(name);
        builder.append('\n');

        if (childNodes != null) {
            for (StructNode childNode : childNodes) {
                builder.append(childNode.assembleAllNodes(indentation + "    "));
            }
        }
        return builder.toString();
    }

    public String assembleAllChildNodes() {
        StringBuilder builder = new StringBuilder();
        if (childNodes != null) {
            for (StructNode childNode : childNodes) {
                builder.append(childNode.assembleAllNodes());
            }
        }
        return builder.toString();
    }

    public Line getLine() {
        return line;
    }

    public void setLine(Line line) {
        this.line = line;
    }
}
