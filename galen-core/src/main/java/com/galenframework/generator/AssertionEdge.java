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
package com.galenframework.generator;

import com.galenframework.generator.raycast.EdgesContainer;

import static com.galenframework.generator.AssertionEdge.EdgeType.*;

public class AssertionEdge {
    public enum EdgeType {
        left, right, top, bottom
    }
    private String object;
    private EdgeType edgeType;

    public AssertionEdge(String object, EdgeType edgeType) {
        this.object = object;
        this.edgeType = edgeType;
    }

    public String getObject() {
        return object;
    }

    public void setObject(String object) {
        this.object = object;
    }

    public EdgeType getEdgeType() {
        return edgeType;
    }

    public void setEdgeType(EdgeType edgeType) {
        this.edgeType = edgeType;
    }

    public static AssertionEdge left(EdgesContainer.Edge edge) {
        return new AssertionEdge(edge.itemNode.getPageItem().getName(), left);
    }

    public static AssertionEdge right(EdgesContainer.Edge edge) {
        return new AssertionEdge(edge.itemNode.getPageItem().getName(), right);
    }

    public static AssertionEdge top(EdgesContainer.Edge edge) {
        return new AssertionEdge(edge.itemNode.getPageItem().getName(), top);
    }

    public static AssertionEdge bottom(EdgesContainer.Edge edge) {
        return new AssertionEdge(edge.itemNode.getPageItem().getName(), bottom);
    }

    public static AssertionEdge left(String name) {
        return new AssertionEdge(name, left);
    }

    public static AssertionEdge right(String name) {
        return new AssertionEdge(name, right);
    }

    public static AssertionEdge top(String name) {
        return new AssertionEdge(name, top);
    }

    public static AssertionEdge bottom(String name) {
        return new AssertionEdge(name, bottom);
    }
}
