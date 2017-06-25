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
package com.galenframework.generator.raycast;

import com.galenframework.generator.PageItemNode;
import com.galenframework.generator.math.Point;

import java.util.LinkedList;
import java.util.List;

public class EdgesContainer {
    public EdgesContainer(List<Edge> rightEdges, List<Edge> leftEdges, List<Edge> bottomEdges, List<Edge> topEdges) {
        this.rightEdges = rightEdges;
        this.leftEdges = leftEdges;
        this.bottomEdges = bottomEdges;
        this.topEdges = topEdges;
    }

    public List<Edge> getRightEdges() {
        return rightEdges;
    }

    public List<Edge> getLeftEdges() {
        return leftEdges;
    }

    public List<Edge> getBottomEdges() {
        return bottomEdges;
    }

    public List<Edge> getTopEdges() {
        return topEdges;
    }

    public static class Edge {
        public PageItemNode itemNode;
        public final boolean isParent;
        public final Point p1;
        public final Point p2;

        public Edge(PageItemNode itemNode, Point p1, Point p2, boolean isParent) {
            this.itemNode = itemNode;
            this.isParent = isParent;
            this.p1 = p1;
            this.p2 = p2;
        }
        public Edge(PageItemNode itemNode, Point p1, Point p2) {
            this(itemNode, p1, p2, false);
        }

        public boolean isInRightZoneOf(Edge edge) {
            if (p1.getLeft() >= edge.p1.getLeft()) {
                if (isInHorizontalZoneOf(edge)) return true;
            }
            return false;
        }

        public boolean isInLeftZoneOf(Edge edge) {
            if (p1.getLeft() <= edge.p1.getLeft()) {
                if (isInHorizontalZoneOf(edge)) return true;
            }
            return false;
        }

        private boolean isInHorizontalZoneOf(Edge edge) {
            int zone1 = identifyHorizontalZoneId(p1.getTop(), edge);
            int zone2 = identifyHorizontalZoneId(p2.getTop(), edge);
            if (zone1 != zone2 || zone1 == 0) {
                return true;
            }
            return false;
        }

        private int identifyHorizontalZoneId(int top, Edge edge) {
            if (top <= edge.p1.getTop()) {
                return -1;
            } else if (top >= edge.p2.getTop()) {
                return 1;
            } else {
                return 0;
            }
        }

        public boolean isInBottomZoneOf(Edge edge) {
            if (p1.getTop() >= edge.p1.getTop()) {
                if (isInVerticalZoneOf(edge)) return true;
            }
            return false;
        }

        public boolean isInTopZoneOf(Edge edge) {
            if (p1.getTop() <= edge.p1.getTop()) {
                if (isInVerticalZoneOf(edge)) return true;
            }
            return false;
        }

        private boolean isInVerticalZoneOf(Edge edge) {
            int zone1 = identifyVerticalZoneId(p1.getLeft(), edge);
            int zone2 = identifyVerticalZoneId(p2.getLeft(), edge);
            if (zone1 != zone2 || zone1 == 0) {
                return true;
            }
            return false;
        }

        private int identifyVerticalZoneId(int left, Edge edge) {
            if (left <= edge.p1.getLeft()) {
                return -1;
            } else if (left >= edge.p2.getLeft()) {
                return 1;
            } else {
                return 0;
            }
        }
    }

    private final List<Edge> rightEdges;
    private final List<Edge> leftEdges;
    private final List<Edge> bottomEdges;
    private final List<Edge> topEdges;

    public static EdgesContainer create(PageItemNode parent, List<PageItemNode> pins) {
        List<Edge> rightEdges = new LinkedList<>();
        List<Edge> leftEdges = new LinkedList<>();
        List<Edge> bottomEdges = new LinkedList<>();
        List<Edge> topEdges = new LinkedList<>();

        Point[] parentPoints = parent.getPageItem().getArea().getPoints();

        rightEdges.add(new Edge(parent, parentPoints[1], parentPoints[2], true));
        leftEdges.add(new Edge(parent, parentPoints[0], parentPoints[3], true));
        topEdges.add(new Edge(parent, parentPoints[0], parentPoints[1], true));
        bottomEdges.add(new Edge(parent, parentPoints[3], parentPoints[2], true));


        for (PageItemNode pin : pins) {
            Point[] p = pin.getPageItem().getArea().getPoints();

            rightEdges.add(new Edge(pin, p[0], p[3], true));
            leftEdges.add(new Edge(pin, p[1], p[2], true));
            topEdges.add(new Edge(pin, p[3], p[2], true));
            bottomEdges.add(new Edge(pin, p[0], p[1], true));
        }

        return new EdgesContainer(rightEdges, leftEdges, bottomEdges, topEdges);
    }

}
